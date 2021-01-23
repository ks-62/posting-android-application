package com.example.findyourplace.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    //インスタンス
    val mApp = MyApplication.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profile)



        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    private fun initialize() {

        textView_my_name.setText(mApp.USER_NAME)
        textView_my_post_count.setText(mApp.POST_COUNT.toString())
        button_profile.setText(mApp.USER_NAME)
    }

    private fun setListener() {
        button_post.setOnClickListener {
            try {
                val intent = Intent(this, PostActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
        button_sign_out.setOnClickListener {
            try {

            } catch (e: Exception) {
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
    }
}