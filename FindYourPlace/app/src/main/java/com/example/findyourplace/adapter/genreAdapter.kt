package com.example.findyourplace.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.findyourplace.R
import com.example.findyourplace.dataClass.genreClass

class genreAdapter : ArrayAdapter<genreClass?> {
    constructor(context: Context) : super(context, android.R.layout.simple_spinner_item) {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    constructor(context: Context, list: MutableList<genreClass?>) : super(
        context,
        android.R.layout.simple_spinner_item,
        list
    ) {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.setText(getItem(position)?.gName)
        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = super.getDropDownView(position, convertView, parent) as TextView
        textView.setText(getItem(position)?.gName)
        return textView
    }
}