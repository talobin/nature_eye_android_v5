package dji.sampleV5.moduleaircraft.data

import dji.sampleV5.modulecommon.data.FragmentPageInfo
import dji.sampleV5.modulecommon.data.FragmentPageInfoItem
import dji.sampleV5.modulecommon.data.IFragmentPageInfoFactory
import dji.sampleV5.moduleaircraft.R


/**
 * Class Description
 *
 * @author Hoker
 * @date 2021/5/7
 *F
 * Copyright (c) 2021, DJI All Rights Reserved.
 */
class AircraftFragmentPageInfoFactory : IFragmentPageInfoFactory {

    override fun createPageInfo(): FragmentPageInfo {
        return FragmentPageInfo(R.navigation.nav_aircraft).apply {
            items.add(FragmentPageInfoItem(R.id.virtual_stick_page, R.string.item_virtual_stick_title, R.string.item_virtual_description))
            items.add(FragmentPageInfoItem(R.id.flight_record_page, R.string.item_flight_record_title, R.string.item_flight_record_description))
            items.add(FragmentPageInfoItem(R.id.flight_upgrade_page, R.string.item_upgrade_title, R.string.item_upgrade_description))
            items.add(FragmentPageInfoItem(R.id.flight_simulator_page, R.string.item_simulator_title, R.string.item_simulator_description))
            items.add(FragmentPageInfoItem(R.id.psdk_data_page, R.string.item_psdk_title, R.string.item_psdk_description))
            items.add(FragmentPageInfoItem(R.id.megaphone_page, R.string.item_megaphone_title, R.string.item_megaphone_description))
            items.add(FragmentPageInfoItem(R.id.waypoint_v3_page, R.string.item_waypoint_title, R.string.item_waypoint_description))
            items.add(FragmentPageInfoItem(R.id.waypoint_v3_page, R.string.item_waypoint_title, R.string.item_waypoint_description))
            items.add(FragmentPageInfoItem(R.id.rtk_center_page, R.string.item_trk_center_title, R.string.item_trk_center_description))
            items.add(FragmentPageInfoItem(R.id.perception_page, R.string.item_perception_title, R.string.item_perception_description))
            items.add(FragmentPageInfoItem(R.id.uas_page, R.string.item_uas_title, R.string.item_uas_description))
            items.add(FragmentPageInfoItem(R.id.lte_page, R.string.item_lte_title, R.string.item_lte_description))
            items.add(FragmentPageInfoItem(R.id.security_code_page, R.string.item_security_code_title, R.string.item_security_code_description))
        }
    }
}