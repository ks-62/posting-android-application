package com.example.findyourplace.dataClass

data class postClass(
    var postTitle: String? = "",
    var country: String = "",
    var city1: String = "",
    var city2: String = "",
    var city3: String = "",
    var genre: String = "",
    var imageUrl1: String = "",
    var imageUrl2: String? = null,
    var imageUrl3: String? = null,
    var imageUrl4: String? = null,
    var imageUrl5: String? = null,
    var imageUrl6: String? = null,
    var keyWord: MutableList<String> = mutableListOf(),
    var postContents: String? = "",
    var userName: String? = "",
    var postDate: String = ""
)