package com.rtchubs.restohubs.util

import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.Image.Plane
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.MediaStore.Images
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.exifinterface.media.ExifInterface
import com.rtchubs.restohubs.nid_scan.FrameMetadata
import java.io.*
import java.nio.ByteBuffer
import java.util.*


object BitmapUtilss {

    private const val TAG = "BitmapUtilss"

    /**
     * Converts NV21 format byte buffer to bitmap.
     */
    fun getBitmap(data: ByteBuffer, metadata: FrameMetadata): Bitmap? {
        data.rewind()
        val imageInBuffer = ByteArray(data.limit())
        data[imageInBuffer, 0, imageInBuffer.size]
        try {
            val image = YuvImage(
                imageInBuffer,
                ImageFormat.NV21,
                metadata.getWidth(),
                metadata.getHeight(),
                null
            )
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(
                Rect(
                    0,
                    0,
                    metadata.getWidth(),
                    metadata.getHeight()
                ), 80, stream
            )
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()
            return rotateBitmap(bmp, metadata.getRotation(), false, false)
        } catch (e: Exception) {
            Log.e("VisionProcessorBase", "Error: " + e.message)
        }
        return null
    }

    /**
     * Converts a YUV_420_888 image from CameraX API to a bitmap.
     */
    @RequiresApi(VERSION_CODES.KITKAT)
    @ExperimentalGetImage
    fun getBitmap(image: ImageProxy): Bitmap? {
        val frameMetadata: FrameMetadata = FrameMetadata.Builder()
            .setWidth(image.width)
            .setHeight(image.height)
            .setRotation(image.imageInfo.rotationDegrees)
            .build()
        val nv21Buffer = yuv420ThreePlanesToNV21(
            image.image!!.planes, image.width, image.height
        )
        return getBitmap(nv21Buffer, frameMetadata)
    }

    /**
     * Rotates a bitmap if it is converted from a bytebuffer.
     */
    private fun rotateBitmap(
        bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
    ): Bitmap? {
        val matrix = Matrix()

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees.toFloat())

