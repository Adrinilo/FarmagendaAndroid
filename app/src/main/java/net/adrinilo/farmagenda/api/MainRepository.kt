package net.adrinilo.farmagenda.api

import net.adrinilo.farmagenda.model.Medicamento

class MedRepository() {
    val service = WebAccess.medicineService

    suspend fun getMedicamentosByNombre(nombre: String): List<Medicamento> {
        val webResponse = service.getMedicamentosByNombre(nombre).await()
        if (webResponse.isSuccessful) {
            return webResponse.body()!!.resultados
        }
        return emptyList()
    }

    suspend fun getMedicamentosByNregistro(nregistro: String): List<Medicamento> {
        val webResponse = service.getMedicamentosByNregistro(nregistro).await()
        if (webResponse.isSuccessful) {
            return webResponse.body()!!.resultados
        }
        return emptyList()
    }
}
