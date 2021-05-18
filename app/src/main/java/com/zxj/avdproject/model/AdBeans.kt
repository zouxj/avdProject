package com.zxj.avdproject.model


data class AdBeans(
    val payload: List<Template>,
    val success: Boolean

) {
    override fun toString(): String {
        return "AdBeans(payload=$payload, success=$success)"
    }
}

data class Template(
    val adId: Int,
    val adName: String,
    val template: TemplateBean,
    val viewType:Int=1

) {
    override fun toString(): String {
        return "Template(adId=$adId, adName='$adName', template=$template, viewType=$viewType)"
    }
}

data class TemplateBean(
    val endTime: String,
    val img: String,
    val imgHeight: Int,
    val imgWidth: Int,
    val sortType: Int,
    val startTime: String,
    var video: String,
    val videoHeight: Int,
    val videoWidth: Int

) {
    override fun toString(): String {
        return "TemplateBean(endTime='$endTime', img='$img', imgHeight=$imgHeight, imgWidth=$imgWidth, sortType=$sortType, startTime='$startTime', video='$video', videoHeight=$videoHeight, videoWidth=$videoWidth)"
    }
}