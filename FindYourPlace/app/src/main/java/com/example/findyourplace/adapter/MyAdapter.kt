package com.example.findyourplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findyourplace.R
import com.example.findyourplace.function.changeUnit
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.recyclerview_card.view.*


class MyAdapter
    (
    private var myDataset: List<Map<String, String>>,
    private val searchCountry: String,
    private val searchLocation: String,
    private val context: Context
): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // リスナー格納変数
    lateinit var itemClickListener: OnItemClickListener

    val storage = Firebase.storage

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_card, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        //...
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val getData = myDataset[position]

        val margin = changeUnit().pxToDp(100, context).toInt()
        val getView = holder.view
        getView.visibility = View.VISIBLE
        getView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        var dr = getView.layoutParams as ViewGroup.MarginLayoutParams
        dr.setMargins(
            0,
            margin,
            0,
            margin
        )

        getView.textView_post_title.text = getData["postTitle"].toString().trim('"')
        getView.textView_post_content.text = getData["postContents"].toString().trim('"')
        val stImgUrl = storage.getReferenceFromUrl(getData["imageUrl1"].toString().trim('"'))
        Glide.with(getView).load(stImgUrl).into(getView.imageView_post1)

        holder.view.textView_location.text = getData["location"].toString().trim('"')
        holder.view.textView_user_name.text = getData["userName"].toString().trim('"')

        // タップしたとき
        holder.view.setOnClickListener {
            itemClickListener.onItemClickListener(
                it, getData["objectID"].toString().trim(
                    '"'
                )
            )
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun addItems(items: List<Map<String, String>>) {
        myDataset = myDataset + items
        notifyDataSetChanged()
    }

    //インターフェースの作成
    interface OnItemClickListener{
        fun onItemClickListener(view: View, postId: String)
    }

    // リスナー
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.itemClickListener = listener
    }

}