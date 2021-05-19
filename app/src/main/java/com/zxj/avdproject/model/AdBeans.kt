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
    val viewType: Int = 1

) {
    override fun toString(): String {
        return "Template(adId=$adId, adName='$adName', template=$template, viewType=$viewType)"
    }
}

data class TemplateBean(
    val screenNum: Int = 0,
    val sortType: Int = 0,
    val height: Int = 0,
    val width: Int = 0,
    val topHeight: Int = 0,
    val middleHeight: Int = 0,
    val bottomHeight: Int = 0,
    val top: String? = null,
    val middle: String? = null,
    val bottom: String? = null,
    val startTime: String? = null,
    val endTime: String? = null

    ) {


}