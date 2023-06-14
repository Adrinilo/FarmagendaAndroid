package net.adrinilo.farmagenda.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.adrinilo.farmagenda.api.MedRepository
import net.adrinilo.farmagenda.model.Medicamento

class MedicamentoViewModel: ViewModel() {

    private var repository: MedRepository = MedRepository()

    fun getMedicamentosByNombre(nombre: String): MutableLiveData<List<Medicamento>> {
        val medicamentos = MutableLiveData<List<Medicamento>>()
        GlobalScope.launch(Main) {
            medicamentos.value = repository.getMedicamentosByNombre(nombre)
        }
        return medicamentos
    }

    fun getMedicamentosByNregistro(nregistro: String): MutableLiveData<List<Medicamento>> {
        val medicamentos = MutableLiveData<List<Medicamento>>()
        GlobalScope.launch(Main) {
            medicamentos.value = repository.getMedicamentosByNregistro(nregistro)
        }
        return medicamentos
    }
}