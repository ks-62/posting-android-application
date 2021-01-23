package com.example.findyourplace.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findyourplace.R
import com.example.findyourplace.adapter.genreAdapter
import com.example.findyourplace.dataClass.genreClass
import com.example.findyourplace.fragment.ListFragment
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_acs
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_aln
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_bty
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_chl
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_ext
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_fnt
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_fsn
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_ftr
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_fun
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_grn
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_hgh
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_ktt
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_lts
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_mnt
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_mts
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_nst
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_ntr
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_opn
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_rlx
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_rvr
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_sea
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_uep
import kotlinx.android.synthetic.main.activity_search.checkBox_cd_wlt
import kotlinx.android.synthetic.main.activity_search.spinner_genre

class SearchActivity : AppCompatActivity() {

    var searchCountry: String = ""
    var searchCity1: String = ""
    var searchCity2: String = ""

    enum class RequestCd( val cd: Int) {
        SEARCH_COUNTRY(0),
        SEARCH_CITY_1(1),
        SEARCH_CITY_2(2)
    }

    companion object {
        var SEARCH_CD: String = "searchCd"
        var SELECT_COUNTRY: String = "country"
        var SELECT_CITY_1: String = "city1"
        var SELECT_CITY_2: String = "city2"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(resultCode == Activity.RESULT_OK) {
                when(requestCode) {
                    RequestCd.SEARCH_COUNTRY.cd -> {
                        searchCountry = data?.getStringExtra(SELECT_COUNTRY)?: ""
                        input_country.text = searchCountry
                    }
                    RequestCd.SEARCH_CITY_1.cd -> {
                        searchCity1 = data?.getStringExtra(SELECT_CITY_1)?: ""
                        input_city1.text = searchCity1
                    }
                    RequestCd.SEARCH_CITY_2.cd -> {
                        searchCity2 = data?.getStringExtra(SELECT_CITY_2)?: ""
                        input_city2.text = searchCity2
                    }
                }
            }
        } catch (e: Exception) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //初期化
        initialize()

    }

    private fun initialize() {

        //スピナー
        spinnerSet()

        //リスナー
        setListener()
    }

    private fun spinnerSet() {
        val genreList: ArrayList<genreClass?> = arrayListOf()
        genreList.add(genreClass("", "ジャンルを選択してください"))
        genreList.add(genreClass("001", "賑やか"))
        genreList.add(genreClass("002", "落ち着く"))
        genreList.add(genreClass("003", "神秘的"))
        genreList.add(genreClass("004", "一人になれる"))
        genreList.add(genreClass("005", "のどか"))
        genreList.add(genreClass("006", "異国感"))
        genreList.add(genreClass("007", "その他"))

        val adapter = genreAdapter(this, genreList)
        spinner_genre.adapter = adapter

    }

    private fun setListener() {
        button_back.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        input_country.setOnClickListener {
            val intent = Intent(this, SearchInputActivity::class.java)
            intent.putExtra(SearchInputActivity.SEARCH_CD, RequestCd.SEARCH_COUNTRY.cd.toString())
            startActivityForResult(intent, RequestCd.SEARCH_COUNTRY.cd)

        }
        input_city1.setOnClickListener {
            val intent = Intent(this, SearchInputActivity::class.java)
            intent.putExtra(SearchInputActivity.SEARCH_CD, RequestCd.SEARCH_CITY_1.cd.toString())
            startActivityForResult(intent, RequestCd.SEARCH_CITY_1.cd)
        }
        input_city2.setOnClickListener {
            val intent = Intent(this, SearchInputActivity::class.java)
            intent.putExtra(SearchInputActivity.SEARCH_CD, RequestCd.SEARCH_CITY_2.cd.toString())
            startActivityForResult(intent, RequestCd.SEARCH_CITY_2.cd)
        }
        button_search.setOnClickListener {
            try {
                val searchGenre: String = (spinner_genre.selectedItem as genreClass).gCode
                val searchKeyword: Array<String> = Array(23){""}
                if(checkBox_cd_mts.isChecked) searchKeyword[0] = "CD_MTS"
                if(checkBox_cd_fsn.isChecked) searchKeyword[1] = "CD_FSN"
                if(checkBox_cd_fun.isChecked) searchKeyword[2] = "CD_FUN"
                if(checkBox_cd_bty.isChecked) searchKeyword[3] = "CD_BTY"
                if(checkBox_cd_chl.isChecked) searchKeyword[4] = "CD_CHL"
                if(checkBox_cd_aln.isChecked) searchKeyword[5] = "CD_ALN"
                if(checkBox_cd_fnt.isChecked) searchKeyword[6] = "CD_FNT"
                if(checkBox_cd_sea.isChecked) searchKeyword[7] = "CD_SEA"
                if(checkBox_cd_mnt.isChecked) searchKeyword[8] = "CD_MNT"
                if(checkBox_cd_grn.isChecked) searchKeyword[9] = "CD_GRN"
                if(checkBox_cd_ntr.isChecked) searchKeyword[10] = "CD_NTR"
                if(checkBox_cd_rvr.isChecked) searchKeyword[11] = "CD_RVR"
                if(checkBox_cd_uep.isChecked) searchKeyword[12] = "CD_UEP"
                if(checkBox_cd_nst.isChecked) searchKeyword[13] = "CD_NST"
                if(checkBox_cd_opn.isChecked) searchKeyword[14] = "CD_OPN"
                if(checkBox_cd_ext.isChecked) searchKeyword[15] = "CD_EXT"
                if(checkBox_cd_ftr.isChecked) searchKeyword[16] = "CD_FTR"
                if(checkBox_cd_wlt.isChecked) searchKeyword[17] = "CD_WLT"
                if(checkBox_cd_rlx.isChecked) searchKeyword[18] = "CD_RLX"
                if(checkBox_cd_ktt.isChecked) searchKeyword[19] = "CD_KTT"
                if(checkBox_cd_lts.isChecked) searchKeyword[20] = "CD_LTS"
                if(checkBox_cd_acs.isChecked) searchKeyword[21] = "CD_ACS"
                if(checkBox_cd_hgh.isChecked) searchKeyword[22] = "CD_HGH"

                val intent = Intent()
                intent.putExtra(ListFragment.SEARCH_GENRE, searchGenre)
                intent.putExtra(ListFragment.SEARCH_COUNTRY, searchCountry)
                intent.putExtra(ListFragment.SEARCH_CITY_1, searchCity1)
                intent.putExtra(ListFragment.SEARCH_CITY_2, searchCity2)
                intent.putExtra(ListFragment.SEARCH_KEYWORD, searchKeyword)
                setResult(Activity.RESULT_OK, intent)
                finish()

            } catch (e: Exception) {

            }
        }
    }

}