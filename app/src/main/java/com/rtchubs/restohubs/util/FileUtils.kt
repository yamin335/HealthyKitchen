package com.rtchubs.restohubs.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode


class FileUtils {
    companion object {
        fun getFileName(context: Context, uri: Uri): String {
            var displayName = ""

            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            val fileCursor = context.contentResolver.query(uri, null, null, null, null)

            fileCursor.use { cursor ->
                cursor?.let {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (it.moveToFirst()) {
                        // Note it's called "Display Name".  This is
                        // provider-specific, and might not necessarily be the file name.
                        displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }

            return displayName
        }

        fun getFileSize(context: Context, uri: Uri): String {
            var fileSize = ""

            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            val fileCursor = context.contentResolver.query(uri, null, null, null, null)

            fileCursor.use { cursor ->
                cursor?.let {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (it.moveToFirst()) {
                        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                        // If the size is unknown, the value stored is null.  But since an
                        // int can't be null in Java, the behavior is implementation-specific,
                        // which is just a fancy term for "unpredictable".  So as
                        // a rule, check if it's null before assigning to an int.  This will
                        // happen often:  The storage API allows for remote files, whose
                        // size might not be locally known.

                        if (!it.isNull(sizeIndex)) {
                            // Technically the column stores an int, but cursor.getString()
                            // will do the conversion automatically.

                            var size = it.getDouble(sizeIndex)
                            if (size > 1024) {
                                size /= 1024
                                if (size > 1024) {
                                    size /= 1024
                                    if (size > 1024) {
                                        size /= 1024
                                        size = BigDecimal(size).setScale(1, RoundingMode.HALF_UP).toDouble()
                                        val temp = size.toString().split(".")
                                        fileSize = if (temp[1].equals("0", true)) {
                                            temp[0] + " GB"
                                        } else {
                                            "$size GB"
                                        }
                                    } else {
                                        size = BigDecimal(size).setScale(1, RoundingMode.HALF_UP).toDouble()
                                        val temp = size.toString().split(".")
                                        fileSize = if (temp[1].equals("0", true)) {
                                            temp[0] + " MB"
                                        } else {
                                            "$size MB"
                                        }
                                    }
                                } else {
                                    size = BigDecimal(size).setScale(1, RoundingMode.HALF_UP).toDouble()
                                    val temp = size.toString().split(".")
                                    fileSize = if (temp[1].equals("0", true)) {
                                        temp[0] + " KB"
                                    } else {
                                        "$size KB"
                                    }
                                }
                            } else {
                                size /= 1024
                                size = BigDecimal(size).setScale(1, RoundingMode.HALF_UP).toDouble()
                                fileSize = "$size B"
                            }

                            //fileSize = it.getString(sizeIndex)
                        } else {
                            fileSize = "Unknown"
                        }
                    }
                }
            }

            return fileSize
        }

        @Suppress("unused")
        fun getExternalStorageState(): Pair<Boolean, Boolean> {
            val isExternalStorageAvailable: Boolean
            val isExternalStorageWritable: Boolean

            when (Environment.getExternalStorageState()) {
                Environment.MEDIA_MOUNTED -> {
                    isExternalStorageAvailable = true
                    isExternalStorageWritable = true
                }

                Environment.MEDIA_MOUNTED_READ_ONLY -> {
                    isExternalStorageAvailable = true
                    isExternalStorageWritable = false
                }

                else -> {
                    isExternalStorageAvailable = false
                    isExternalStorageWritable = false
                }

            }

            return Pair(isExternalStorageAvailable, isExternalStorageWritable)
        }

        /**
         * Get a file from a Uri.
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param applicationContext The context.
         * @param uri     The Uri to query.
         */
        @Suppress("unused")
        @Throws(Exception::class)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun getFileFromUri(applicationContext: Context, uri: Uri): File? {
            var path: String? = null
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(applicationContext, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    val realDocId = split[1]
                    if ("primary".equals(type, ignoreCase = true)) {
                        //If your app is used on a device that runs Android 4.3 (API level 18) or lower,
                        // then the array contains just one element,
                        // which represents the primary external storage volume
                        val externalStorageVolumes: Array<out File> =
                            ContextCompat.getExternalFilesDirs(applicationContext, null)
                        val primaryExternalStorage = externalStorageVolumes[0]
                        path = "$primaryExternalStorage/$realDocId"
//                            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                MediaStore.getDocumentUri(applicationContext, uri)?.path
//                            } else {
//                                Environment.getExternalStorageDirectory().toString() + "/" + realDocId
//                            }
                    }
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    path = getDataColumn(applicationContext, contentUri, null, null)
                } else if (isMediaDocument(uri)) { // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    path = getDataColumn(applicationContext, contentUri!!, selection, selectionArgs)
                } else if (isGoogleDrive(uri)) { // Google Drive
                    return saveFileIntoExternalStorageByUri(applicationContext, uri)
                } // MediaStore (and general)
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                path = getDataColumn(applicationContext, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                path = uri.path
            }

            return path?.let {
                File(path)
            }


//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            }
//            else {
//                val cursor = context.contentResolver.query(uri, null, null, null, null)
//                File(cursor?.getString(cursor.getColumnIndex("_data")))
//            }
        }


        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        private fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            // Get columns name by uri type.
            val column = "_data"
//            var column = MediaStore.Images.Media.DATA
//            when {
//                uri === MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
//                    column = MediaStore.Images.Media.DATA
//                }
//                uri === MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> {
//                    column = MediaStore.Audio.Media.DATA
//                }
//                uri === MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
//                    column = MediaStore.Video.Media.DATA
//                }
//            }

            val projection = arrayOf(
                column
            )

            val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,null)
            cursor.use {
                cursor?.let {
                    if (it.moveToFirst()) {
                        return it.getString(it.getColumnIndexOrThrow(column))
                    }
                }
            }
            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is GoogleDrive.
         */
        private fun isGoogleDrive(uri: Uri): Boolean {
            return uri.authority
                .equals("com.google.android.apps.docs.storage", ignoreCase = true)
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }


        private fun makeEmptyFileIntoExternalStorageWithTitle(applicationContext: Context, title: String): File {
            //If your app is used on a device that runs Android 4.3 (API level 18) or lower,
            // then the array contains just one element,
            // which represents the primary external storage volume
            val externalStorageVolumes: Array<out File> = ContextCompat.getExternalFilesDirs(applicationContext, null)
            val primaryExternalStorage = externalStorageVolumes[0]
            //path = "$primaryExternalStorage/$realDocId"

            //val root: String = Environment.getExternalStorageDirectory().getAbsolutePath()
            return File(primaryExternalStorage.absolutePath, title)
        }


        @Suppress("unused")
        @Throws(Exception::class)
        fun saveBitmapFileIntoExternalStorageWithTitle(
            applicationContext: Context,
            bitmap: Bitmap,
            title: String
        ) {
            val fileOutputStream =
                FileOutputStream(makeEmptyFileIntoExternalStorageWithTitle(applicationContext,"$title.png"))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        }


        @Throws(Exception::class)
        fun saveFileIntoExternalStorageByUri(
            context: Context,
            uri: Uri
        ): File? {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalSize: Int = inputStream?.available()!!
            val fileName: String = getFileName(context, uri)
            val file: File = makeEmptyFileIntoExternalStorageWithTitle(context, fileName)
            val bis = BufferedInputStream(inputStream)
            val bos = BufferedOutputStream(
                FileOutputStream(
                    file, false
                )
            )

            bis.use { bufferedInputStream ->
                bos.use { bufferedOutputStream ->
                    val buf = ByteArray(originalSize)
                    bufferedInputStream.read(buf)
                    do {
                        bufferedOutputStream.write(buf)
                    } while (bufferedInputStream.read(buf) != -1)
                    bufferedOutputStream.flush()
                }
            }

            return file
        }

    }
}