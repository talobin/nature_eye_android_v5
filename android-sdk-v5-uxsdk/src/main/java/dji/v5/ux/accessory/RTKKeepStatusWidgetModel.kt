package dji.v5.ux.accessory

import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.common.error.RxError
import dji.v5.manager.interfaces.IRTKCenter
import dji.v5.ux.core.base.DJISDKModel
import dji.v5.ux.core.base.WidgetModel
import dji.v5.ux.core.communication.ObservableInMemoryKeyedStore
import dji.v5.ux.core.util.DataProcessor
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Description :Widget Model for the [RTKKeepStatusWidget] used to define
 * the underlying logic and communication
 *
 * @author: Byte.Cai
 *  date : 2022/7/11
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class RTKKeepStatusWidgetModel(
    djiSdkModel: DJISDKModel,
    uxKeyManager: ObservableInMemoryKeyedStore,
    private val rtkCenter: IRTKCenter,
) : WidgetModel(djiSdkModel, uxKeyManager) {
    private val isRTKKeepStatusEnabledProcessor: DataProcessor<Boolean> = DataProcessor.create(false)

    override fun inSetup() {
        // do nothing
        rtkCenter.getRTKMaintainAccuracyEnabled(object : CommonCallbacks.CompletionCallbackWithParam<Boolean> {
            override fun onSuccess(t: Boolean?) {
                isRTKKeepStatusEnabledProcessor.onNext(t ?: false)
            }

            override fun onFailure(error: IDJIError) {
                isRTKKeepStatusEnabledProcessor.onNext(false)
            }

        })
    }

    override fun inCleanup() {
        // do nothing
    }

    val rtkKeepStatusEnable: Flowable<Boolean>
        get() = isRTKKeepStatusEnabledProcessor.toFlowable()

    fun setRTKKeepStatusEnable(enabled: Boolean): Completable {
        return Completable.create {
            rtkCenter.setRTKMaintainAccuracyEnabled(enabled, object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    it.onComplete()
                }

                override fun onFailure(error: IDJIError) {
                    it.onError(RxError(error))
                }
            })

        }.subscribeOn(Schedulers.computation())
    }

}