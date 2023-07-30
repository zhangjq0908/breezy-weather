package org.breezyweather.sources.here

import io.reactivex.rxjava3.core.Observable
import org.breezyweather.sources.here.json.HereGeocodingResult
import retrofit2.http.GET
import retrofit2.http.Query

interface HereRevGeocodingApi {
    @GET("v1/revgeocode")
    fun revGeoCode(
        @Query("apiKey") apikey: String,
        @Query("at") query: String,
        @Query("types") types: String,
        @Query("limit") limit: Int,
        @Query("lang") language: String,
        @Query("show") show: String
    ): Observable<HereGeocodingResult>
}