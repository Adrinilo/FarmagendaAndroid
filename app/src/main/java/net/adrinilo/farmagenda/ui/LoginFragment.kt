package net.adrinilo.farmagenda.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.databinding.FragmentLoginBinding
import net.adrinilo.farmagenda.viewmodels.PacienteViewModel
import net.adrinilo.farmagenda.views.MainActivity.Companion.TAG


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var pacienteViewModel: PacienteViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var username: String
    private lateinit var apellido1: String
    private lateinit var apellido2: String
    private lateinit var telefono: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pacienteViewModel = ViewModelProvider(this)[PacienteViewModel::class.java]
        db = FirebaseFirestore.getInstance()

        binding.btnlogin.setOnClickListener {
            //Toast.makeText(context, "Prueba Listener", Toast.LENGTH_SHORT).show()
            getData()
        }
    }

    private fun getData() {
        val etusername = binding.etusername
        val etapellido1 = binding.etapellido1
        val etapellido2 = binding.etapellido2
        val ettelefono = binding.ettelefono
        val etpassword = binding.etpassword

        username = etusername.text.toString()
        apellido1 = etapellido1.text.toString()
        apellido2 = etapellido2.text.toString()
        telefono = ettelefono.text.toString()
        password = etpassword.text.toString()

        if (telefono != "" && password != "") {
            verifyCredentials()
        } else if (telefono == "") {
            Toast.makeText(requireContext(), "Campo Telefono obligatorio", Toast.LENGTH_SHORT)
                .show()
        } else if (password == "") {
            Toast.makeText(requireContext(), "Campo Contraseña obligatorio", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Guarda el usuario en Firebase
    private fun verifyCredentials() {
        // Comprobamos si existe el usuario en la coleccion que almacena los identificadores
        db.collection("pacientes").document(telefono).get()
            .addOnSuccessListener { documentSnapshot ->
                // si la operación ha sido correcta independientemente del resultado obtenido
                if (documentSnapshot.exists()) {
                    // Si existe el documento con el id especificado, verificamos contraseña
                    verifyPassword(documentSnapshot.get("password").toString())
                } else {
                    // Si no existe lo añadimos a la base de datos
                    saveuser()
                    // Almacenamos en memoria local
                    storeuser()
                    // Y salimos de esta ventana
                    findNavController().navigate(R.id.navigation)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error finding document", e)
            }
    }

    private fun verifyPassword(userPassword: String) {
        if (userPassword == password) {
            storeuser()
            findNavController().navigate(R.id.navigation)
        } else {
            Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_LONG).show()
            requireView().findViewById<EditText>(R.id.etpassword).text = null
        }
    }

    //Guarda el usuario en DataStore
    private fun storeuser() {
        lifecycleScope.launch(Dispatchers.IO) {
            pacienteViewModel.saveCredentials(
                username = username,
                apellido1 = apellido1,
                apellido2 = apellido2,
                telefono = telefono,
                password = password,
                context = requireContext()
            )

            //pacienteViewModel.insert(Paciente(telefono = telefono, nombre = username, apellido1 = apellido1, apellido2 = apellido2))
        }
    }

    private fun saveuser() {
        val paciente: MutableMap<String, Any> = HashMap() // diccionario key value
        paciente["nombre"] = username
        paciente["apellido1"] = apellido1
        paciente["apellido2"] = apellido2
        paciente["telefono"] = telefono
        paciente["password"] = password
        db.collection("pacientes").document(telefono)
            .set(paciente)
            .addOnSuccessListener {
                //si no hay error al añadir el nuevo paciente, añadimos su identificador a la coleccion
                //saveuserIdentifier()
                Log.d(TAG, "DocumentSnapshot added with ID: " + telefono)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Telefono en uso", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error adding document", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}