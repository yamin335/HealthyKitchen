package com.rtchubs.restohubs.nid_scan

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.camerax.CameraFragment
import com.rtchubs.restohubs.camerax.PermissionsFragment
import com.rtchubs.restohubs.databinding.NIDScanCameraXFragmentBinding
import com.rtchubs.restohubs.models.NIDBackModel
import com.rtchubs.restohubs.models.NIDDataModels
import com.rtchubs.restohubs.models.NIDFrontModel
import com.rtchubs.restohubs.ui.MainActivity
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.Utils.allPermissionsGranted
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.roundToInt

enum class ImageType {
    NID_FRONT,
    NID_BACK,
    NID_PROFILE,
    NONE
}
class NIDScanCameraXFragment : BaseFragment<NIDScanCameraXFragmentBinding, NIDScanCameraXViewModel>() {

    @Inject
    lateinit var application: Application

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_nid_scan_camerax_preview

    override val viewModel: NIDScanCameraXViewModel by viewModels { viewModelFactory }

    val args: NIDScanCameraXFragmentArgs by navArgs()

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = TEXT_RECOGNITION

    private var displayId: Int = -1
    private var camera: Camera? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    var imageType: ImageType = ImageType.NID_FRONT
    var nidFrontData = NIDFrontModel()
    var nidBackData = NIDBackModel()

    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            navController.navigateUp()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Determine the Image Save directory
        outputDirectory = MainActivity.getOutputDirectory(requireContext())

        viewDataBinding.previewView.post {
            // Keep track of the display in which this view is attached
            displayId = viewDataBinding.previewView.display.displayId
        }

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        viewModel.shouldCaptureImage.observe( viewLifecycleOwner, Observer { shouldCapture ->
            if (shouldCapture) {
                captureAndSaveImage()
            }
        })

        viewModel.getProcessCameraProvider().observe( viewLifecycleOwner, Observer { provider ->
            cameraProvider = provider
            if (allPermissionsGranted(requireContext())) {
                bindAllCameraUseCases()
            }
            //bindAllCameraUseCases()
        })

        viewDataBinding.btnSkip.setOnClickListener {
            val action = NIDScanCameraXFragmentDirections.actionNIDScanCameraXFragmentToProfileSignInFragment(NIDDataModels(false, nidFrontData, nidBackData), args.registrationHelper)
            navController.navigate(action)
        }
    }

    private fun bindAllCameraUseCases() {
        bindPreviewUseCase()
        bindAnalysisUseCase()
    }

    private fun isCameraLiveViewportEnabled() = false

    private fun getFaceDetectorOptionsForLivePreview(): FaceDetectorOptions {
        return FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setMinFaceSize(0.1F) // Ranges from [0.0] to [1.0]
            .enableTracking() // Enables face tracking
            .build()
    }

    private fun getCameraXTargetAnalysisSize(): Size {
        return Size(480, 640)
    }

    private fun bindPreviewUseCase() {
        if (!isCameraLiveViewportEnabled()) {
            return
        }

        if (cameraProvider == null) {
            return
        }

        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        // Initialize CameraX Preview
        previewUseCase = Preview.Builder()
            .setTargetResolution(getCameraXTargetAnalysisSize())
            .build()

        // Initialize ImageCapture for capturing image
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(getCameraXTargetAnalysisSize())
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(viewDataBinding.previewView.display.rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        //cameraProvider?.unbindAll()

        try {

            // Attach the viewfinder's surface provider to preview use case
            previewUseCase!!.setSurfaceProvider(viewDataBinding.previewView.createSurfaceProvider())

            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider?.bindToLifecycle(
                this, cameraSelector!!, imageCapture, previewUseCase)

        } catch (exc: Exception) {
            Log.e(CameraFragment.TAG, "Use case binding failed", exc)
        }

        //previewUseCase!!.setSurfaceProvider(viewDataBinding.previewView.createSurfaceProvider())
        //cameraProvider!!.bindToLifecycle(this, cameraSelector!!, previewUseCase, imageCapture)
    }

    fun blinkUIAsCameraShutter() {
        // We can only change the foreground Drawable using API level 23+ API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Display flash animation to indicate that photo was captured
            viewDataBinding.cameraContainer.postDelayed({
                viewDataBinding.cameraContainer.foreground = ColorDrawable(Color.WHITE)
                viewDataBinding.cameraContainer.postDelayed(
                    { viewDataBinding.cameraContainer.foreground = null }, 50L)
            }, 100L)
        }
    }

    @SuppressLint("NewApi")
    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor = try {
            when (selectedModel) {
                TEXT_RECOGNITION -> {
                    TextRecognitionProcessor(requireContext(), object : TextRecognitionResultPublisher {
                        override fun onFrontNID(result: NIDFrontModel) {
                            nidFrontData = result
                            viewDataBinding.frontBackText.text = "Back of NID Card"
                            imageType = ImageType.NID_BACK

                            // We can only change the foreground Drawable using API level 23+ API
                            blinkUIAsCameraShutter()

                            //imageProcessor?.stop()
                            //viewModel.shouldCaptureImage.postValue(true)
                            bindAnalysisUseCase()
                            Toast.makeText(requireContext(), "Show Back Side of NID", Toast.LENGTH_LONG).show()
                        }

                        override fun onBackNID(result: NIDBackModel) {
                            nidBackData = result
                            imageType = ImageType.NONE
                            // We can only change the foreground Drawable using API level 23+ API
                            blinkUIAsCameraShutter()

                            val action = NIDScanCameraXFragmentDirections.actionNIDScanCameraXFragmentToProfileSignInFragment(NIDDataModels(true, nidFrontData, nidBackData), args.registrationHelper)
                            navController.navigate(action)
                        }
                    }, imageType)
                }
                FACE_DETECTION -> {
                    val faceDetectorOptions = getFaceDetectorOptionsForLivePreview()
                    FaceDetectorProcessor(requireContext(), faceDetectorOptions)
                }
                else -> throw IllegalStateException("Invalid model name")
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Can not create image processor: " + e.localizedMessage,
                Toast.LENGTH_LONG
            )
                .show()
            return
        }

        val builder = ImageAnalysis.Builder().setTargetResolution(getCameraXTargetAnalysisSize())
