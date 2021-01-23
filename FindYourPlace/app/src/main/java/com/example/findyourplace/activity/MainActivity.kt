package com.example.findyourplace.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    var mAuth : FirebaseAuth? = null
    var mGoogleSignInClient : GoogleSignInClient? = null

    //インスタンス
    val mApp = MyApplication.getInstance()

    enum class RequestCd(val cd: Int) {
        SING_IN(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            //初期化
            initialize()

        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    //Googleサインイン画面から戻ってきたときに実行される。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this, "アカウントが見つかりませんでした。", Toast.LENGTH_LONG).show()
        }
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCd.SING_IN.cd){
                var task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    var account = task.result

                    //Googleアカウントの情報が取得できた際の処理
                    if (account != null) firebaseAuthWithGoogle(account)

                    //mAuthのuidを使って、すでにrealtimedatabeseにユーザー登録しているかどうかチェック
                    //val ref = FirebaseDatabase.getInstance()
                    val ref = FirebaseFirestore.getInstance()
                    //val checkId = ref.getReference("/users/" + mAuth?.uid)
                    ref.collection("users").document(mAuth?.uid.toString())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.data == null) {
                                val intent = Intent(this, NicknameActivity::class.java)
                                startActivity(intent)
                            } else {
                                mApp.USER_ID = mAuth?.uid.toString()
                                mApp.USER_NAME = (document.data as Map<String, String>)["userName"].toString()
                                mApp.POST_COUNT = (document.data as Map<String, String>)["postCount"].toString().toInt()
                                val intent = Intent(this, PostListActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                        }

                }catch (e : ApiException){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    /**
     * 初期化処理
     */
    private fun initialize() {
        try {
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mAuth = FirebaseAuth.getInstance()

            //リスナー設定
            setListener()

        } catch (e: Exception) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * リスナー設定
     */
    private fun setListener() {
        button_login.setOnClickListener {
            try {
                signIn()
            } catch (e: Exception) {
                Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * サインイン
     */
    private fun signIn() {
        var googleSignInIntent  = mGoogleSignInClient?.signInIntent
        //Googleサインイン画面に遷移
        startActivityForResult(googleSignInIntent, RequestCd.SING_IN.cd)
    }

    /**
     * Googleアカウント情報を元に、firebaseで認証する。
     */
    private fun firebaseAuthWithGoogle(acct : GoogleSignInAccount){

        var credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                //認証成功時の処理
                if (it.isSuccessful){
                    Log.d("currentUser", mAuth?.currentUser.toString())
                }//認証失敗時の処理
                else{

                }
            }
    }

}