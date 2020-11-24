package com.rtchubs.restohubs.ui.shops

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.PermissionChecker
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.ShopDetailsContactUsFragmentBinding
import com.rtchubs.restohubs.models.Merchant
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.ui.common.CommonAlertDialog

private const val MERCHANT = "merchant"
private const val CALL_REQUEST_CODE = 222

class ShopDetailsContactUsFragment : BaseFragment<ShopDetailsContactUsFragmentBinding, ShopDetailsContactUsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_shop_details_contact_us

    override val viewModel: ShopDetailsContactUsViewModel by viewModels { viewModelFactory }

    private var merchant: Merchant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            merchant = it.getSerializable(MERCHANT) as Merchant
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.merchant = merchant

        viewDataBinding.rowMobile.setOnClickListener {
            merchant?.mobile?.let { number ->
                callPhone(number)
            }
        }

        viewDataBinding.rowWhatsApp.setOnClickListener {
            merchant?.whatsApp?.let { number ->
                openWhatsApp(number)
            }
        }

        viewDataBinding.rowEmail.setOnClickListener {
            merchant?.email?.let { email ->
                mailTo(email)
            }
        }

        viewDataBinding.rowAddress.setOnClickListener {
            merchant?.address?.let { address ->
                showAddressOnGoogleMap("RC7F+9M Dhaka")
            }
        }

    }

    private fun showAddressOnGoogleMap(code: String) {
        // Display the location using a global plus code.
        val gmmIntentUri = Uri.parse("http://plus.codes/$code")
        //gmmIntentUri = Uri.parse("https://plus.codes/QJQ5+XX,San%20Francisco")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(requireActivity().packageManager)?.let {
            startActivity(mapIntent)
        }
    }

    private fun openWhatsApp(number: String) {

        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.setPackage("com.whatsapp")
        startActivity(intent)

//        val sendIntent = Intent()
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
//        sendIntent.type = "text/plain"
//        sendIntent.setPackage("com.whatsapp")
//        startActivity(sendIntent)
    }

    private fun callPhone(number: String) {
        if (PermissionChecker.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CALL_PHONE
            ) == PermissionChecker.PERMISSION_GRANTED) {
            val callingIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            requireActivity().startActivity(callingIntent)
        } else {
            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                val explanationDialog = CommonAlertDialog(object :  CommonAlertDialog.YesCallback{
                    override fun onYes() {
                        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)
                    }
                }, "Allow Permission", "You have to allow permission for making call.\n\nDo you want to allow permission?")
                explanationDialog.show(parentFragmentManager, "#call_permission_dialog")

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private fun mailTo(@Suppress("SameParameterValue") recipient: String) {
        try {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                // The intent does not have a URI, so declare the "text/plain" MIME type
                type = "text/plain"
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient)) // recipients
                // You can also attach multiple items by passing an ArrayList of Uris
            }
            val chooser = Intent.createChooser(mailIntent, "Send Mail")
            // Verify the intent will resolve to at least one activity
            if (mailIntent.resolveActivity(requireActivity().packageManager) != null) {
                requireActivity().startActivity(chooser)
            }
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param merchant Selected Merchant.
         * @return A new instance of fragment 'ShopDetailsContactUsFragment'.
         */

        @JvmStatic
        fun newInstance(merchant: Merchant) =
            ShopDetailsContactUsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MERCHANT, merchant)
                }
            }
    }
}