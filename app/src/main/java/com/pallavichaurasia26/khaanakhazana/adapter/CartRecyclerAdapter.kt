package com.pallavichaurasia26.khaanakhazana.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.model.Dish
import com.squareup.picasso.Picasso

class CartRecyclerAdapter(val context: Context, val itemList: ArrayList<Dish>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val dish = itemList[position]
        holder.txtDishName.text = dish.dishName
        val costForOne = "Rs. ${dish.dishCostForOne}"
        holder.txtDishCostForOne.text = costForOne
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishCostForOne: TextView = view.findViewById(R.id.txtDishCostForOne)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}