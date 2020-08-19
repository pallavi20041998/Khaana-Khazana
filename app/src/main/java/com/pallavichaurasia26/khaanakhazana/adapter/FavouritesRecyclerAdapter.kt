package com.pallavichaurasia26.khaanakhazana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.activity.DescriptionActivity
import com.pallavichaurasia26.khaanakhazana.model.Restaurant
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context, val itemList:ArrayList<Restaurant>): RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent, false)

        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: FavouritesRecyclerAdapter.FavouritesViewHolder,
        position: Int
    ) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        val costForOne = "${restaurant.restaurantCostForOne}/person"
        holder.txtRestaurantCostForOne.text = costForOne
        Picasso.get().load(restaurant.restaurantImageUrl).error(R.drawable.default_restaurant_cover)
            .into(holder.imgRestaurantImage)

        holder.llContent.setOnClickListener{
            val intent= Intent(context, DescriptionActivity::class.java)
            intent.putExtra("id",restaurant.restaurantId)
            intent.putExtra("name",restaurant.restaurantName)
            context.startActivity(intent)
        }
    }
    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantCostForOne: TextView = view.findViewById(R.id.txtRestaurantCostForOne)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val llContent: LinearLayout =view.findViewById(R.id.llContent)
    }
}