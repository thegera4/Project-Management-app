package com.app.kot_projecmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kot_projecmanag.R
import com.app.kot_projecmanag.models.Board
import com.bumptech.glide.Glide

open class BoardsItemsAdapter(private val context: Context, private var list: ArrayList<Board>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .fitCenter()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.civBoardImage)

            holder.tvBoardName.text = model.name
            holder.tvCreatedBy.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val civBoardImage: ImageView = view.findViewById(R.id.civBoardImage)
        val tvBoardName: TextView = view.findViewById(R.id.tvBoardName)
        val tvCreatedBy: TextView = view.findViewById(R.id.tvCreatedBy)

    }
    }