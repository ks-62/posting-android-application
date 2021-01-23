package com.example.findyourplace.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.apikey.ACL
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import kotlinx.android.synthetic.main.activity_search_input.*
import kotlinx.coroutines.*

class SearchInputActivity : AppCompatActivity() {

    //インスタンス
    val mApp = MyApplication.getInstance()

    private val appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    private val appID = ApplicationID(appInfo.metaData.getString("FYP_APP_ID_ALGOLIA").toString())
    private val apiKey = APIKey(appInfo.metaData.getString("FYP_API_KEY_ALGOLIA").toString())
    private val client = ClientSearch(appID, apiKey)
    private val indexName = IndexName("post")
    val scope = CoroutineScope(Dispatchers.Default)

    var getSearchCd: String = ""
    var attr: String = ""

    //ListView用データ
    var listWords: MutableList<String> = mutableListOf()

    companion object {
        var SEARCH_CD: String = "searchCd"
    }

    enum class RequestCd( val cd: Int) {
        SEARCH_COUNTRY(0),
        SEARCH_CITY_1(1),
        SEARCH_CITY_2(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_input)

        getSearchCd = intent.getStringExtra(SEARCH_CD)?: ""

        initialize()

    }

    private fun initialize() {

        when(getSearchCd) {
            "0" -> {
                attr = "country"
            }
            "1" -> {
                attr = "city1"
            }
            "2" -> {
                attr = "city2"
            }
        }

        setListener()
    }

    private fun setListener() {

        listView_word.setOnItemClickListener { adapterView, _, position, _ ->
            val selectWord: String = adapterView.getItemAtPosition(position).toString()

            val intent = Intent()
            when(getSearchCd) {
                "0" -> {
                    intent.putExtra(SearchActivity.SELECT_COUNTRY, selectWord)
                }
                "1" -> {
                    intent.putExtra(SearchActivity.SELECT_CITY_1, selectWord)
                }
                "2" -> {
                    intent.putExtra(SearchActivity.SELECT_CITY_2, selectWord)
                }
            }
            setResult(Activity.RESULT_OK, intent)
            finish()

        }

        editText_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                scope.launch {
                    try {
                        //list初期化
                        listWords = mutableListOf()

                        val getWords = client.initIndex(indexName).searchForFacets(Attribute(attr), s.toString()).facets
                        if(getWords.size == 0 || getWords == null) {
                            listWords.add(getString(R.string.noData))
                        }
                        else {
                            getWords.forEachIndexed { index, s ->
                                listWords.add(getWords[index].value)
                            }
                        }

                        //listWords = listWords.distinct() as MutableList<String>
                        // adapterを作成します
                        val adapter = ArrayAdapter(
                            applicationContext,
                            android.R.layout.simple_list_item_1,
                            listWords
                        )

                        withContext(Dispatchers.Main) {
                            // adapterをlistViewに紐付けます。
                            listView_word.adapter = adapter
                        }



                    } catch (e: Exception ){
                        val dkew = e
                    }

                }
            }
        })

    }

}