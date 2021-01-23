package com.example.findyourplace.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.example.findyourplace.dataClass.userClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : AppCompatActivity() {

    val mApp = MyApplication.getInstance()
    //cloue fireStoreインスタンス
    val fs = FirebaseFirestore.getInstance()

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_nickname)

            //ユーザー情報を設定する前なので共通変数には入れない。
            user = FirebaseAuth.getInstance().currentUser

            setListener()

        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    private fun setListener() {
        button_nickname_submit.setOnClickListener {
            try {
                val nickname: String? = editText_nick_name.text.trim().toString()
                if(nickname == "") {
                    return@setOnClickListener
                }

                //ニックネーム登録
                createUser(nickname, user)

            } catch (e: Exception) {
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * ニックネーム登録処理
     */
    fun createUser(nName: String?, user: FirebaseUser?) {
        val uId = user?.uid
        val uName = nName
        val newUser = userClass(uName, 0)

        fs.collection("users")
        fs.collection("users").document(uId.toString())
            .set(newUser)
            .addOnSuccessListener {

                mApp.USER_ID = uId.toString()
                mApp.USER_NAME = newUser.userName.toString()
                mApp.POST_COUNT = newUser.postCount

                val intent = Intent(this, PostListActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                //登録失敗
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
    }


}