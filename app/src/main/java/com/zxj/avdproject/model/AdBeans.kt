package com.zxj.avdproject.model

data class AdBeans(
    val payload: List<Template>,
    val success: Boolean
)

data class Template(
    val adId: Int,
    val adName: String,
    val template: TemplateBean,
    val viewType:Int=1
)

data class TemplateBean(
    val endTime: String,
    val img: String,
    val imgHeight: Int,
    val imgWidth: Int,
    val sortType: Int,
    val startTime: String,
    val video: String,
    val videoHeight: Int,
    val videoWidth: Int
)