package com.zxj.netlibrary

import android.content.Context
import kotlin.properties.Delegates

/**
 *
 * @des:
 * @data: 12/12/20 4:41 PM
 * @Version: 1.0.0
 */
class NetContext {
    companion object {
        val instance: NetContext by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetContext()
        }

    }
    var CONTEXT: Context by Delegates.notNull()
    
    fun getContext(): Context {
        return CONTEXT
    }

}