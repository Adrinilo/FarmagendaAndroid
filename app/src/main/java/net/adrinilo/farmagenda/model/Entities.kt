package net.adrinilo.farmagenda.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Paciente(
    var telefono: String,
    var nombre: String,
    var apellido1: String?,
    var apellido2: String?,
    var password: String
) : Serializable


data class Respuesta(
    val resultados: List<Medicamento>
) : Serializable


data class Medicamento(
    var nregistro: String,
    @SerializedName("nombre")
    val descripcion: String,
    @SerializedName("vtm")
    var nombre: Vtm,
    var dosis: String,
    var labtitular: String,
    @SerializedName("formaFarmaceuticaSimplificada")
    var ffs: Ffs,
    @SerializedName("viasAdministracion")
    var administracion: List<Vadmin>,
    var fotos: List<Foto>,
    var docs: List<Doc>
) : Serializable

data class Vtm(
    var nombre: String
) : Serializable

data class Ffs(
    var texto: String
) : Serializable

data class Vadmin(
    var texto: String
) : Serializable

data class Foto(
    var url: String
) : Serializable

data class Doc(
    @SerializedName("urlHtml")
    var prospecto: String
) : Serializable


data class Calendario(
    var idtratamiento: String,
    var date: String,
    var hora: String
) : Serializable


data class Toma(
    var nregistro: String,
    @SerializedName("nombre")
    val descripcion: String,
    @SerializedName("vtm")
    var nombre: Vtm,
    var dosis: String,
    var labtitular: String,
    @SerializedName("formaFarmaceuticaSimplificada")
    var ffs: Ffs,
    @SerializedName("viasAdministracion")
    var administracion: List<Vadmin>,
    var fotos: List<Foto>,
    var docs: List<Doc>,
    var hora: String,
    var fecha: String
) : Serializable
