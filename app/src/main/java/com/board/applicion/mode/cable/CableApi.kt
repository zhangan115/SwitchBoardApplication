package com.board.applicion.mode.cable

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CableApi {

    /**
     * 1,获取电缆列表
     */
    @GET("api/cable/listPage")
    fun getCableList(@QueryMap() requestMap: Map<String, String>): Call<CableBaseEntity<List<CableBean>>>

}