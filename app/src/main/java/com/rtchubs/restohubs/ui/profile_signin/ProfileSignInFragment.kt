package com.rtchubs.restohubs.ui.profile_signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.ProfileSignInBinding
import com.rtchubs.restohubs.ui.LoginHandlerCallback
import com.rtchubs.restohubs.ui.common.BaseFragment
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

private const val REQUEST_TAKE_PHOTO_NID_FRONT = 1

class ProfileSignInFragment : BaseFragment<ProfileSignInBinding, ProfileSignInViewModel>() {


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_sign_in

    override val viewModel: ProfileSignInViewModel by viewModels { viewModelFactory }

    var rivNidFrontCaptureImage: String = ""
    var rivNidBackCaptureImage: String = ""

//    val args: ProfileSignInFragmentArgs by navArgs()

    private var listener: LoginHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

//        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
//            val tt = 0
//        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        requireActivity().window?.setSoftInputMode(
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
//        )
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.nameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewDataBinding.btnSubmit.isEnabled = s.toString().length > 3
            }

        })

//        val nidFrontData = args.NIDData.frontData
//        val nidBackData = args.NIDData.backData

//        viewDataBinding.nameField.setText(nidFrontData.name)
//        viewDataBinding.birthDayField.setText(nidFrontData.birthDate)
//        viewDataBinding.nidField.setText(nidFrontData.nidNo)
//        viewDataBinding.addressField.setText(nidBackData.birthPlace)

        viewDataBinding.btnSubmit.setOnClickListener {
            preferencesHelper.isLoggedIn = true
            listener?.onLoggedIn()
//            print("Model: ${Build.MODEL} -- ID: ${Build.ID} -- Manufacturer: ${Build.MANUFACTURER}")
//            val helper = args.registrationHelper
//            val name = viewDataBinding.nameField.text.toString()
//
//            // For test only
//            val inputStream: InputStream = mContext.assets.open("grace_hopper.jpg")
//            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//            val userImage = BitmapUtilss.fileFromBitmap(bitmap, mContext).asFilePart("UserImage")
//            viewModel.registerNewUser(helper.mobile, helper.otp,
//                helper.pinNumber, name, helper.operator, Build.ID,
//                Build.MANUFACTURER, Build.MODEL, nidFrontData.nidNo, null, null, userImage)
//                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {response ->
//
//                response?.let {
//                    when {
//                        it.isSuccess == true -> {
//                            preferencesHelper.isRegistered = true
//                            preferencesHelper.isTermsAccepted = true
//                            preferencesHelper.pinNumber = helper.pinNumber
//                            preferencesHelper.mobileNo = helper.mobile
//                            preferencesHelper.operator = helper.operator
//                            preferencesHelper.deviceId = Build.ID
//                            preferencesHelper.deviceName = Build.MANUFACTURER
//                            preferencesHelper.deviceModel = Build.MODEL
//
//                            showSuccessToast(mContext, registrationSuccessMessage)
//                            navController.navigate(ProfileSignInFragmentDirections.actionProfileSignInFragmentToSignInFragment())
//                        }
//                        it.isSuccess == false && it.errorMessage != null -> {
//                            showWarningToast(mContext, it.errorMessage)
//                        }
//                        else -> {
//                            showWarningToast(mContext, commonErrorMessage)
//                        }
//                    }
//                }
//            })
        }
        viewDataBinding.rivNidFrontImage.setOnClickListener {
            //dispatchTakePictureIntent("rivNidFrontImage")
        }
        viewDataBinding.rivNidBackImage.setOnClickListener {
            //dispatchTakePictureIntent("rivNidBackImage")
        }
    }

    private fun dispatchTakePictureIntent(captureFor: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(captureFor)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        mContext,
                        "com.rtchubs.restohubs.fileprovider",
                        it
                    )
                    Timber.d("received file uri : $photoURI")

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_NID_FRONT)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(captureFor: String): File {
        // Create an image file name
        // val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val timeStamp: String = Date().time.toString()
        val storageDir: File = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        Timber.d("file directory : $storageDir")

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (captureFor == "rivNidFrontImage") {
                rivNidFrontCaptureImage = absolutePath
            } else if (captureFor == "rivNidBackImage") {
                rivNidBackCaptureImage = absolutePath
            }
            //galleryAddPic(absolutePath)
        }
    }

    // need this when file is saved in external storage public directory
   /* private fun galleryAddPic(absolutePath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(absolutePath)
            mediaScanIntent.data = Uri.fromFile(f)
            cntx.sendBroadcast(mediaScanIntent)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO_NID_FRONT && resultCode == Activity.RESULT_OK) {


            //Intent data is returning empty

            /*data?.data?.let { uri ->
                viewDataBinding.rivNidFrontImage.setImageURI(uri)
            } ?: run {
                Timber.e("uri is null")
            }*/

           /* val imageBitmap = data?.extras?.get("data") as? Bitmap
            viewDataBinding.rivNidFrontImage.setImageBitmap(imageBitmap)*/


        }
    }

}