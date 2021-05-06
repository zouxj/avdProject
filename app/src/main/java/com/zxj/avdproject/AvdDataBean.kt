package com.zxj.avdproject

/**
 *
 * @des:
 * @data: 12/13/20 5:24 AM
 * @Version: 1.0.0
 */
data class AvdDataBean(
    var imageRes: Int = 0,
    var imageUrl: String?="",
    var videoUrl: String?="",
    var title: String? = null,
    var viewType: Int = 0
)