//        val targetAnalysisSize = getCameraXTargetAnalysisSize()
//        if (targetAnalysisSize != null) {
//            builder.setTargetResolution(targetAnalysisSize)
//        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            cameraExecutor,
            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped =
                        lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees =
                        imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        viewDataBinding.graphicOverlay.setImageSourceInfo(
                            imageProxy.width, imageProxy.height, isImageFlipped
                        )
                    } else {
                        viewDataBinding.graphicOverlay.setImageSourceInfo(
                            imageProxy.height, imageProxy.width, isImageFlipped
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    imageProcessor!!.processImageProxy(imageProxy, viewDataBinding.graphicOverlay)
                } catch (e: MlKitException) {
                    Toast.makeText(
                        requireContext(),
                        e.localizedMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )

        // Must unbind the use-cases before rebinding them
        //cameraProvider?.unbindAll()

        try {

            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
//            camera = cameraProvider?.bindToLifecycle(
//                viewLifecycleOwner, cameraSelector!!, previewUseCase, imageCapture, analysisUseCase)

            camera = cameraProvider!!.bindToLifecycle(this, cameraSelector!!, analysisUseCase)

        } catch (exc: Exception) {
            Log.e(CameraFragment.TAG, "Use case binding failed", exc)
        }
    }

    private fun createFile(baseFolder: File, format: String = "yyyy-MM-dd-HH-mm-ss-SSS", extension: String = ".jpg") =
        File(baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension)

    fun captureAndSaveImage() {
        // Create output file to hold the image
        val photoFile = createFile(outputDirectory)

        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        // Setup image capture listener which is triggered after photo has been taken
        imageCapture?.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    progress.visibility = View.GONE
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    nidFrontData.image = savedUri
                    // We can only change the foreground Drawable using API level 23+ API
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        // Update the gallery thumbnail with latest picture taken
//                        setGalleryThumbnail(savedUri)
//                    }

                    //Text recognition
//                    Glide.with(requireContext()).asBitmap().load(savedUri)
//                        .into(object : CustomTarget<Bitmap?>() {
//                            override fun onResourceReady(
//                                resource: Bitmap,
//                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
//                            ) {
//                                runTextRecognition(resource)
//                            }
//
//                            override fun onLoadCleared(placeholder: Drawable?) {
//                                print("load cleared")
//                            }
//                        })

                    // Implicit broadcasts will be ignored for devices running API level >= 24
                    // so if you only target API level 24+ you can remove this statement
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        requireActivity().sendBroadcast(
                            Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                        )
                    }

                    // If the folder selected is an external media directory, this is
                    // unnecessary but otherwise other apps will not be able to access our
                    // images unless we scan them using [MediaScannerConnection]
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri.toFile().extension)
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(savedUri.toFile().absolutePath),
                        arrayOf(mimeType)
                    ) { _, uri ->
                        //Log.d(NIDScanCameraXFragment.TAG, "Image capture scanned into media store: $uri")
                    }
                }
            })
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()
    }

    companion object {
        private const val TAG = "NIDScanCameraXFragment"

        // For Captured Image Save
        /** Helper function used to create a timestamped file */

        private const val PERMISSION_REQUESTS = 1
        private const val OBJECT_DETECTION = "Object Detection"
        private const val OBJECT_DETECTION_CUSTOM = "Custom Object Detection (Bird)"
        private const val FACE_DETECTION = "Face Detection"
        private const val TEXT_RECOGNITION = "Text Recognition"
        private const val BARCODE_SCANNING = "Barcode Scanning"
        private const val IMAGE_LABELING = "Image Labeling"
        private const val IMAGE_LABELING_CUSTOM = "Custom Image Labeling (Bird)"
        private const val AUTOML_LABELING = "AutoML Image Labeling"
        private const val STATE_SELECTED_MODEL = "selected_model"
        private const val STATE_LENS_FACING = "lens_facing"

        private fun isPermissionGranted (
            context: Context,
            permission: String?
        ): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission!!)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }
    }
}