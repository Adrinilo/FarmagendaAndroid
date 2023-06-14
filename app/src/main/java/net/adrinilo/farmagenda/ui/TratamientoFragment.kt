package net.adrinilo.farmagenda.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.databinding.FragmentTratamientoBinding
import net.adrinilo.farmagenda.utils.setTime
import net.adrinilo.farmagenda.ui.dialog.DatePickerFragment
import net.adrinilo.farmagenda.ui.dialog.TimePickerFragment
import net.adrinilo.farmagenda.viewmodels.PacienteViewModel
import net.adrinilo.farmagenda.views.MainActivity.Companion.TAG
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TratamientoFragment : Fragment() {

    private var _binding: FragmentTratamientoBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var etdate: EditText
    private lateinit var pacienteid: String
    private lateinit var pacienteViewModel: PacienteViewModel
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private lateinit var tvtime: TextView
    private lateinit var ettomadiaria: EditText
    private lateinit var etselectedmed: TextView
    private val args: TratamientoFragmentArgs by navArgs()
    private var selectedmedid: String? = null
    private var selectedmedname: String? = null
    private lateinit var etrepeticion: EditText
    private lateinit var btnprospecto: Button
    private var newtratamiento: String? = null
    private var selectedhora: Int = 0
    private var selectedmin: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTratamientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        pacienteViewModel = ViewModelProvider(this)[PacienteViewModel::class.java]
        ettomadiaria = requireView().findViewById<EditText>(R.id.ettomadiaria)

        binding.apply {
            if (args.selectedmedid != "defaultvalue") {
                selectedmedid = args.selectedmedid
                selectedmedname = args.selectedmedname
                etselectedmed.setText(selectedmedname?.replaceFirstChar { selectedmedname!![0].uppercase() })
                btnprospecto.visibility = View.VISIBLE
                if (args.selectedmedprospecto != "") {
                    btnprospecto.setOnClickListener {
                        findNavController().navigate(
                            TratamientoFragmentDirections.actionTratamientoFragmentToWebViewFragment(
                                args.selectedmedprospecto
                            )
                        )
                    }
                } else {
                    dialogprospecto()
                }
            }
        }

        lifecycleScope.launch {
            pacienteid = getPacienteId()
        }

        setListeners()
        setUpTime()
        settextListener()

        binding.btnInsertTratamiento.setOnClickListener {
            checkdata()
        }
    }

    private fun dialogprospecto() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error al buscar prospecto")
            .setMessage("Prospecto no disponible")
            .setPositiveButton(R.string.entendido) { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun checkdata() {
        if (etselectedmed.text.isBlank()) {
            showCheckDialog("Medicamento no seleccionado")
        } else if (ettomadiaria.text.isBlank() || ettomadiaria.text.toString() == "0") {
            showCheckDialog("Numero de tomas no seleccionadas")
        } else if (etdate.text.isBlank()) {
            showCheckDialog("Fecha no seleccionada")
        } else if (etrepeticion.text.isBlank()) {
            etrepeticion.setText("No se repite")
        } else {
            showConfirmDialog()
        }
    }

    private fun showCheckDialog(text: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error al introducir datos")
            .setMessage(text)
            .setPositiveButton(R.string.entendido) { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar datos")
            .setMessage("${etselectedmed.text} ${ettomadiaria.text} veces al día, a las ${tvtime.text}")
            .setPositiveButton(R.string.entendido) { dialog, which ->
                savetratamiento()
                findNavController().navigate(R.id.action_tratamientoFragment_to_inicioFragment)
            }.show()
    }

    private fun settextListener() {
        ettomadiaria.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val time_sl = requireView().findViewById<TextView>(R.id.time_selection_label)
                val repetext = ettomadiaria.text.toString()

                if ((repetext != "")) {
                    val repeticion = repetext.toInt()

                    when {
                        repeticion == 1 -> time_sl.text = "Hora de la toma"
                        repeticion > 1 -> time_sl.text = "Hora de la primera toma"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setUpTime() {
        tvtime = requireView().findViewById(R.id.tvtime)
        tvtime.text = LocalTime.now().format(formatter)
        tvtime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        showDialog { _, hour, minute ->
            selectedhora = hour
            selectedmin = minute
            val currentTime = convertTime(hour, minute)
            tvtime.setTime(currentTime)
        }
    }

    private fun convertTime(hour: Int, minute: Int): LocalTime {
        return LocalTime.of(hour, minute)
    }

    private fun showDialog(observer: TimePickerDialog.OnTimeSetListener) {
        TimePickerFragment.newInstance(observer)
            .show(requireActivity().supportFragmentManager, "time-picker")
    }

    private suspend fun getPacienteId(): String = suspendCoroutine { continuation ->
        lifecycleScope.launch(Dispatchers.IO) {
            val pacienteid = pacienteViewModel.loadCredentials(requireContext()).telefono
            withContext(Dispatchers.Main) {
                continuation.resume(pacienteid)
            }
        }
    }

    private fun setListeners() {
        etdate = requireView().findViewById<EditText>(R.id.etdate)
        etdate.setOnClickListener { showDatePickerDialog(etdate) }
        etselectedmed = requireView().findViewById(R.id.etselectedmed)
        etselectedmed.setOnClickListener { findNavController().navigate(R.id.action_tratamientoFragment_to_apiMedFragment) }
        etrepeticion = requireView().findViewById(R.id.etrepeticion)
        etrepeticion.setOnClickListener { showRadioConfirmationDialog() }
        btnprospecto = requireView().findViewById<Button>(R.id.btnprospecto)
    }

    private fun showDatePickerDialog(editText: EditText) {
        val newFragment =
            DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dayStr = day.twoDigits()
                val monthStr = (month + 1).twoDigits() // +1 porque Enero es el 0

                val selectedDate = "$dayStr/$monthStr/$year"
                editText.setText(selectedDate)
            })

        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    fun Int.twoDigits() = if (this <= 9) "0$this" else this.toString()

    // Mostar Dialogo con Multiples opciones y una sola seleccion simultania
    private fun showRadioConfirmationDialog() {
        var selectedOptionIndex: Int = 0
        val options = arrayOf(
            "No se repite",
            "Todos los días",
            "Todas las semanas",
            "Todos los meses",
            "Todos los años",
            "Personalizar..."
        )
        var selectedOption = options[selectedOptionIndex]
        MaterialAlertDialogBuilder(requireContext())
            .setSingleChoiceItems(options, selectedOptionIndex) { dialog_, which ->
                selectedOptionIndex = which
                selectedOption = options[which]
            }.setPositiveButton(R.string.confirm) { dialog, which ->
                etrepeticion.setText(selectedOption)
            }.setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    // Guardar los datos del tratamiento en Firebase
    private fun savetratamiento() {
        val tratamiento: MutableMap<String, Any> = HashMap() // diccionario key value
        tratamiento["idpaciente"] = pacienteid
        tratamiento["idmed"] = selectedmedid.toString()
        tratamiento["finishdate"] = "01/01/9999"
        db.collection("tratamientos")
            .add(tratamiento)
            .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                newtratamiento = documentReference.id
                savecalendario()
                Toast.makeText(requireContext(), "Tratamiento añadido con exito", Toast.LENGTH_LONG)
                    .show()
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
            }).addOnFailureListener(OnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            })
    }

    private fun savecalendario() {
        val calendario: MutableMap<String, Any> = HashMap() // diccionario key value
        calendario["idtratamiento"] = newtratamiento.toString()
        calendario["hora"] = tvtime.text.toString()
        calendario["date"] = etdate.text.toString()
        db.collection("calendario")
            .add(calendario)
            .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                //configAlarm()
                Toast.makeText(
                    requireContext(),
                    "Tratamiento añadido al calendario con exito",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
            }).addOnFailureListener(OnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            })
    }
}
