package com.rtchubs.restohubs.ar_location

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.ar_location.helpers.DisplayRotationHelper
import com.rtchubs.restohubs.ar_location.helpers.TapHelper
import com.rtchubs.restohubs.ar_location.rendering.BackgroundRenderer
import com.rtchubs.restohubs.ar_location.rendering.ObjectRenderer
import com.rtchubs.restohubs.ar_location.rendering.PlaneRenderer
import com.rtchubs.restohubs.ar_location.rendering.PointCloudRenderer
import com.rtchubs.restohubs.databinding.ARLocationFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.rendering.AnnotationRenderer
import uk.co.appoly.arcorelocation.rendering.ImageRenderer
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import uk.co.appoly.arcorelocation.utils.Utils2D
import java.io.IOException
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ARLocationFragment : BaseFragment<ARLocationFragmentBinding, ARLocationViewModel>(),
    GLSurfaceView.Renderer {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_ar_location

    override val viewModel: ARLocationViewModel by viewModels { viewModelFactory }

    private val TAG: String = ARLocationFragment::class.java.simpleName

    private var mSession: Session? = null
    private var mMessageSnackbar: Snackbar? = null
    private var mDisplayRotationHelper: DisplayRotationHelper? = null

    private val mBackgroundRenderer = BackgroundRenderer()

    private val mPlaneRenderer = PlaneRenderer()
    private val mPointCloud = PointCloudRenderer()
    private var tapHelper: TapHelper? = null

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private val mAnchorMatrix = FloatArray(16)

    // Tap handling and UI.
    //private final ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);
    private val mAnchors = ArrayList<Anchor>()

    private var locationScene: LocationScene? = null

    private var systemUIConfigurationBackup: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hideSystemUI()
    }

    override fun onDetach() {
        super.onDetach()
        restoreSystemUI()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDisplayRotationHelper = DisplayRotationHelper( /*context=*/requireContext())
        }

        tapHelper = TapHelper( /*context=*/requireContext())
        viewDataBinding.surfaceview.setOnTouchListener(tapHelper)

        // Set up renderer.
        viewDataBinding.surfaceview.setPreserveEGLContextOnPause(true)
        viewDataBinding.surfaceview.setEGLContextClientVersion(2)
        viewDataBinding.surfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.

        viewDataBinding.surfaceview.setRenderer(this)
        viewDataBinding.surfaceview.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)

        var exception: Exception? = null
        var message: String? = null
        try {
            mSession = Session( /* context= */requireContext())
        } catch (e: UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore"
            exception = e
        } catch (e: UnavailableApkTooOldException) {
            message = "Please update ARCore"
            exception = e
        } catch (e: UnavailableSdkTooOldException) {
            message = "Please update this app"
            exception = e
        } catch (e: Exception) {
            message = "This device does not support AR"
            exception = e
        }

        if (message != null) {
            showSnackbarMessage(message, true)
            Log.e(TAG, "Exception creating session", exception)
            return
        }

        // Create default config and check if supported.

        // Create default config and check if supported.
        val config = Config(mSession)
        if (!mSession!!.isSupported(config)) {
            showSnackbarMessage("This device does not support AR", true)
        }
        mSession!!.configure(config)


        // Set up our location scene


        // Set up our location scene
        locationScene = LocationScene(requireContext(), requireActivity(), mSession)

        // Image marker at Eiffel Tower

        // Image marker at Eiffel Tower
        val eiffelTower = LocationMarker(
            90.432124,
            23.800899,
            ImageRenderer("eiffel.png")
        )
        eiffelTower.setOnTouchListener {
            Toast.makeText(
                requireContext(),
                "Touched Eiffel Tower", Toast.LENGTH_SHORT
            ).show()
        }
        eiffelTower.touchableSize = 1000
        locationScene!!.mLocationMarkers.add(
            eiffelTower
        )

        // Annotation at Buckingham Palace

        // Annotation at Buckingham Palace
        locationScene!!.mLocationMarkers.add(
            LocationMarker(
                -1.535823,
                52.284501,
                AnnotationRenderer("Buckingham Palace")
            )
        )

        // Example of using your own renderer.
        // Uses a slightly modified version of hello_ar_java's ObjectRenderer

        // Example of using your own renderer.
        // Uses a slightly modified version of hello_ar_java's ObjectRenderer
        locationScene!!.mLocationMarkers.add(
            LocationMarker(
                90.430650,
                23.800017,
                ObjectRenderer("andy.obj", "andy.png")
            )
        )

    }

    override fun onResume() {
        super.onResume()

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (ARLocationPermissionHelper.hasPermission(requireActivity())) {
            if (locationScene != null) locationScene!!.resume()
            if (mSession != null) {
                showLoadingMessage()
                // Note that order matters - see the note in onPause(), the reverse applies here.
                try {
                    mSession!!.resume()
                } catch (e: CameraNotAvailableException) {
                    e.printStackTrace()
                }
            }
            viewDataBinding.surfaceview.onResume()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDisplayRotationHelper!!.onResume()
            }
        } else {
            ARLocationPermissionHelper.requestPermission(requireActivity())
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationScene != null) locationScene!!.pause()
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call mSession.update() and get a SessionPausedException.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDisplayRotationHelper!!.onPause()
        }
        viewDataBinding.surfaceview.onPause()
        if (mSession != null) {
            mSession!!.pause()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!ARLocationPermissionHelper.hasPermission(requireActivity())) {
            Toast.makeText(
                requireContext(),
                "Camera permission is needed to run this application", Toast.LENGTH_LONG
            ).show()
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(requireActivity())
            }
            navController.popBackStack()
        }
    }

    private fun hideSystemUI() {
        // Standard Android full-screen functionality.
        systemUIConfigurationBackup = requireActivity().window.decorView.systemUiVisibility
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun restoreSystemUI() {
        requireActivity().window.decorView.systemUiVisibility = systemUIConfigurationBackup
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun showSnackbarMessage(
        message: String,
        finishOnDismiss: Boolean
    ) {
        mMessageSnackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message, Snackbar.LENGTH_INDEFINITE
        )
        mMessageSnackbar!!.view.setBackgroundColor(-0x40cdcdce)
        if (finishOnDismiss) {
            mMessageSnackbar!!.setAction(
                "Dismiss"
            ) { mMessageSnackbar!!.dismiss() }
            mMessageSnackbar!!.addCallback(
                object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        navController.popBackStack()
                    }
                })
        }
        mMessageSnackbar!!.show()
    }



    private fun showLoadingMessage() {
        requireActivity().runOnUiThread { showSnackbarMessage("Searching for surfaces...", false) }
    }

    private fun hideLoadingMessage() {
        requireActivity().runOnUiThread {
            if (mMessageSnackbar != null) {
                mMessageSnackbar!!.dismiss()
            }
            mMessageSnackbar = null
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear screen to notify driver it should not load any pixels from previous frame.

        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (mSession == null) {
            return
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        mDisplayRotationHelper!!.updateSessionIfNeeded(mSession)

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            val frame = mSession!!.update()
            val camera = frame.camera


            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.
            val tap = tapHelper!!.poll()
            if (tap != null && camera.trackingState == TrackingState.TRACKING) {
                Log.i(TAG,
                    "HITTEST: Got a tap and tracking"
                )
                Utils2D.handleTap(requireActivity(), locationScene, frame, tap)
            }

            // Draw background.
            mBackgroundRenderer.draw(frame)

            // Draw location markers
            locationScene!!.draw(frame)

            // If not tracking, don't draw 3d objects.
            if (camera.trackingState == TrackingState.PAUSED) {
                return
            }

            // Get projection matrix.
            val projmtx = FloatArray(16)
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

            // Get camera matrix and draw.
            val viewmtx = FloatArray(16)
            camera.getViewMatrix(viewmtx, 0)

            // Compute lighting from average intensity of the image.
            val lightIntensity = frame.lightEstimate.pixelIntensity

            // Visualize tracked points.
            val pointCloud = frame.acquirePointCloud()
            mPointCloud.update(pointCloud)
            mPointCloud.draw(viewmtx, projmtx)

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release()

            // Check if we detected at least one plane. If so, hide the loading message.
            if (mMessageSnackbar != null) {
                for (plane in mSession!!.getAllTrackables(
                    Plane::class.java
                )) {
                    if (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING
                        && plane.trackingState == TrackingState.TRACKING
                    ) {
                        hideLoadingMessage()
                        break
                    }
                }
            }

            // Visualize planes.
            mPlaneRenderer.drawPlanes(
                mSession!!.getAllTrackables(Plane::class.java),
                camera.displayOrientedPose,
                projmtx
            )

            // Visualize anchors created by touch.
            val scaleFactor = 1.0f
            for (anchor in mAnchors) {
                if (anchor.trackingState != TrackingState.TRACKING) {
                    continue
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.pose.toMatrix(mAnchorMatrix, 0)
            }
        } catch (t: Throwable) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG,
                "Exception on the OpenGL thread",
                t
            )
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mDisplayRotationHelper!!.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        // Create the texture and pass it to ARCore session to be filled during update().

        // Create the texture and pass it to ARCore session to be filled during update().
        mBackgroundRenderer.createOnGlThread( /*context=*/requireContext())
        if (mSession != null) {
            mSession!!.setCameraTextureName(mBackgroundRenderer.textureId)
        }

        // Prepare the other rendering objects.
        /*try {
            mVirtualObject.createOnGlThread(*/
        /*context=*/ /*this, "andy.obj", "andy.png");
            mVirtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObjectShadow.createOnGlThread(*/
        /*context=*/ /*this,
                "andy_shadow.obj", "andy_shadow.png");
            mVirtualObjectShadow.setBlendMode(BlendMode.Shadow);
            mVirtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }*/try {
            mPlaneRenderer.createOnGlThread( /*context=*/requireContext(), "trigrid.png")
        } catch (e: IOException) {
            Log.e(TAG,
                "Failed to read plane texture"
            )
        }
        mPointCloud.createOnGlThread( /*context=*/requireContext())
    }

}