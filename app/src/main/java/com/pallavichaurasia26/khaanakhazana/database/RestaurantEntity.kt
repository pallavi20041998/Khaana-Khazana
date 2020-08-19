package com.pallavichaurasia26.khaanakhazana.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="restaurant")
data class RestaurantEntity (
    @PrimaryKey val restaurant_id:String,
    @ColumnInfo(name="restaurant_name") val restaurantName:String,
    @ColumnInfo(name="restaurant_rating") val restaurantRating:String,
    @ColumnInfo(name="restaurant_cost_for_one") val restaurantCostForOne:String,
    @ColumnInfo(name="restaurant_image_url") val restaurantImageUrl:String
)