package com.pallavichaurasia26.khaanakhazana.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.CartRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.adapter.DescriptionRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.database.OrderEntity
import com.pallavichaurasia26.khaanakhazana.database.RestaurantDatabase
import com.pallavichaurasia26.khaanakhazana.model.Dish
import com.pallavichaurasia26.khaanakhazana.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btnOrder: Button

    lateinit var sharedPreferences: SharedPreferences

    var orderList = ArrayList<Dish>()

    var restaurantName: String? = null
    var restaurantId: String? = null

    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCart = findViewById(R.id.recyclerCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        btnOrder = findViewById(R.id.btnOrder)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        layoutManager = LinearLayoutManager(this@CartActivity)

        progressLayout.visibility = View.GONE

        if (intent != null) {

            restaurantId = intent.getStringExtra("restaurantId")
            restaurantName = intent.getStringExtra("restaurantName")

        } else {
            finish()
            Toast.makeText(this@CartActivity, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }

        if (restaurantId == null || restaurantName == null) {
            finish()
            Toast.makeText(
                this@CartActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        cartList()
        placeOrder()
    }

    class GetItemsDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }

    fun cartList() {

        val list = GetItemsDBAsync(applicationContext).execute().get()

        for (element in list) {
            orderList.addAll(Gson().fromJson(element.foodItems, Array<Dish>::class.java).asList())
        }

        recyclerAdapter = CartRecyclerAdapter(this@CartActivity, orderList)
        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

    }

    fun placeOrder() {

        var totalCost = 0
        for (i in 0 until orderList.size) {
            totalCost += orderList[i].dishCostForOne.toInt()
        }
        btnOrder.text = "Place Order(Total: Rs. $totalCost)"

        btnOrder.setOnClickListener {
            sendRequest(totalCost)

        }
    }

    fun sendRequest(totalCost: Int) {

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonObject = JSONObject()

        jsonObject.put("user_id", sharedPreferences.getString("user_id", "0"))
        jsonObject.put("restaurant_id", restaurantId)
        jsonObject.put("total_cost", totalCost.toString())

        val food = JSONArray()

        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].dishId)
            food.put(i, foodId)
        }
        jsonObject.put("food", food)

        if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonObject, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            ClearDBAsync(applicationContext, restaurantId.toString()).execute()
                                .get()
                            DescriptionRecyclerAdapter.isCartEmpty = true
                            Toast.makeText(this@CartActivity, "Order Placed", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, PlaceOrderActivity::class.java)
                            startActivity(intent)
                            finishAffinity()

                        } else {
                            Toast.makeText(
                                this@CartActivity,
                                "Some error Occurred",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Some Unexpected Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@CartActivity,
                        "Volley Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "8404b651f7c3ba"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)

            }
            dialog.create()
            dialog.show()
        }
    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
        alterDialog.setTitle("Alert!")
        alterDialog.setMessage("Going back will remove everything from cart")
        alterDialog.setPositiveButton("Okay") { text, listener ->
            ClearDBAsync(applicationContext, restaurantId.toString()).execute().get()
            DescriptionRecyclerAdapter.isCartEmpty = true
            super.onBackPressed()
        }
        alterDialog.setNegativeButton("No") { text, listener ->

        }
        alterDialog.show()
    }

    override fun onStop() {
        ClearDBAsync(applicationContext, restaurantId.toString()).execute().get()
        DescriptionRecyclerAdapter.isCartEmpty = true
        super.onStop()
    }


}