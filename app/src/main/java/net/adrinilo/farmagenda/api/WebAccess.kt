package net.adrinilo.farmagenda.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WebAccess {

    val medicineService : MedicineService by lazy {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("https://cima.aemps.es/cima/rest/")
            .build()

        return@lazy retrofit.create(MedicineService::class.java)
    }
}
