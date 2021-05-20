package com.zxj.avdproject.model

data class QrCodeBean (val payload: Payload ){
    data  class Payload(  val qrCode:String)
}