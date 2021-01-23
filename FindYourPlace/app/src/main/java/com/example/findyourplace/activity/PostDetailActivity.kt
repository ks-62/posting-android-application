package com.example.findyourplace.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.filters
import com.algolia.search.dsl.query
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostDetailActivity : AppCompatActivity() {

    //PostIdパラメーター
    var POST_ID: String = ""

    //インスタンス
    val mApp = MyApplication.getInstance()
    val storage = Firebase.storage
    //cloue fireStoreインスタンス
    val fs = FirebaseFirestore.getInstance()
    val appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val appID = ApplicationID(appInfo.metaData.getString("FYP_APP_ID_ALGOLIA").toString())
    val apiKey = APIKey(appInfo.metaData.getString("FYP_API_KEY_ALGOLIA").toString())
    private val client = ClientSearch(appID, apiKey)
    private val indexName = IndexName("post")
    private val scope = CoroutineScope(Dispatchers.Default)

    var getPostId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_post_detail)

            getPostId = intent.getStringExtra(POST_ID)?: ""

            val fq = query() {
                filters {
                    and {
                        facet("objectID", getPostId)
                    }
                }
            }

            scope.launch {
                val index = client.initIndex(indexName)
                val gIndex = index.search(fq)
                val mapData = gIndex.hitsOrNull!![0] as Map<String, String>

                withContext(Dispatchers.Main) {
                    textView_post_title.text = mapData["postTitle"].toString().trim('"')
                    //画像設定
                    for(i in 1..6) {
                        val tempImageUrl = "imageUrl" + i.toString()
                        if(mapData[tempImageUrl].toString().trim('"') != "null") {
                            var storageHttp = mapData[tempImageUrl].toString().trim('"')
                            val httpsReference = storage.getReferenceFromUrl(storageHttp)
                            when(i) {
                                1 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post1) }
                                2 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post2) }
                                3 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post3) }
                                4 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post4) }
                                5 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post5) }
                                6 -> { Glide.with(applicationContext).load(httpsReference).apply(
                                    RequestOptions()
                                        .dontTransform()).into(imageView_post6) }
                            }
                        } else {
                            when(i) {
                                1 -> { imageView_post1.visibility = View.GONE }
                                2 -> { imageView_post2.visibility = View.GONE }
                                3 -> { imageView_post3.visibility = View.GONE }
                                4 -> { imageView_post4.visibility = View.GONE }
                                5 -> { imageView_post5.visibility = View.GONE }
                                6 -> { imageView_post6.visibility = View.GONE }
                            }
                        }
                    }

                    textView_genre.text = mapData["genre"].toString().trim('"')
                    textView_post_content.text = mapData["postContents"].toString().trim('"')
                    var textCity: String = mapData["city1"].toString().trim('"')
                    textCity += mapData["city2"].toString().trim('"')
                    textCity += mapData["city3"].toString().trim('"')
                    textView_location.text = textCity
                    textView_user_name.text = mapData["userName"].toString().trim('"')
                    textView_post_date.text = mapData["postDate"].toString().trim('"')
                }
            }

            setListener()

        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    private fun setListener() {
        button_back.setOnClickListener {
            try {
                val intent = Intent(this, PostListActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
    }

}