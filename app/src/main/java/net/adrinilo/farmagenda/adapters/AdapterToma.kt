package net.adrinilo.farmagenda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.adrinilo.farmagenda.R
import net.adrinilo.farmagenda.model.Toma

class AdapterToma(
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<AdapterToma.ViewHolder>() {

    private var dataList: List<Toma> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setTomas(tomas: List<Toma>) {
        this.dataList = tomas
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Toma) {

            val tvnombre_rowtoma = itemView.findViewById<TextView>(R.id.tvnombre_rowtoma)
            val tvadmin_rowtoma = itemView.findViewById<TextView>(R.id.tvadmin_rowtoma)
            val tvforma_rowtoma = itemView.findViewById<TextView>(R.id.tvforma_rowtoma)
            val tvdescripcion_rowtoma =
                itemView.findViewById<TextView>(R.id.tvdescripcion_rowtoma)
            val tvlaboratorio_rowtoma =
                itemView.findViewById<TextView>(R.id.tvlaboratorio_rowtoma)
            val tvdosis_rowtoma = itemView.findViewById<TextView>(R.id.tvdosis_rowtoma)
            val tvhora_rowtoma = itemView.findViewById<TextView>(R.id.tvhora_rowtoma)
            val tvfecha_rowtoma = itemView.findViewById<TextView>(R.id.tvfecha_rowtoma)
            val iv_rowtoma = itemView.findViewById<ImageView>(R.id.iv_rowtoma)

            tvnombre_rowtoma.text = dataItem.nombre.nombre.uppercase()
            tvadmin_rowtoma.text = dataItem.administracion[0].texto
            tvforma_rowtoma.text = dataItem.ffs.texto
            tvdescripcion_rowtoma.text = "DESCRIPCIÓN: ${dataItem.descripcion}"
            tvlaboratorio_rowtoma.text = dataItem.labtitular
            tvhora_rowtoma.text = dataItem.hora
            tvfecha_rowtoma.text = dataItem.fecha

            if (dataItem.dosis == "1000 mg" || dataItem.dosis == "1.000 mg") {
                tvdosis_rowtoma.text = "1 G"
            } else {
                tvdosis_rowtoma.text = dataItem.dosis
            }

            if (dataItem.fotos[0].url == "generic_img") {
                Picasso.get()
                    .load(R.drawable.img_med_genericos)
                    .resize(0,1000)
                    .centerCrop()
                    .into(iv_rowtoma)
            } else {
                Picasso.get()
                    .load(dataItem.fotos[0].url)
                    .resize(0,1000)
                    .centerCrop()
                    .into(iv_rowtoma)
            }

            itemView.tag = dataItem

        }
    }
}
