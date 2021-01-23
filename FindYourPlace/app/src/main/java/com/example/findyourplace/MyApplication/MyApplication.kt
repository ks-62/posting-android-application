package com.example.findyourplace.MyApplication

import android.app.Application
import com.example.findyourplace.R

class MyApplication: Application() {

    var USER_ID: String? = ""
    var USER_NAME: String = ""
    var POST_COUNT: Int = 0

    var resKeyword: Map<String, String> = mapOf(
        Pair("CD_MTS", "CD_MTS"),
        Pair("CD_FSN", "CD_FSN"),
        Pair("CD_FUN", "CD_FUN"),
        Pair("CD_BTY", "CD_BTY"),
        Pair("CD_CHL", "CD_CHL"),
        Pair("CD_ALN", "CD_ALN"),
        Pair("CD_FNT", "CD_FNT"),
        Pair("CD_SEA", "CD_SEA"),
        Pair("CD_MNT", "CD_MNT"),
        Pair("CD_GRN", "CD_GRN"),
        Pair("CD_NTR", "CD_NTR"),
        Pair("CD_RVR", "CD_RVR"),
        Pair("CD_UEP", "CD_UEP"),
        Pair("CD_NST", "CD_NST"),
        Pair("CD_OPN", "CD_OPN"),
        Pair("CD_EXT", "CD_EXT"),
        Pair("CD_FTR", "CD_FTR"),
        Pair("CD_WLT", "CD_WLT"),
        Pair("CD_RLX", "CD_RLX"),
        Pair("CD_KTT", "CD_KTT"),
        Pair("CD_LTS", "CD_LTS"),
        Pair("CD_ACS", "CD_ACS"),
        Pair("CD_HGH", "CD_HGH")
    )

    companion object {
        private var instance: MyApplication? = null

        fun getInstance(): MyApplication {
            if(instance == null) {
                instance = MyApplication()
            }
            return instance!!
        }

    }

}