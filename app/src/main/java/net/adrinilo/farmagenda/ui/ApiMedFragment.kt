package net.adrinilo.farmagenda.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.adapters.AdapterMedicamento
import net.adrinilo.farmagenda.databinding.FragmentApiMedBinding
import net.adrinilo.farmagenda.model.Medicamento
import net.adrinilo.farmagenda.viewmodels.MedicamentoViewModel
import net.adrinilo.farmagenda.views.MainActivity.Companion.TAG

class ApiMedFragment : Fragment(), AdapterMedicamento.OnClickListenerRow {

    private var _binding: FragmentApiMedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterMedicamento
    private lateinit var medicamentoViewModel: MedicamentoViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var medicamentos: List<Medicamento>
    private lateinit var etsearch_med_name: EditText
    lateinit var selectedMed: Medicamento
    lateinit var bundle: Bundle


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApiMedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etsearch_med_name = requireView().findViewById<EditText>(R.id.etsearch_med_name)
        initRV()
        medicamentoViewModel = ViewModelProvider(this).get(MedicamentoViewModel::class.java)
        db = FirebaseFirestore.getInstance()

        binding.btnBuscarMed.setOnClickListener {
            loadData()
        }
    }

    private fun initRV() {
        adapter = AdapterMedicamento(requireContext(), R.layout.rowmedicamento, this)
        binding.rvmedicamentos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvmedicamentos.adapter = adapter
    }

    private fun loadData() {
        val mednombre = etsearch_med_name.text.toString()

        medicamentoViewModel.getMedicamentosByNombre(mednombre)
            .observe(viewLifecycleOwner, Observer { it ->
                it?.let {
                    medicamentos = it
                    adapter.setMedicamentos(medicamentos)
                }
            })
    }

    override fun onClickMed(med: Medicamento) {
        //Toast.makeText(requireContext(), "Medicamento pulsado ${med.nregistro}", Toast.LENGTH_SHORT).show()
        selectedMed = med
        dialogSelectMed()
    }

    private fun dialogSelectMed() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación de Medicamento")
        val ll = LinearLayout(requireContext())
        ll.setPadding(30, 30, 30, 30)
        ll.orientation = LinearLayout.VERTICAL

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(0, 50, 0, 50)

        val tildialog = TextInputLayout(requireContext())
        tildialog.layoutParams = lp
        val tvdialogmed = TextView(requireContext())
        tvdialogmed.setPadding(0, 80, 0, 80)
        tvdialogmed.textSize = 20.0F
        tvdialogmed.text = "${getString(R.string.dialogtext)} ${selectedMed.descripcion} ?"
        tildialog.addView(tvdialogmed)
        ll.addView(tildialog)
        builder.setView(ll)

        builder.setPositiveButton("Continuar") { _, _ ->
            //Continuamos con el proceso navegando a la siguiente pantalla
            sendData()
        }
        builder.setNegativeButton("Cancelar") { _, _ ->
        }

        builder.show()
    }

    private fun sendData() {
        val direction = ApiMedFragmentDirections.actionApiMedFragmentToTratamientoFragment(
            selectedMed.nregistro ?: "",
            selectedMed.nombre.nombre ?: "",
            selectedMed.docs[1].prospecto ?: ""
        )
        findNavController().navigate(direction)
    }

}