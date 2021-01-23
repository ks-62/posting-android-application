package com.example.findyourplace.function

import android.content.Context

class changeUnit {

    fun dpTopx(dp: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return dp * metrics.density
    }

    fun pxToDp(px: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return px / metrics.density
    }
}