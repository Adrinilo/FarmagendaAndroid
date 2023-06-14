package net.adrinilo.farmagenda.api;

import kotlinx.coroutines.Deferred
import net.adrinilo.farmagenda.model.Respuesta
import retrofit2.Response
import retrofit2.http.*

interface MedicineService {
    @GET("medicamentos")
    fun getMedicamentosByNombre(
        @Query("nombre") nombre: String): Deferred<Response<Respuesta>>

    @GET("medicamentos")
    fun getMedicamentosByNregistro(
        @Query("nregistro") nregistro: String): Deferred<Response<Respuesta>>
}