package com.example.findyourplace.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.filters
import com.algolia.search.dsl.query
import com.algolia.search.dsl.settings
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.example.findyourplace.Constants
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.example.findyourplace.activity.PostDetailActivity
import com.example.findyourplace.activity.SearchActivity
import com.example.findyourplace.adapter.MyAdapter
import com.example.findyourplace.function.changeUnit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.*


class ListFragment: Fragment() {

    //インスタンス
    val mApp = MyApplication.getInstance()
    val fs = FirebaseFirestore.getInstance()

    private val appInfo = activity!!.applicationContext.getPackageManager().getApplicationInfo(activity!!.packageName, PackageManager.GET_META_DATA)
    private val appID = ApplicationID(appInfo.metaData.getString("FYP_APP_ID_ALGOLIA").toString())
    private val apiKey = APIKey(appInfo.metaData.getString("FYP_API_KEY_ALGOLIA").toString())
    private val client = ClientSearch(appID, apiKey)
    private val indexName = IndexName("post")
    private val scope = CoroutineScope(Dispatchers.Default)

    //PostId変数
    lateinit var postIdList: List<Map<String, String>>
    var recAddFlg: Boolean = false
    var pageCount: Int = 1

    //recyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object {
        var SEARCH_GENRE: String = "searchGenre"
        var SEARCH_COUNTRY: String = "searchCountry"
        var SEARCH_CITY_1: String = "searchCity1"
        var SEARCH_CITY_2: String = "searchCity2"
        var SEARCH_KEYWORD: String = "searchKeyword"
    }

    enum class RequestCd(val cd: Int) {
        SEARCH(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val pLay = linear_search_word
            pLay.removeAllViews()
            if(resultCode == Activity.RESULT_OK) {

                val index = client.initIndex(indexName)

                when(requestCode and 0xffff) {
                    RequestCd.SEARCH.cd -> {
                        //現在のユーザーを共通関数に入れる
                        //linear_progress.visibility = View.VISIBLE
                        val resKw = mApp.resKeyword
                        var swList: MutableList<String> = mutableListOf()

                        data?.let {
                            val sGenre: String = it.getStringExtra(SEARCH_GENRE) ?: ""
                            if (sGenre != "") swList.add(sGenre)
                            val sCountry: String = it.getStringExtra(SEARCH_COUNTRY) ?: ""
                            if (sCountry != "") swList.add(sCountry)
                            val sCity1: String = it.getStringExtra(SEARCH_CITY_1) ?: ""
                            if (sCity1 != "") swList.add(sCity1)
                            val sCity2: String = it.getStringExtra(SEARCH_CITY_2) ?: ""
                            if (sCity2 != "") swList.add(sCity2)
                            val sKeyword: Array<String> = it.getStringArrayExtra(SEARCH_KEYWORD)

                            sKeyword.forEachIndexed { index, s ->
                                if (s != "") {
                                    val kId = Constants.resKeyword[s] as Int
                                    swList.add(resources.getString(kId))
                                }
                            }

                            val fq = query() {
                                filters {
                                    and {
                                        if (sGenre != "") facet("genre", sGenre)
                                        if (sCountry != "") facet("country", sCountry)
                                        if (sCity1 != "") facet("city1", sCity1)
                                        if (sCity2 != "") facet("city2", sCity2)
                                        if (sKeyword[0] != "") facet("keyWord", sKeyword[0])
                                        if (sKeyword[1] != "") facet("keyWord", sKeyword[1])
                                        if (sKeyword[2] != "") facet("keyWord", sKeyword[2])
                                        if (sKeyword[3] != "") facet("keyWord", sKeyword[3])
                                        if (sKeyword[4] != "") facet("keyWord", sKeyword[4])
                                        if (sKeyword[5] != "") facet("keyWord", sKeyword[5])
                                        if (sKeyword[6] != "") facet("keyWord", sKeyword[6])
                                        if (sKeyword[7] != "") facet("keyWord", sKeyword[7])
                                        if (sKeyword[8] != "") facet("keyWord", sKeyword[8])
                                        if (sKeyword[9] != "") facet("keyWord", sKeyword[9])
                                        if (sKeyword[10] != "") facet("keyWord", sKeyword[10])
                                        if (sKeyword[11] != "") facet("keyWord", sKeyword[11])
                                        if (sKeyword[12] != "") facet("keyWord", sKeyword[12])
                                        if (sKeyword[13] != "") facet("keyWord", sKeyword[13])
                                        if (sKeyword[14] != "") facet("keyWord", sKeyword[14])
                                        if (sKeyword[15] != "") facet("keyWord", sKeyword[15])
                                        if (sKeyword[16] != "") facet("keyWord", sKeyword[16])
                                        if (sKeyword[17] != "") facet("keyWord", sKeyword[17])
                                        if (sKeyword[18] != "") facet("keyWord", sKeyword[18])
                                        if (sKeyword[19] != "") facet("keyWord", sKeyword[19])
                                        if (sKeyword[20] != "") facet("keyWord", sKeyword[20])
                                        if (sKeyword[21] != "") facet("keyWord", sKeyword[21])
                                        if (sKeyword[22] != "") facet("keyWord", sKeyword[22])
                                    }
                                }
                            }

                            scope.launch {
                                try {
                                    val gIndex = index.search(fq)
                                    postIdList = gIndex.hitsOrNull as List<Map<String, String>>

                                    withContext(Dispatchers.Main) {
                                        progressBar_list_fragment.visibility = View.VISIBLE
                                        recyclerView = recyclerView_post_list.apply {
                                            // use this setting to improve performance if you know that changes
                                            // in content do not change the layout size of the RecyclerView
                                            setHasFixedSize(true)

                                            // use a linear layout manager
                                            layoutManager = viewManager

                                            viewAdapter = MyAdapter(postIdList,"","",
                                                activity!!.applicationContext
                                            )

                                            (viewAdapter as MyAdapter).setOnItemClickListener(object :
                                                MyAdapter.OnItemClickListener {
                                                override fun onItemClickListener(
                                                    view: View,
                                                    postId: String
                                                ) {
                                                    //クリックされたら画面遷移
                                                    val intent = Intent(
                                                        activity!!.applicationContext,
                                                        PostDetailActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        PostDetailActivity().POST_ID,
                                                        postId
                                                    )
                                                    startActivity(intent)
                                                }
                                            })

                                            // specify an viewAdapter (see also next example)
                                            adapter = viewAdapter

                                        }

                                        //テキストビュー追加
                                        val relLiner = linear_search_word
                                        swList.forEachIndexed { index, s ->
                                            val text = TextView(activity!!.applicationContext)
                                            text.text = s
                                            text.textSize = 16F
                                            //テキストの色
                                            text.setTextColor(resources.getColor(R.color.grey))
                                            //背景
                                            text.setBackgroundResource(R.drawable.background_grey_radius)
                                            relLiner.addView(text)
                                            //padding
                                            text.setPadding(
                                                changeUnit().pxToDp(
                                                    80,
                                                    activity!!.applicationContext
                                                ).toInt(),
                                                changeUnit().pxToDp(
                                                    8,
                                                    activity!!.applicationContext
                                                ).toInt(),
                                                changeUnit().pxToDp(
                                                    80,
                                                    activity!!.applicationContext
                                                ).toInt(),
                                                changeUnit().pxToDp(
                                                    8,
                                                    activity!!.applicationContext
                                                ).toInt()
                                            )
                                            //margin
                                            val mlp =
                                                text.layoutParams as ViewGroup.MarginLayoutParams
                                            mlp.setMargins(
                                                0,
                                                0,
                                                changeUnit().pxToDp(
                                                    50,
                                                    activity!!.applicationContext
                                                ).toInt(),
                                                0
                                            )
                                            text.layoutParams = mlp
                                        }

                                        progressBar_list_fragment.visibility = View.GONE
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        activity!!.applicationContext,
                                        "エラーが発生しました",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }


                    }
                }
            }


        } catch (e: Exception) {
            Toast.makeText(activity!!.applicationContext, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()

        scope.launch {
            withContext(Dispatchers.Main) { progressBar_list_fragment.visibility = View.VISIBLE }
            algoliaSearch()
            withContext(Dispatchers.Main) { progressBar_list_fragment.visibility = View.GONE }
        }

    }

    private fun initialize() {
        val pLay = linear_search_word
        pLay.removeAllViews()
    }

    private suspend fun algoliaSearch() {

        val index = client.initIndex(indexName)

        //取得数制限(一時的)
        val settings = settings {
            hitsPerPage = 2
        }
        index.setSettings(settings)

        val query = query() {
            page = 0
        }
        postIdList = index.search(query).hitsOrNull as List<Map<String, String>>

        withContext(Dispatchers.Main) {

            viewManager = object : LinearLayoutManager(activity!!.applicationContext) {
                override fun scrollVerticallyBy(
                    dx: Int,
                    recycler: Recycler,
                    state: RecyclerView.State
                ): Int {
                    val scrollRange = super.scrollVerticallyBy(dx, recycler, state)
                    val overScroll = dx - scrollRange
                    // 5 は検知するオーバースクロール量。適宜調整する
                    if (overScroll > 50) {
                        if(recAddFlg) return 0
                        Log.e("scrollRange", scrollRange.toString())
                        Log.e("dx", dx.toString())
                        // bottom edge over scroll
                        scope.launch {
                            recAddFlg = true

                            val query = query() {
                                page = pageCount
                            }
                            val addPostIdList = index.search(query).hitsOrNull as List<Map<String, String>>
                            if(addPostIdList.size == 0) return@launch

                            withContext(Dispatchers.Main) {
                                progressBar_bottom.visibility = View.VISIBLE
                                progressBar_bottom.alpha = 0.0F
                                progressBar_bottom.animate()
                                    .translationY(-200F)
                                    .alpha(1.0F)
                                    .setDuration(200)
                                recyclerTouchEnable(true)

                                (viewAdapter as MyAdapter).addItems(addPostIdList)
                                delay(2000)
                                recAddFlg = false
                                pageCount++

                                progressBar_bottom.visibility = View.GONE
                                progressBar_bottom.animate().translationY(0F)
                                recyclerTouchEnable(false)
                            }

                        }
                    } else if (overScroll < -5) {
                        // top edge over scroll
                    } else {
                        // reset scroll state
                    }
                    return scrollRange
                }
            }

            recyclerView = recyclerView_post_list.apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                viewAdapter = MyAdapter(postIdList, "", "", activity!!.applicationContext)

                (viewAdapter as MyAdapter).setOnItemClickListener(object :
                    MyAdapter.OnItemClickListener {
                    override fun onItemClickListener(view: View, postId: String) {
                        //クリックされたら画面遷移
                        val intent = Intent(
                            activity!!.applicationContext,
                            PostDetailActivity::class.java
                        )
                        intent.putExtra(PostDetailActivity().POST_ID, postId)
                        startActivity(intent)
                    }
                })

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }

        }

        //リスナー設定
        setListener()

    }

    private fun setListener() {
        button_search.setOnClickListener {
            val intent = Intent(activity!!.applicationContext, SearchActivity::class.java)
            startActivityForResult(intent, RequestCd.SEARCH.cd)
        }
    }

    private fun recyclerTouchEnable(enableFlg: Boolean) {
        recyclerView_post_list.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //enableFlgがtrueなら操作不可 falseなら操作可能
                return enableFlg
            }
        })
    }

}