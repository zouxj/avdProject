package com.zxj.avdproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.*
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.uitls.SharedPreferencesUtils
import com.zxj.avdproject.uitls.SharedPreferencesUtils.DEVICE_CODE
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register.setOnClickListener {
            if (username.text.toString().trim().isNotEmpty()) {
                register(username.text.toString().trim())
            } else {
                ToastUtil.showOne(this, "请输入设备号")
            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    /**
     * 注册
     */
    private fun register(deviceCode: String) {
        OkGo.post<String>("$URLS${ApiUrls.register}").params("deviceCode", deviceCode).tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    SharedPreferencesUtils.setParam(this@LoginActivity,DEVICE_CODE,deviceCode)
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }


}