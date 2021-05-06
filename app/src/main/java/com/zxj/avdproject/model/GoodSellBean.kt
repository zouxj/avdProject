package com.zxj.avdproject.model

data class GoodSellBean(val success: Boolean, val requestId: String, val payload: Payload)

class Payload {
    val orderId: String = ""
}