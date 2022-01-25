package com.app.kot_projecmanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.kot_projecmanag.R
import java.util.*

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_label_color,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is MyViewHolder) {

            holder.viewMain.setBackgroundColor(Color.parseColor(item))

            if (item == mSelectedColor) {
                holder.ivSelectedColor.visibility = View.VISIBLE
            } else {
                holder.ivSelectedColor.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val viewMain: View = view.findViewById(R.id.view_main)
        val ivSelectedColor: ImageView = view.findViewById(R.id.iv_selected_color)
    }

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }
}
