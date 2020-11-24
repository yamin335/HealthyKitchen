package com.rtchubs.restohubs.di

import com.rtchubs.restohubs.worker.DaggerWorkerFactory
import com.rtchubs.restohubs.worker.TokenRefreshWorker
import com.rtchubs.restohubs.worker.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class WorkerBindingModule {

    /*injector for DaggerWorkerFactory*/
    @Binds
    @IntoMap
    @WorkerKey(TokenRefreshWorker::class)
    abstract fun bindTokenRefreshWorker(factory: TokenRefreshWorker.Factory):
            DaggerWorkerFactory.ChildWorkerFactory


}
