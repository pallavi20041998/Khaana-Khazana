package com.pallavichaurasia26.khaanakhazana.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pallavichaurasia26.khaanakhazana.activity.DescriptionActivity
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.database.RestaurantDatabase
import com.pallavichaurasia26.khaanakhazana.database.RestaurantEntity
import com.pallavichaurasia26.khaanakhazana.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, var itemList:ArrayList<Restaurant>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant= itemList[position]
        holder.txtRestaurantName.text=restaurant.restaurantName
        holder.txtRestaurantRating.text=restaurant.restaurantRating
        val costForOne="${restaurant.restaurantCostForOne}/person"
        holder.txtRestaurantCostForOne.text=costForOne
        Picasso.get().load(restaurant.restaurantImageUrl).error(R.drawable.default_restaurant_cover).into(holder.imgRestaurantImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantCostForOne,
            restaurant.restaurantImageUrl
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.favImage.setImageResource(R.drawable.ic_action_fav_checked)
        } else {
            holder.favImage.setImageResource(R.drawable.ic_action_fav)
        }

        holder.favImage.setOnClickListener {


            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_action_fav_checked)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_action_fav)
                }
            }
        }

        holder.llContent.setOnClickListener{
            val intent= Intent(context, DescriptionActivity::class.java)
            intent.putExtra("id",restaurant.restaurantId)
            intent.putExtra("name",restaurant.restaurantName)
            context.startActivity(intent)
        }

    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView=view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantCostForOne: TextView=view.findViewById(R.id.txtRestaurantCostForOne)
        val imgRestaurantImage: ImageView =view.findViewById(R.id.imgRestaurantImage)
        val favImage:ImageView = view.findViewById(R.id.imgIsFav)
        val llContent: LinearLayout =view.findViewById(R.id.llContent)



    }

    fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            /*
            Mode 1 -> Check DB if the book is favourite or not
            Mode 2 -> Save the book into DB as favourite
            Mode 3 -> Remove the favourite book
            */

            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id)
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }


}
