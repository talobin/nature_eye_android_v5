package dji.sampleV5.all

import dji.sampleV5.modulecommon.TestingToolsActivity
import dji.sampleV5.modulecommon.data.CommonFragmentPageInfoFactory
import dji.sampleV5.modulecommon.data.FragmentPageInfo
import dji.sampleV5.moduleaircraft.data.AircraftFragmentPageInfoFactory
import dji.sampleV5.modulehandheld.data.HandheldFragmentPageInfoFactory
import java.util.*

/**
 * Class Description
 *
 * @author Hoker
 * @date 2021/3/9
 *
 * Copyright (c) 2021, DJI All Rights Reserved.
 */
class AllTestingToolsActivity : TestingToolsActivity() {
    override fun loadPages() {
        msdkCommonOperateVm.apply {
            val itemList = LinkedHashSet<FragmentPageInfo>().also {
                it.add(CommonFragmentPageInfoFactory().createPageInfo())
                it.add(AircraftFragmentPageInfoFactory().createPageInfo())
                it.add(HandheldFragmentPageInfoFactory().createPageInfo())
            }
            loaderItem(itemList)
        }
    }
}