package com.aboutcapsule.android.util

import com.aboutcapsule.android.service.CapsuleService
import com.aboutcapsule.android.service.MemberService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    companion object {
        private const val BASE_URL = "http://k8b302.p.ssafy.io:8000/"
        private const val memberPort = "api/member/"
        private const val capsulePort = "api/capsule/"
        private const val chatPort = ""
    }
        val getMemberRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL+ memberPort)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    object memberInstacne {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL+ memberPort)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api: MemberService by lazy {
            retrofit.create(MemberService::class.java)
        }
    }
    object capsuleInstance {
        private val retrofit by lazy{
            Retrofit.Builder()
                .baseUrl(BASE_URL + capsulePort)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api: CapsuleService by lazy{
            retrofit.create(CapsuleService::class.java)
        }
    }

}