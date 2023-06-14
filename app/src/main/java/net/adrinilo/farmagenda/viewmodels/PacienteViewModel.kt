package net.adrinilo.farmagenda.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.adrinilo.farmagenda.model.Paciente
import net.adrinilo.farmagenda.views.dataStore

class PacienteViewModel (application: Application) : AndroidViewModel(application) {

    suspend fun saveCredentials(username: String, telefono: String, apellido1: String?, apellido2: String?, password: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("username")] = username
            preferences[stringPreferencesKey("telefono")] = telefono
            preferences[stringPreferencesKey("apellido1")] = apellido1 ?:""
            preferences[stringPreferencesKey("apellido2")] = apellido2 ?:""
            preferences[stringPreferencesKey("password")] = password
        }
    }

    suspend fun loadCredentials(context: Context): Paciente {
        var paciente = Paciente("", "", "", "", "")
        context.dataStore.data.map { preferences ->
            paciente = Paciente(
                nombre = preferences[stringPreferencesKey("username")].orEmpty(),
                telefono = preferences[stringPreferencesKey("telefono")].orEmpty(),
                apellido1 = preferences[stringPreferencesKey("apellido1")].orEmpty(),
                apellido2 = preferences[stringPreferencesKey("apellido2")].orEmpty(),
                password = preferences[stringPreferencesKey("password")].orEmpty()
            )
        }.first()
        return paciente
    }
}