        // Mirror the image along the X or Y axis.
        matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        return rotatedBitmap
    }

    fun getBitmapFromAsset(
        context: Context,
        fileName: String
    ): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e(TAG, "Error reading asset: $fileName", e)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close input stream: ", e)
                }
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun getBitmapFromContentUri(
        contentResolver: ContentResolver,
        imageUri: Uri
    ): Bitmap? {
        val decodedBitmap = Images.Media.getBitmap(contentResolver, imageUri) ?: return null
        val orientation = getExifOrientationTag(contentResolver, imageUri)
        var rotationDegrees = 0
        var flipX = false
        var flipY = false
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipX = true
            ExifInterface.ORIENTATION_ROTATE_90 -> rotationDegrees = 90
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                rotationDegrees = 90
                flipX = true
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> rotationDegrees =
                180
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipY = true
            ExifInterface.ORIENTATION_ROTATE_270 -> rotationDegrees =
                -90
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                rotationDegrees = -90
                flipX = true
            }
            ExifInterface.ORIENTATION_UNDEFINED, ExifInterface.ORIENTATION_NORMAL -> {
            }
            else -> {
            }
        }
        return rotateBitmap(decodedBitmap, rotationDegrees, flipX, flipY)
    }

    private fun getExifOrientationTag(
        resolver: ContentResolver,
        imageUri: Uri
    ): Int {
        // We only support parsing EXIF orientation tag from local file on the device.
        // See also:
        // https://android-developers.googleblog.com/2016/12/introducing-the-exifinterface-support-library.html
        if (ContentResolver.SCHEME_CONTENT != imageUri.scheme
            && ContentResolver.SCHEME_FILE != imageUri.scheme
        ) {
            return 0
        }
        var exif: ExifInterface? = null
        try {
            resolver.openInputStream(imageUri).use { inputStream ->
                if (inputStream == null) {
                    return 0
                }
                exif = ExifInterface(inputStream)
            }
        } catch (e: IOException) {
            Log.e(
                TAG,
                "failed to open file to read rotation meta data: $imageUri",
                e
            )
            return 0
        }
        return exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: 0
    }

    fun convertBitmapToNv21Buffer(bitmap: Bitmap): ByteBuffer? {
        return ByteBuffer.wrap(convertBitmapToNv21Bytes(bitmap))
    }

    fun convertBitmapToNv21Bytes(bitmap: Bitmap): ByteArray {
        val inputWidth = bitmap.width
        val inputHeight = bitmap.height
        val argb = IntArray(inputWidth * inputHeight)
        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
        val nv21Bytes = ByteArray(
            inputHeight * inputWidth
                    + 2 * Math.ceil(inputHeight / 2.0).toInt() * Math.ceil(
                inputWidth / 2.0
            ).toInt()
        )
        encodeToNv21(nv21Bytes, argb, inputWidth, inputHeight)
        return nv21Bytes
    }

    private fun encodeToNv21(
        nv21Bytes: ByteArray,
        argb: IntArray,
        width: Int,
        height: Int
    ) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var red: Int
        var green: Int
        var blue: Int
        var y: Int
        var u: Int
        var v: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {

                // first byte is alpha, but is unused
                red = argb[index] and 0xff0000 shr 16
                green = argb[index] and 0xff00 shr 8
                blue = argb[index] and 0xff shr 0

                // well known RGB to YUV algorithm
                y = (66 * red + 129 * green + 25 * blue + 128 shr 8) + 16
                u = (-38 * red - 74 * green + 112 * blue + 128 shr 8) + 128
                v = (112 * red - 94 * green - 18 * blue + 128 shr 8) + 128

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                // pixel AND every other scanline.
                nv21Bytes[yIndex++] =
                    (if (y < 0) 0 else if (y > 255) 255 else y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    nv21Bytes[uvIndex++] =
                        (if (v < 0) 0 else if (v > 255) 255 else v).toByte()
                    nv21Bytes[uvIndex++] =
                        (if (u < 0) 0 else if (u > 255) 255 else u).toByte()
                }
                index++
            }
        }
    }

    fun convertBitmapToYv12Buffer(bitmap: Bitmap): ByteBuffer? {
        return ByteBuffer.wrap(convertBitmapToYv12Bytes(bitmap))
    }

    fun convertBitmapToYv12Bytes(bitmap: Bitmap): ByteArray? {
        val nv21Bytes = convertBitmapToNv21Bytes(bitmap)
        return nv21Toyv12(nv21Bytes)
    }

    /**
     * Converts nv21 byte[] to yv12 byte[].
     *
     *
     * NV21 (4:2:0) Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y V U V U V U V U V U V U
     *
     *
     * YV12 (4:2:0) Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y Y V V V V V V U U U U U U
     */
    private fun nv21Toyv12(nv21Bytes: ByteArray): ByteArray? {
        val totalBytes = nv21Bytes.size
        val rowSize = totalBytes / 6 // 4+2+0
        val yv12Bytes = ByteArray(totalBytes)
        System.arraycopy(nv21Bytes, 0, yv12Bytes, 0, rowSize * 4)
        val offSet = totalBytes / 6 * 4
        for (i in 0 until rowSize) {
            yv12Bytes[offSet + i] = nv21Bytes[offSet + 2 * i] // V
            yv12Bytes[offSet + rowSize + i] = nv21Bytes[offSet + 2 * i + 1] // U
        }
        return yv12Bytes
    }

    /**
     * Converts YUV_420_888 to NV21 bytebuffer.
     *
     *
     * The NV21 format consists of a single byte array containing the Y, U and V values. For an
     * image of size S, the first S positions of the array contain all the Y values. The remaining
     * positions contain interleaved V and U values. U and V are subsampled by a factor of 2 in both
     * dimensions, so there are S/4 U values and S/4 V values. In summary, the NV21 array will contain
     * S Y values followed by S/4 VU values: YYYYYYYYYYYYYY(...)YVUVUVUVU(...)VU
     *
     *
     * YUV_420_888 is a generic format that can describe any YUV image where U and V are subsampled
     * by a factor of 2 in both dimensions. [Image.getPlanes] returns an array with the Y, U and
     * V planes. The Y plane is guaranteed not to be interleaved, so we can just copy its values into
     * the first part of the NV21 array. The U and V planes may already have the representation in the
     * NV21 format. This happens if the planes share the same buffer, the V buffer is one position
     * before the U buffer and the planes have a pixelStride of 2. If this is case, we can just copy
     * them to the NV21 array.
     */
    @RequiresApi(VERSION_CODES.KITKAT)
    private fun yuv420ThreePlanesToNV21(
        yuv420888planes: Array<Plane>, width: Int, height: Int
    ): ByteBuffer {
        val imageSize = width * height
        val out = ByteArray(imageSize + 2 * (imageSize / 4))
        if (areUVPlanesNV21(yuv420888planes, width, height)) {
            // Copy the Y values.
            yuv420888planes[0].buffer[out, 0, imageSize]
            val uBuffer = yuv420888planes[1].buffer
            val vBuffer = yuv420888planes[2].buffer
            // Get the first V value from the V buffer, since the U buffer does not contain it.
            vBuffer[out, imageSize, 1]
            // Copy the first U value and the remaining VU values from the U buffer.
            uBuffer[out, imageSize + 1, 2 * imageSize / 4 - 1]
        } else {
            // Fallback to copying the UV values one by one, which is slower but also works.
            // Unpack Y.
            unpackPlane(yuv420888planes[0], width, height, out, 0, 1)
            // Unpack U.
            unpackPlane(yuv420888planes[1], width, height, out, imageSize + 1, 2)
            // Unpack V.
            unpackPlane(yuv420888planes[2], width, height, out, imageSize, 2)
        }
        return ByteBuffer.wrap(out)
    }

    /**
     * Checks if the UV plane buffers of a YUV_420_888 image are in the NV21 format.
     */
    @RequiresApi(VERSION_CODES.KITKAT)
    private fun areUVPlanesNV21(
        planes: Array<Plane>,
        width: Int,
        height: Int
    ): Boolean {
        val imageSize = width * height
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        // Backup buffer properties.
        val vBufferPosition = vBuffer.position()
        val uBufferLimit = uBuffer.limit()

        // Advance the V buffer by 1 byte, since the U buffer will not contain the first V value.
        vBuffer.position(vBufferPosition + 1)
        // Chop off the last byte of the U buffer, since the V buffer will not contain the last U value.
        uBuffer.limit(uBufferLimit - 1)

        // Check that the buffers are equal and have the expected number of elements.
        val areNV21 =
            vBuffer.remaining() == 2 * imageSize / 4 - 2 && vBuffer.compareTo(uBuffer) == 0

        // Restore buffers to their initial state.
        vBuffer.position(vBufferPosition)
        uBuffer.limit(uBufferLimit)
        return areNV21
    }

    /**
     * Unpack an image plane into a byte array.
     *
     *
     * The input plane data will be copied in 'out', starting at 'offset' and every pixel will be
     * spaced by 'pixelStride'. Note that there is no row padding on the output.
     */
    @TargetApi(VERSION_CODES.KITKAT)
    private fun unpackPlane(
        plane: Plane, width: Int, height: Int, out: ByteArray, offset: Int, pixelStride: Int
    ) {
        val buffer = plane.buffer
        buffer.rewind()

        // Compute the size of the current plane.
        // We assume that it has the aspect ratio as the original image.
        val numRow = (buffer.limit() + plane.rowStride - 1) / plane.rowStride
        if (numRow == 0) {
            return
        }
        val scaleFactor = height / numRow
        val numCol = width / scaleFactor

        // Extract the data in the output buffer.
        var outputPos = offset
        var rowStart = 0
        for (row in 0 until numRow) {
            var inputPos = rowStart
            for (col in 0 until numCol) {
                out[outputPos] = buffer[inputPos]
                outputPos += pixelStride
                inputPos += plane.pixelStride
            }
            rowStart += plane.rowStride
        }
    }


    fun getResizedBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width / height.toFloat()
            val ratioMax = maxWidth / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        } else {

            return image
        }
    }

    /**
     * returns the bytesize of the give bitmap
     */
    fun byteSizeOf(bitmap: Bitmap): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmap.allocationByteCount
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmap.byteCount
        } else {
            bitmap.rowBytes * bitmap.height
        }
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun combineImagesHorizontally(
        first: Bitmap,
        second: Bitmap
    ): Bitmap { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        var cs: Bitmap? = null

        val width: Int
        var height = 0

        if (first.width > second.width) {
            width = first.width + second.width
            height = first.height
        } else {
            width = second.width + second.width
            height = first.height
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val comboImage = Canvas(cs!!)

        comboImage.drawBitmap(first, 0f, 0f, null)
        comboImage.drawBitmap(second, first.width.toFloat(), 0f, null)


        return cs
    }

    fun combineImagesVertically(
        first: Bitmap?,
        second: Bitmap?
    ): Bitmap?{ // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        var cs: Bitmap? = null
        if (first == null || second == null) return cs

        val firstResized = getResizedBitmap(first, 800, 750)
        val secondResized = getResizedBitmap(second, 800, 750)

        cs = Bitmap.createBitmap(
            firstResized.width,
            firstResized.height + secondResized.height,
            Bitmap.Config.ARGB_8888
        )

        val comboImage = Canvas(cs!!)

        comboImage.drawBitmap(firstResized, 0f, 0f, null)
        comboImage.drawBitmap(secondResized, 0f, firstResized.height.toFloat(), null)


        return cs
    }


    fun createSingleImageFromMultipleImages(firstImage: Bitmap, secondImage: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(
            firstImage.width,
            firstImage.height + secondImage.height,
            firstImage.config
        )
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        canvas.drawBitmap(secondImage, 0f, firstImage.height.toFloat(), null)
        return result
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getImageUriFromBitmap(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun fileFromBitmap(bitmap: Bitmap?, context: Context): File {
        //create a file to write bitmap customerMenu
        val f = File(context.cacheDir, "${UUID.randomUUID()}.jpg")
        f.createNewFile()

        //Convert bitmap to byte array
        val os = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, os)
        val bitmapdata = os.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        return f
    }

    fun uriFromBitmap(bitmap: Bitmap, context: Context) =
        Uri.parse(fileFromBitmap(bitmap, context).absolutePath)
}
