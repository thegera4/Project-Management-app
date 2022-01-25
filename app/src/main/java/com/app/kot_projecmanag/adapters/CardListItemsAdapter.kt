package com.app.kot_projecmanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kot_projecmanag.R
import com.app.kot_projecmanag.activities.activities.TaskListActivity
import com.app.kot_projecmanag.models.Card
import com.app.kot_projecmanag.models.SelectedMembers

open class CardListItemsAdapter (private val context: Context,
                                 private var list: ArrayList<Card>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){

            if (model.labelColor.isNotEmpty()){
                holder.viewLabelColor.visibility = View.VISIBLE
                holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.viewLabelColor.visibility = View.GONE
            }

            holder.tvCardName.text = model.name

            if ((context as TaskListActivity)
                    .mAssignedMemberDetailList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for (i in context.mAssignedMemberDetailList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMemberDetailList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                if (selectedMembersList.size > 0){
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.rvCardSelectedMembersList.visibility = View.GONE
                    } else {
                        holder.rvCardSelectedMembersList.visibility = View.VISIBLE

                        holder.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context,4)
                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                        holder.rvCardSelectedMembersList.adapter = adapter
                        adapter.setOnClickListener(
                            object : CardMemberListItemsAdapter.OnClickListener {
                                override fun onClick() {
                                    if (onClickListener != null){
                                        onClickListener!!.onClick(position)
                                    }
                                }
                            })
                    }
                } else {
                    holder.rvCardSelectedMembersList.visibility = View.GONE
                }

            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCardName: TextView = view.findViewById(R.id.tv_card_name)
        val viewLabelColor: View = view.findViewById(R.id.view_label_color)
        val rvCardSelectedMembersList: RecyclerView = view.findViewById(R.id.rv_card_selected_members_list)
    }

}
