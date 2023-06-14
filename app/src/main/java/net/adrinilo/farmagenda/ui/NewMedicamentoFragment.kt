package net.adrinilo.farmagenda.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.databinding.FragmentNewMedicineBinding
import net.adrinilo.farmagenda.views.MainActivity.Companion.TAG


class NewMedicamentoFragment : Fragment() {

    private var _binding: FragmentNewMedicineBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        binding.btnNewMed.setOnClickListener {
            addData()
            findNavController().navigate(R.id.action_newMedicamentoFragment_to_diaFragment)
        }
    }

/*    private fun savemed() {
        val etnombre_med = requireView().findViewById<EditText>(R.id.etnombre_med)
        val etdosis_med = requireView().findViewById<EditText>(R.id.etdosis_med)
        val etlaboratorio = requireView().findViewById<EditText>(R.id.etlaboratorio)
        val etadmin_med = requireView().findViewById<EditText>(R.id.etadmin_med)
        val etdetalle_med = requireView().findViewById<EditText>(R.id.etdetalle_med)

        val nombre = etnombre_med.text.toString()
        val dosis = etdosis_med.text.toString()
        val laboratorio = etlaboratorio.text.toString()
        val administracion = etadmin_med.text.toString()
        val detalle = etdetalle_med.text.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            medicamentoViewModel.saveData(
                nombre = nombre,
                dosis = dosis,
                laboratorio = laboratorio,
                administracion = administracion,
                detalle = detalle,
                context = requireContext()
            )
        }
    }*/

    private fun addData() {
        val etnombre_med = requireView().findViewById<EditText>(R.id.etnombre_med)
        val etdosis_med = requireView().findViewById<EditText>(R.id.etdosis_med)
        val etlaboratorio = requireView().findViewById<EditText>(R.id.etlaboratorio)
        val etadmin_med = requireView().findViewById<EditText>(R.id.etadmin_med)
        val etformato_med = requireView().findViewById<EditText>(R.id.etformato_med)
        val descripción = "${etnombre_med.text} ${etdosis_med.text} ${etformato_med.text}"

        val documentId = db.collection("medicamentos").document().id

        val medicamento: MutableMap<String, Any> = HashMap() // diccionario key value
        medicamento["nregistro"] = documentId
        medicamento["descripcion"] = descripción
        medicamento["nombre"] = etnombre_med.text.toString()
        medicamento["dosis"] = etdosis_med.text.toString()
        medicamento["labtitular"] = etlaboratorio.text.toString()
        medicamento["ffs"] = etformato_med.text.toString()
        medicamento["administracion"] = etadmin_med.text.toString()
        medicamento["fotos"] = "generic_img"
        medicamento["docs"] = ""
        db.collection("medicamentos").document(documentId)
            .set(medicamento)
            .addOnSuccessListener{
                Log.d(TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener{ e ->
                Log.w(TAG,"Error adding document", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}