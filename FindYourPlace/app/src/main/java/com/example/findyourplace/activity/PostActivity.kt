package com.example.findyourplace.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.findyourplace.MyApplication.MyApplication
import com.example.findyourplace.R
import com.example.findyourplace.adapter.genreAdapter
import com.example.findyourplace.dataClass.genreClass
import com.example.findyourplace.dataClass.postClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.button_list
import kotlinx.android.synthetic.main.activity_post.button_profile
import kotlinx.android.synthetic.main.activity_post.button_title
import kotlinx.android.synthetic.main.activity_post.linear_progress
import kotlinx.android.synthetic.main.activity_post_list.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : AppCompatActivity() {

    //インスタンス
    val mApp = MyApplication.getInstance()
    val fs = FirebaseFirestore.getInstance()
    val storage = Firebase.storage
    var storageRef = storage.reference

    var FILE_NAME: String = ""
    //メイン画像用
    lateinit var MAIN_BIT_MAP: Bitmap
    lateinit var MAIN_URI_LIST: Uri
    //サブ画像用
    var SUB_BIT_MAP: MutableList<Bitmap> = mutableListOf()
    var SUB_URI_LIST = mutableListOf<Uri>()

    //ダウンロードURL変数
    var DOWNLOAD_URI: MutableList<String> = mutableListOf()

    enum class RequestCd(val cd: Int) {
        MAIN_IMAGE(0),
        SUB_IMAGE(1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            RequestCd.MAIN_IMAGE.cd -> {
                try {
                    resultData?.data?.also { uri ->
                        //今回選択されもファイル専用の変数作成
                        var mainInputStream: InputStream? = null

                        //選択された画像が一つの場合
                        MAIN_URI_LIST = uri
                        mainInputStream = contentResolver?.openInputStream(MAIN_URI_LIST)
                        MAIN_BIT_MAP = BitmapFactory.decodeStream(mainInputStream)
                        MAIN_BIT_MAP = rotateImageIfRequired(MAIN_BIT_MAP, MAIN_URI_LIST)

                        //画像を表示
                        imageView_post1.setImageBitmap(MAIN_BIT_MAP)
                        textView_img1.visibility = View.INVISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
            RequestCd.SUB_IMAGE.cd -> {
                try {
                    var itemCount = resultData?.clipData?.itemCount ?:0

                    //imageViewを取得
                    val imgViewList: MutableList<ImageView> = mutableListOf()
                    imgViewList.add(imageView_post2)
                    imgViewList.add(imageView_post3)
                    imgViewList.add(imageView_post4)
                    imgViewList.add(imageView_post5)
                    imgViewList.add(imageView_post6)
                    val txtViewImgList: MutableList<TextView> = mutableListOf()
                    txtViewImgList.add(textView_img2)
                    txtViewImgList.add(textView_img3)
                    txtViewImgList.add(textView_img4)
                    txtViewImgList.add(textView_img5)
                    txtViewImgList.add(textView_img6)

                    if(resultData?.clipData == null) {
                        //選択された画像が一つの場合
                        resultData?.data?.also { uri ->
                            //現在の画像登録数をチェック
                            if(SUB_URI_LIST.size >= 5) {
                                //一番古い画像から削除していく
                                SUB_URI_LIST.removeAt(0)
                                SUB_BIT_MAP.removeAt(0)
                            }

                            //今回選択されもファイル専用の変数作成
                            var subUriList: MutableList<Uri> = mutableListOf()
                            var subInputStream: MutableList<InputStream?> = mutableListOf()
                            var subBitmap: MutableList<Bitmap> = mutableListOf()

                            //今回選択した画像を登録
                            subUriList.add(uri)
                            subInputStream.add(contentResolver?.openInputStream(subUriList[subUriList.lastIndex]))
                            subBitmap.add(BitmapFactory.decodeStream(subInputStream[subInputStream.lastIndex]))

                            subUriList.forEachIndexed { index, uri ->
                                //グローバル変数へ代入
                                subBitmap[index] = rotateImageIfRequired(subBitmap[index], subUriList[index])
                                SUB_URI_LIST.add(subUriList[index])
                                SUB_BIT_MAP.add(subBitmap[index])
                            }

                            //画像表示
                            SUB_URI_LIST.forEachIndexed { index, bitmap ->
                                //画像を表示
                                imgViewList[index].setImageBitmap(SUB_BIT_MAP[index])
                            }
                        }
                    } else {
                        //6つ以上画像を選択していたら処理を抜ける
                        if(itemCount > 5){
                            Toast.makeText(this, "サブ画像は5つまでしか選択できません", Toast.LENGTH_LONG).show()
                            return
                        }

                        //今回選択されもファイル専用の変数作成
                        var subUriList: MutableList<Uri> = mutableListOf()
                        var subInputStream: MutableList<InputStream?> = mutableListOf()
                        var subBitmap: MutableList<Bitmap> = mutableListOf()

                        //複数ファイルが選択された場合
                        for(i in 0..itemCount - 1) {
                            var uri = resultData?.clipData?.getItemAt(i)?.uri
                            //現在の画像登録数をチェック
                            if(SUB_URI_LIST.size >= 5) {
                                //一番古い画像から削除していく
                                SUB_URI_LIST.removeAt(0)
                                SUB_BIT_MAP.removeAt(0)
                            }

                            uri?.let {
                                subUriList.add(it)
                                subInputStream.add(contentResolver?.openInputStream(subUriList[subUriList.lastIndex]))
                                subBitmap.add(BitmapFactory.decodeStream(subInputStream[subInputStream.lastIndex]))
                            }
                        }

                        subUriList.forEachIndexed { index, uri ->
                            //グローバル変数へ代入
                            subBitmap[index] = rotateImageIfRequired(subBitmap[index], subUriList[index])
                            SUB_URI_LIST.add(subUriList[index])
                            SUB_BIT_MAP.add(subBitmap[index])
                        }

                        SUB_URI_LIST.forEachIndexed { index, uri ->
                            //画像表示
                            imgViewList[index].setImageBitmap(SUB_BIT_MAP[index])
                            txtViewImgList[index].visibility = View.INVISIBLE
                        }

                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_post)

            initialize()

        } catch (e: Exception) {

        }
    }

    private fun initialize() {

        button_profile.setText(mApp.USER_NAME)

        //スピナーセット
        spinnerSet()

        setListener()
    }

    private fun setListener() {
        button_title.setOnClickListener {

        }
        button_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        button_list.setOnClickListener {
            val intent = Intent(this, PostListActivity::class.java)
            startActivity(intent)
        }
        button_post_submit.setOnClickListener {
            val inputBm: MutableList<Bitmap> = mutableListOf()
            val inputUri: MutableList<Uri> = mutableListOf()

            inputBm.add(MAIN_BIT_MAP)
            SUB_BIT_MAP.forEachIndexed { index, bitmap ->
                inputBm.add(bitmap)
            }

            inputUri.add(MAIN_URI_LIST)
            SUB_URI_LIST.forEachIndexed { index, uri ->
                inputUri.add(uri)
            }
            BitmapToBaos(inputBm, inputUri)
        }
        imageView_post1.setOnClickListener {
            selectMainImage()
        }
        imageView_post2.setOnClickListener { selectSubImage() }
        imageView_post3.setOnClickListener { selectSubImage() }
        imageView_post4.setOnClickListener { selectSubImage() }
        imageView_post5.setOnClickListener { selectSubImage() }
        imageView_post6.setOnClickListener { selectSubImage() }

        }

    private fun spinnerSet() {
        val genreList: ArrayList<genreClass?> = arrayListOf()
        val strGenre: Array<String> = resources.getStringArray(R.array.genreArray)
        genreList.add(genreClass("", strGenre[0]))
        genreList.add(genreClass("001", strGenre[1]))
        genreList.add(genreClass("002", strGenre[2]))
        genreList.add(genreClass("003", strGenre[3]))
        genreList.add(genreClass("004", strGenre[4]))
        genreList.add(genreClass("005", strGenre[5]))

        val adapter = genreAdapter(this, genreList)
        spinner_genre.adapter = adapter

    }

    private fun insertRD(dlUri: List<String>) {

        //登録する情報
        //登録する情報
        val insrtTtl = editText_post_title.text.toString()
        val insrtCntry = editText_country.text.toString()
        val insrtCity1 = editText_city_1.text.toString()
        val insrtCity2 = editText_city_2.text.toString()
        val insrtCity3 = editText_city_3.text.toString()
        val insrtGenre = (spinner_genre.selectedItem as genreClass).gCode
        val insrtKwd: MutableList<String> = mutableListOf()
        val insrtCntnt = editText_post_content.text.toString()
        val insrtDate = android.text.format.DateFormat.format("yyyy/MM/dd kk:mm:ss", Date()).toString()
        val insrtName = mApp.USER_NAME

        if(checkBox_cd_mts.isChecked) insrtKwd.add("CD_MTS")
        if(checkBox_cd_fsn.isChecked) insrtKwd.add("CD_FSN")
        if(checkBox_cd_fun.isChecked) insrtKwd.add("CD_FUN")
        if(checkBox_cd_bty.isChecked) insrtKwd.add("CD_BTY")
        if(checkBox_cd_chl.isChecked) insrtKwd.add("CD_CHL")
        if(checkBox_cd_aln.isChecked) insrtKwd.add("CD_ALN")
        if(checkBox_cd_fnt.isChecked) insrtKwd.add("CD_FNT")
        if(checkBox_cd_sea.isChecked) insrtKwd.add("CD_SEA")
        if(checkBox_cd_mnt.isChecked) insrtKwd.add("CD_MNT")
        if(checkBox_cd_grn.isChecked) insrtKwd.add("CD_GRN")
        if(checkBox_cd_ntr.isChecked) insrtKwd.add("CD_NTR")
        if(checkBox_cd_rvr.isChecked) insrtKwd.add("CD_RVR")
        if(checkBox_cd_uep.isChecked) insrtKwd.add("CD_UEP")
        if(checkBox_cd_nst.isChecked) insrtKwd.add("CD_NST")
        if(checkBox_cd_opn.isChecked) insrtKwd.add("CD_OPN")
        if(checkBox_cd_ext.isChecked) insrtKwd.add("CD_EXT")
        if(checkBox_cd_ftr.isChecked) insrtKwd.add("CD_FTR")
        if(checkBox_cd_wlk.isChecked) insrtKwd.add("CD_WLK")
        if(checkBox_cd_rlx.isChecked) insrtKwd.add("CD_RLX")
        if(checkBox_cd_ktt.isChecked) insrtKwd.add("CD_KTT")
        if(checkBox_cd_lts.isChecked) insrtKwd.add("CD_LTS")
        if(checkBox_cd_acs.isChecked) insrtKwd.add("CD_ACS")
        if(checkBox_cd_hgh.isChecked) insrtKwd.add("CD_HGH")

        var newPost: postClass = postClass()

        when(DOWNLOAD_URI.size) {
            1 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0],null,null,null,null,null,
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                ) }
            2 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0], dlUri[1],null,null,null,null,
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                )
            }
            3 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0], dlUri[1], dlUri[2],null,null,null,
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                )
            }
            4 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0], dlUri[1], dlUri[2], dlUri[3],null,null,
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                )
            }
            5 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0], dlUri[1], dlUri[2], dlUri[3], dlUri[4],null,
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                )
            }
            6 -> {
                newPost = postClass(
                    insrtTtl,
                    insrtCntry,
                    insrtCity1,
                    insrtCity2,
                    insrtCity3,
                    insrtGenre,
                    dlUri[0], dlUri[1], dlUri[2], dlUri[3], dlUri[4], dlUri[5],
                    insrtKwd,
                    insrtCntnt,
                    insrtName,
                    insrtDate
                )
            }
        }

        fs.collection("post")
            .add(newPost)
            .addOnSuccessListener {
                val aa = ""
            }
            .addOnFailureListener {
                val aa = ""
            }

        //プログレスバー解除
        linear_progress.visibility = View.GONE
    }

    /**
     * メイン画像選択
     */
    private fun selectMainImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.CATEGORY_OPENABLE, true)
        startActivityForResult(Intent.createChooser(intent, "画像を選択"), RequestCd.MAIN_IMAGE.cd)
    }

    /**
     * サブ画像選択
     */
    private fun selectSubImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, RequestCd.SUB_IMAGE.cd)
    }

    /**
     * 登録処理
     */
    private fun uploadFunction(BAOS:MutableList<ByteArrayOutputStream>, fileName: MutableList<String>) {
        //登録処理
        BAOS.forEachIndexed { index, s ->
            //画像登録
            val data = BAOS[index].toByteArray()
            var imgRef = storageRef.child("images/" + fileName[index])
            var uploadTask = imgRef.putBytes(data)
            uploadStorage(BAOS, imgRef, uploadTask, index)
        }
    }

    /**
     * cloud storage にアップロード
     */
    private fun uploadStorage(baos: MutableList<ByteArrayOutputStream>, imageRef: StorageReference, uploadTask: UploadTask, index: Int) {
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(index == 0) {
                    DOWNLOAD_URI.add(0, task.result.toString())
                } else {
                    DOWNLOAD_URI.add(task.result.toString())
                }

                //全ての画像をアップロードしたらrealtime databaseにアップロードする
                if(DOWNLOAD_URI.size == baos.size) {
                    insertRD(DOWNLOAD_URI)
                }

            } else {
                // Handle failures
            }
        }
    }

    private fun BitmapToBaos(bm: MutableList<Bitmap>, uriList: MutableList<Uri>) {
        //入力データの確認
        if(bm.size != uriList.size) {
            Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
            return
        }
        val fileName: MutableList<String> = mutableListOf()
        val BAOS:MutableList<ByteArrayOutputStream> = mutableListOf()

        bm.forEachIndexed { index, bitmap ->

            //ファイル名作成
            fileName.add(UUID.randomUUID().toString())
            BAOS.add(ByteArrayOutputStream())

            bm[index]?.compress(Bitmap.CompressFormat.JPEG, 100, BAOS[index])
        }

        //プログレスバー表示
        linear_progress.visibility = View.VISIBLE
        //登録処理
        uploadFunction(BAOS, fileName)
    }

    /**
     * 画像の向きを調整する
     */
    @Throws(IOException::class)
    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val ei = ExifInterface(fileDescriptor)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        parcelFileDescriptor?.close()
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedImg
    }
}