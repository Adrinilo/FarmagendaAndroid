package net.adrinilo.farmagenda.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.databinding.FragmentInicioBinding
import net.adrinilo.farmagenda.viewmodels.PacienteViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val pacienteViewModel: PacienteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvwelcome = binding.tvwelcome

        viewLifecycleOwner.lifecycleScope.launch {
             withContext(Dispatchers.Main) {
                 val username = getUsername()
                 tvwelcome.text = "Bienvenido $username"
             }
        }

        binding.btncalendario.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_diaFragment)
        }

        binding.btnnewtratamiento.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_tratamientoFragment)
        }

        binding.btncita.setOnClickListener {
            val url = "https://sescam.jccm.es/portalsalud/#/app/citas"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.btnusercount.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_userFragment)
        }
    }

    private suspend fun getUsername(): String = suspendCoroutine { continuation ->
        lifecycleScope.launch(Dispatchers.IO) {
            val username = pacienteViewModel.loadCredentials(requireContext()).nombre
            withContext(Dispatchers.Main) {
                continuation.resume(username)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}