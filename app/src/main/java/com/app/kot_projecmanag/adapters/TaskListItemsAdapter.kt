package com.app.kot_projecmanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kot_projecmanag.R
import com.app.kot_projecmanag.activities.activities.TaskListActivity
import com.app.kot_projecmanag.models.Task
import java.util.*
import kotlin.collections.ArrayList

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view =
            LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams =
            LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp()).toPx(),0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]
        if (holder is MyViewHolder){
            if (position == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            } else {
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }

            holder.tvTaskListTitle.text = model.title
            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }

            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()

                if(listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context,"Please enter a list name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibEditListName.setOnClickListener {
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }

            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()

                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                } else {
                    Toast.makeText(context,"Please enter a list name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.tvAddCard.setOnClickListener {
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
            }

            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()

                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                } else {
                    Toast.makeText(context,"Please enter a card name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.rvCardList.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context, model.cards)
            holder.rvCardList.adapter = adapter

            adapter.setOnClickListener(
                object : CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {

                        if (context is TaskListActivity){
                            context.cardDetails(position, cardPosition)
                        }
                    }
                }
            )

            val dividerItemDecoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
            holder.rvCardList.addItemDecoration(dividerItemDecoration)

            //drag and drop functionality
            val helper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition

                        if (mPositionDraggedFrom == -1){
                            mPositionDraggedFrom = draggedPosition
                        }
                        mPositionDraggedTo = targetPosition
                        Collections.swap(list[position].cards, draggedPosition, targetPosition)
                        adapter.notifyItemMoved(draggedPosition, targetPosition)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 &&
                                mPositionDraggedFrom != mPositionDraggedTo){
                            (context as TaskListActivity).updateCardsInTaskList(position,
                                list[position].cards)
                        }

                        mPositionDraggedFrom = -1
                        mPositionDraggedTo = -1
                    }

                }
            )
            helper.attachToRecyclerView(holder.rvCardList)
        }
    }

    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert!")
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //funcion para obtener densidad de pantalla en dp from px
    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    //al reves px from dp
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        val tvTaskListTitle: TextView = view.findViewById(R.id.tv_task_list_title)
        val tvAddTaskList: TextView = view.findViewById(R.id.tv_add_task_list)
        val tvAddCard: TextView = view.findViewById(R.id.tv_add_card)
        val llTaskItem: LinearLayout = view.findViewById(R.id.ll_task_item)
        val llTitleView: LinearLayout = view.findViewById(R.id.ll_title_view)
        val cvAddTaskListName: CardView = view.findViewById(R.id.cv_add_task_list_name)
        val cvEditTaskListName: CardView = view.findViewById(R.id.cv_edit_task_list_name)
        val cvAddCard: CardView = view.findViewById(R.id.cv_add_card)
        val ibCloseListName: ImageButton = view.findViewById(R.id.ib_close_list_name)
        val ibDoneListName: ImageButton = view.findViewById(R.id.ib_done_list_name)
        val ibEditListName: ImageButton = view.findViewById(R.id.ib_edit_list_name)
        val ibDoneEditListName: ImageButton = view.findViewById(R.id.ib_done_edit_list_name)
        val ibCloseEditableView: ImageButton = view.findViewById(R.id.ib_close_editable_view)
        val ibDeleteList: ImageButton = view.findViewById(R.id.ib_delete_list)
        val ibCloseCardName: ImageButton = view.findViewById(R.id.ib_close_card_name)
        val ibDoneCardName: ImageButton = view.findViewById(R.id.ib_done_card_name)
        val etTaskListName: EditText = view.findViewById(R.id.et_task_list_name)
        val etEditTaskListName: EditText = view.findViewById(R.id.et_edit_task_list_name)
        val etCardName: EditText = view.findViewById(R.id.et_card_name)
        val rvCardList: RecyclerView = view.findViewById(R.id.rv_card_list)
    }
}