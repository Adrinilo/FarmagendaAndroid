package net.adrinilo.farmagenda.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.databinding.FragmentUserBinding
import net.adrinilo.farmagenda.model.Paciente
import net.adrinilo.farmagenda.viewmodels.PacienteViewModel
import net.adrinilo.farmagenda.views.dataStore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var pacienteViewModel: PacienteViewModel
    private lateinit var user: Paciente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pacienteViewModel = ViewModelProvider(this)[PacienteViewModel::class.java]
        lifecycleScope.launch {
            user = getUser()
            setData()
        }
        binding.btnlogout.setOnClickListener {
            clearDataStored()
            findNavController().navigate(R.id.action_userFragment_to_loginFragment)
        }
    }

    private fun setData() {
        val etusernombre = requireView().findViewById<EditText>(R.id.etusernombre)
        val etuserapellido1 = requireView().findViewById<EditText>(R.id.etuserapellido1)
        val etuserapellido2 = requireView().findViewById<EditText>(R.id.etuserapellido2)
        val etusertelefono = requireView().findViewById<EditText>(R.id.etusertelefono)

        etusernombre.setText(user.nombre)
        etuserapellido1.setText(user.apellido1)
        etuserapellido2.setText(user.apellido2)
        etusertelefono.setText(user.telefono)
    }

    private suspend fun getUser(): Paciente = withContext(Dispatchers.IO) {
        pacienteViewModel.loadCredentials(requireContext())
    }

    fun clearDataStored() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dataStore = requireContext().dataStore

            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }
}