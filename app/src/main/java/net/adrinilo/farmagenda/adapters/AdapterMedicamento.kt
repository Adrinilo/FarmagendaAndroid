package net.adrinilo.farmagenda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.model.Medicamento

class AdapterMedicamento(
    val context: Context,
    val layout: Int,
    val listener: OnClickListenerRow
) : RecyclerView.Adapter<AdapterMedicamento.ViewHolder>() {

    private var dataList: List<Medicamento> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setMedicamentos(medicamentos: List<Medicamento>) {
        this.dataList = medicamentos
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Medicamento, listener: OnClickListenerRow) {

            val tvnombre_rowdetalle = itemView.findViewById<TextView>(R.id.tvnombre_rowdetalle)
            val tvadmin_rowdetalle = itemView.findViewById<TextView>(R.id.tvadmin_rowdetalle)
            val tvforma_rowdetalle = itemView.findViewById<TextView>(R.id.tvforma_rowdetalle)
            val tvdescripcion_rowdetalle =
                itemView.findViewById<TextView>(R.id.tvdescripcion_rowdetalle)
            val tvlaboratorio_rowdetalle =
                itemView.findViewById<TextView>(R.id.tvlaboratorio_rowdetalle)
            val tvdosis_rowdetalle = itemView.findViewById<TextView>(R.id.tvdosis_rowdetalle)
            val ivrowdetalle = itemView.findViewById<ImageView>(R.id.ivrowdetalle)

            tvnombre_rowdetalle.text = dataItem.nombre.nombre.uppercase()
            tvadmin_rowdetalle.text = dataItem.administracion[0].texto
            tvforma_rowdetalle.text = dataItem.ffs.texto
            tvdescripcion_rowdetalle.text = "DESCRIPCIÓN: ${dataItem.descripcion}"
            tvlaboratorio_rowdetalle.text = dataItem.labtitular
            if (dataItem.dosis == "1000 mg" || dataItem.dosis == "1.000 mg") {
                tvdosis_rowdetalle.text = "1 G"
            } else {
                tvdosis_rowdetalle.text = dataItem.dosis
            }

            if (dataItem.fotos[0].url == "generic_img") {
                Picasso.get()
                    .load(R.drawable.img_med_genericos)
                    .resize(0,1000)
                    .centerCrop()
                    .into(ivrowdetalle)
            } else {
                Picasso.get()
                    .load(dataItem.fotos[0].url)
                    .resize(0,1000)
                    .centerCrop()
                    .into(ivrowdetalle)
            }

            itemView.tag = dataItem

            (itemView as CardView).getChildAt(0).tag = false
            //itemView.setOnClickListener { listener.onClickMed(itemView) }
            itemView.findViewById<Button>(R.id.btnselectmed_toma).setOnClickListener { listener.onClickMed(dataItem) }
        }
    }

    interface OnClickListenerRow {
        fun onClickMed(med: Medicamento)

    }
}
