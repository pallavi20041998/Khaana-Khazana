package com.pallavichaurasia26.khaanakhazana.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.model.Dish

class DescriptionRecyclerAdapter(
    val context: Context,
    var itemList: ArrayList<Dish>,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_description_single_row, parent, false)

        return DescriptionViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(dishObject: Dish)
        fun onRemoveItemClick(dishObject: Dish)
    }


    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val dish = itemList[position]
        var s = position
        holder.txtDishId.text = "${s + 1}"
        holder.txtDishName.text = dish.dishName
        val costForOne = "Rs ${dish.dishCostForOne}"
        holder.txtDishCostForOne.text = costForOne

        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(dish)

        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(dish)
        }
    }

    class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishId: TextView = view.findViewById(R.id.txtDishId)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishCostForOne: TextView = view.findViewById(R.id.txtDishCostForOne)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}