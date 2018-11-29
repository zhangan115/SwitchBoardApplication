package com.board.applicion.mode.cable

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CableApi {


    /**
     * 1,获取配电室和主控室列表
     */
    @GET("voms/api/app/substation/list.json")
    fun getSubList(): Call<CableBaseEntity<List<SubstationBean>>>

    /**
     * 2,获取主控室屏柜列表
     */
    @GET("voms/api/app/cabinet/list.json")
    fun getCabinetList(@Query("mcrId") roomId: Long): Call<CableBaseEntity<List<CabinetBean>>>

    /**
     * 3,获取电缆列表
     */
    @GET("voms/api/app/cable/list.json")
    fun getCableList(@Query("stCabinetId") stCabinetId: Long): Call<CableBaseEntity<List<CableBean>>>


}