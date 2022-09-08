package dji.sampleV5.moduleaircraft.models

import androidx.lifecycle.MutableLiveData
import dji.sampleV5.modulecommon.models.DJIViewModel
import dji.v5.manager.aircraft.uas.UASRemoteIDManager
import dji.v5.manager.aircraft.uas.UASRemoteIDStatus
import dji.v5.manager.areacode.AreaCode

/**
 * Description :美国无人机远程识别VM
 *
 * @author: Byte.Cai
 *  date : 2022/8/3
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
class UASUAVM :DJIViewModel(){
    val uasRemoteIDStatus=MutableLiveData<UASRemoteIDStatus>()
    init {
        UASRemoteIDManager.getInstance().setAreaCode(AreaCode.US)
    }
    fun addRemoteIdStatusListener() {
        UASRemoteIDManager.getInstance().addUASRemoteIDStatusListener {
            uasRemoteIDStatus.postValue(it)
        }
    }
}