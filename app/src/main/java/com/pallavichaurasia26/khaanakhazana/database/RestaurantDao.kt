package com.pallavichaurasia26.khaanakhazana.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant")
    fun getAllRestaurants():List<RestaurantEntity>

    @Query("SELECT * FROM restaurant WHERE restaurant_id= :restaurantId")
    fun getRestaurantById(restaurantId: String):RestaurantEntity
}