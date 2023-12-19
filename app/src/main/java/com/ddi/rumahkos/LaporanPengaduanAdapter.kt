package com.ddi.rumahkos

import LaporanPengaduanModel
import PengelolaModel
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LaporanPengaduanAdapter(private val mList: ArrayList<LaporanPengaduanModel>) :
    RecyclerView.Adapter<LaporanPengaduanAdapter.ListViewHolder>() {
    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    class ListViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var namadannokmar: TextView
        var btnhapus :ImageView
        private var v: View

        init {
            namadannokmar = itemView.findViewById(R.id.subjek)
            btnhapus = itemView.findViewById(R.id.btnhapus)

            v = itemView

            v.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
            }

            btnhapus.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemDelete(position)
                    }
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_listlaporanpengaduan, parent, false)
        return ListViewHolder(
            v,
            mListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val curItem = mList[position]
        val context = holder.itemView.context


        holder.namadannokmar.text = "${curItem.no}. ${curItem.nama}, Kamar No. ${curItem.nokamar}"

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}