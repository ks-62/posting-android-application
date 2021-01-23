package com.example.findyourplace.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.example.findyourplace.adapter.MyAdapter
import com.example.findyourplace.fragment.ListFragment
import com.example.findyourplace.fragment.PostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_post_list.*

class PostListActivity : AppCompatActivity() {

    //インスタンス
    val mApp = MyApplication.getInstance()
    val fs = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_post_list)

            val curUser = FirebaseAuth.getInstance().currentUser
            mApp.USER_ID = curUser?.uid
            button_profile.setText(mApp.USER_NAME)

            val storage = Firebase.storage

            initialize()

        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    private fun initialize() {

        val fragment = ListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.content_container, fragment)
        fragmentTransaction.commit()

        //リスナーセット
        setListener()
    }

    private fun setListener() {
        button_title.setOnClickListener {
            //使い方？とかそういうのを出す

        }
        button_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        button_list.setOnClickListener {
            val fragment = ListFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.content_container, fragment)
            fragmentTransaction.commit()
        }
        button_post.setOnClickListener {
            val fragment = PostFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.content_container, fragment)
            fragmentTransaction.commit()
        }
    }



}