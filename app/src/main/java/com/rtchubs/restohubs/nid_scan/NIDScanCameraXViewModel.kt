package com.rtchubs.restohubs.nid_scan

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.rtchubs.restohubs.prefs.PreferencesHelper
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class NIDScanCameraXViewModel @Inject constructor(private val application: Application, private val preferencesHelper: PreferencesHelper) : ViewModel() {
    private val TAG = "NIDScanCameraXViewModel"

    val shouldCaptureImage: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val cameraProviderLiveData: MutableLiveData<ProcessCameraProvider> by lazy {
        MutableLiveData<ProcessCameraProvider>()
    }

    fun getProcessCameraProvider(): LiveData<ProcessCameraProvider> {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(application)
        cameraProviderFuture.addListener(
            Runnable {
                try {
                    cameraProviderLiveData.postValue(cameraProviderFuture.get())
                } catch (e: ExecutionException) {
                    // Handle any errors (including cancellation) here.
                    Timber.e("NIDScanCameraXViewModel %s %s", "Unhandled exception", e.toString())
                } catch (e: InterruptedException) {
                    Timber.e("NIDScanCameraXViewModel %s %s", "Unhandled exception", e.toString())
                }
            },
            ContextCompat.getMainExecutor(application)
        )

        return cameraProviderLiveData
    }
}