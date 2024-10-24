package com.hhplus.ecommerce.common.dto

object AuthContext {
    private val data: ThreadLocal<JwtData> = ThreadLocal()

    fun setAuthData(authData: JwtData) {
        data.set(authData)
    }

    fun getAuthData(): JwtData? {
        return data.get()
    }

    fun clear() {
        data.remove()
    }
}