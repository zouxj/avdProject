package com.zxj.avdproject

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.readystatesoftware.chuck.internal.support.JsonConvertor
import java.io.Reader
import java.lang.reflect.Type


object Convert {
    private fun create(): Gson {
        return GsonHolder.gson
    }

  fun <T> fromJson(json: String?, type: Class<T>?): T {
        return create().fromJson(json, type)
    }

    fun <T> fromJson(json: String?, type: Type?): T {
        return create().fromJson(json, type)
    }

    fun <T> fromJson(reader: JsonReader, typeOfT: Type): T {
        return create().fromJson(reader, typeOfT)
    }

    fun <T> fromJson(json: Reader?, classOfT: Class<T>?): T {
        return create().fromJson(json, classOfT)
    }

    fun <T> fromJson(json: Reader?, typeOfT: Type?): T {
        return create().fromJson(json, typeOfT)
    }

    fun toJson(src: Any?): String {
        return create().toJson(src)
    }

    fun toJson(src: Any?, typeOfSrc: Type?): String {
        return create().toJson(src, typeOfSrc)
    }

    fun formatJson(json: String): String {
        return try {
            val jp = JsonParser()
            val je: JsonElement = jp.parse(json)
            JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            json
        }
    }

    fun formatJson(src: Any?): String? {
        return try {
            val jp = JsonParser()
            val je: JsonElement = jp.parse(toJson(src))
            JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            e.message
        }
    }


    private object GsonHolder {
         val gson: Gson = Gson()
    }
}