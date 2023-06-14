package net.adrinilo.farmagenda.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.adapters.AdapterToma
import net.adrinilo.farmagenda.databinding.FragmentDiaBinding
import net.adrinilo.farmagenda.model.Calendario
import net.adrinilo.farmagenda.model.Doc
import net.adrinilo.farmagenda.model.Foto
import net.adrinilo.farmagenda.model.Medicamento
import net.adrinilo.farmagenda.model.Toma
import net.adrinilo.farmagenda.model.Ffs
import net.adrinilo.farmagenda.model.Vadmin
import net.adrinilo.farmagenda.model.Vtm
import net.adrinilo.farmagenda.viewmodels.PacienteViewModel
import net.adrinilo.farmagenda.views.MainActivity.Companion.TAG
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiaFragment : Fragment() {

    private var _binding: FragmentDiaBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterToma
    private lateinit var db: FirebaseFirestore
    private val pacienteViewModel: PacienteViewModel by viewModels()
    private var ListTomas: ArrayList<Toma> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        lifecycleScope.launch {
            val pacienteid = getPacienteId()
            val tratamientos = getTratamientos(pacienteid).await()
            documentToListTra(tratamientos)
        }
        /*        binding.fab.setOnClickListener {
                    findNavController().navigate(R.id.action_diaFragment_to_newMedicamentoFragment)
                }*/
    }

    // Obtenemos los ids de los tratamientos del paciente
    private fun getTratamientos(pacienteid: String): Deferred<List<DocumentSnapshot>> {
        val deferred = CompletableDeferred<List<DocumentSnapshot>>()
        db.collection("tratamientos")
            .whereEqualTo("idpaciente", pacienteid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val documents = snapshot.documents
                    deferred.complete(documents)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
        return deferred
    }

    // Para cada tratamiento guardamos su id y el id de medicamento
    private fun documentToListTra(documents: List<DocumentSnapshot>) {
        val tratamientos: ArrayList<String> = ArrayList()
        documents.forEach { d ->
            val idtra = d.id
            tratamientos.add(
                idtra
            )
        }
        getTomas(tratamientos)
    }

    // Por cada tratamiento obtenemos el medicamento, hora y fecha correspondientes
    private fun getTomas(tratamientos: ArrayList<String>) {
        val deferreds = tratamientos.map { t ->
            lifecycleScope.async {
                val med = getCurrentMed(t)
                val cal = getCurrentCal(t)
                addToma(med, cal)
            }
        }

        lifecycleScope.async {
            deferreds.awaitAll()
            orderData()
        }
    }

    // Una vez obtenida la información la ordenamos antes de inicializar el recyclerview
    private fun orderData() {
        val sortedList = ListTomas.sortedWith(compareBy({ it.fecha }, { it.hora }))
        ListTomas.clear()
        ListTomas.addAll(sortedList)
        setData()
    }

    // Iniciamos el recyclerview e indicamos al adapter la lista de objetos que queremos que use
    private fun setData() {
        initRV()
        adapter.setTomas(ListTomas)
    }

    private fun initRV() {
        adapter = AdapterToma(requireContext(), R.layout.rowtomas)
        binding.rvtomas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvtomas.adapter = adapter
    }

    // Metodo que se encarga de obtener el medicamento correspondiente en base al tratamiento
    // Teniendo en cuenta que cada metodo haya terminado de ejecutarse antes de continuar la ejecución
    private suspend fun getCurrentMed(t: String): Medicamento {
        val id = getIDMedicamento(t)
        val doc = getMedicamentoByID(id)
        val med = documentToMed(doc)
        return med
    }

    // Con el id de tratamiento obtenemos el id de medicamento
    private suspend fun getIDMedicamento(t: String): String {
        return suspendCoroutine { continuation ->
            db.collection("tratamientos")
                .document(t)
                .get()
                .addOnSuccessListener { tratamiento ->
                    val idmed = tratamiento["idmed"] as String
                    continuation.resume(idmed)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

    // Con el id de medicamento obtenemos el documento de firebase vinculado
    private suspend fun getMedicamentoByID(id: String): DocumentSnapshot {
        return suspendCoroutine { continuation ->
            db.collection("medicamentos")
                .document(id)
                .get()
                .addOnSuccessListener { d ->
                    continuation.resume(d)
                    Log.d(TAG, "${d.id} => ${d.data}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

    // Con el id de documento obtenemos el objeto medicamento
    private suspend fun documentToMed(d: DocumentSnapshot): Medicamento {
        return suspendCoroutine { continuation ->
            val nregistro = d["nregistro"] as String
            val descripcion = d["descripcion"] as String
            val nombre = d["nombre"] as String
            val dosis = d["dosis"] as String
            val labtitular = d["labtitular"] as String
            val ffs = d["ffs"] as String
            val administracion = d["administracion"] as String
            val fotos = d["fotos"] as String
            val docs = d["docs"] as String
            val med =
                Medicamento(
                    nregistro = nregistro,
                    descripcion = descripcion,
                    nombre = Vtm(nombre),
                    dosis = dosis,
                    labtitular = labtitular,
                    ffs = Ffs(ffs),
                    administracion = listOf(Vadmin(administracion)),
                    fotos = listOf(Foto(fotos)),
                    docs = listOf(Doc(docs))
                )
            continuation.resume(med)
        }
    }

    // Metodo que se encarga de obtener el calendario correspondiente en base al tratamiento
    // Teniendo en cuenta que cada metodo haya terminado de ejecutarse antes de continuar la ejecución
    private suspend fun getCurrentCal(t:String): Calendario {
        val doc = getDocCal(t)
        val cal = documentToCal(doc, t)
        return cal
    }

    // Con el id de tratamiento obtenemos el id de documento de firebase
    private suspend fun getDocCal(t:String): DocumentSnapshot {
        return suspendCoroutine { continuation ->
            db.collection("calendario")
                .whereEqualTo("idtratamiento", t)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        continuation.resumeWithException(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val document = snapshot.documents[0]
                        continuation.resume(document)
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }
    }

    // Con el id de documento y el id de tratamiento obtenemos el objeto calendario
    private suspend fun documentToCal(d: DocumentSnapshot, t: String): Calendario {
        return suspendCoroutine { continuation ->
            val hora = d["hora"] as String
            val date = d["date"] as String
            val cal =
                Calendario(
                    date = date,
                    hora = hora,
                    idtratamiento = t
                )
            continuation.resume(cal)
        }
    }

    // Con el medicamento y calendario vinculados al tratamiento añadimos un nuevo objeto "Toma" a la lista de tomas
    private fun addToma(med: Medicamento, cal: Calendario) {
        ListTomas.add(
            Toma(
                nregistro = med.nregistro,
                descripcion = med.descripcion,
                nombre = med.nombre,
                dosis = med.dosis,
                labtitular = med.labtitular,
                ffs = med.ffs,
                administracion = med.administracion,
                fotos = med.fotos,
                docs = med.docs,
                hora = cal.hora,
                fecha = cal.date
            )
        )
    }

    // Obtenemos el id de paciente
    private suspend fun getPacienteId(): String = suspendCoroutine { continuation ->
        lifecycleScope.launch(Dispatchers.IO) {
            val pacienteid = pacienteViewModel.loadCredentials(requireContext()).telefono
            withContext(Dispatchers.Main) {
                continuation.resume(pacienteid)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}