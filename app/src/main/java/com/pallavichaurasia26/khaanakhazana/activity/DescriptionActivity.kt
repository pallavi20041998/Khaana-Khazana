package com.pallavichaurasia26.khaanakhazana.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.DescriptionRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.database.OrderEntity
import com.pallavichaurasia26.khaanakhazana.database.RestaurantDatabase
import com.pallavichaurasia26.khaanakhazana.model.Dish
import com.pallavichaurasia26.khaanakhazana.util.ConnectionManager
import org.json.JSONException

class DescriptionActivity : AppCompatActivity() {

    lateinit var recyclerDescription: RecyclerView

    lateinit var btnProceed: Button

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    lateinit var layoutManager: RecyclerView.LayoutManager

    var dishInfoList = arrayListOf<Dish>()

    var orderList = arrayListOf<Dish>()

    lateinit var recyclerAdapter: DescriptionRecyclerAdapter

    lateinit var toolbar: Toolbar

    var restaurantId: String? = null

    var restaurantName: String? = null

    lateinit var coordinatorLayout: CoordinatorLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        layoutManager = LinearLayoutManager(this@DescriptionActivity)

        recyclerDescription = findViewById(R.id.recyclerDescription)

        btnProceed = findViewById(R.id.btnProceed)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)

        progressBar.visibility = View.VISIBLE

        btnProceed.visibility = View.GONE

        if (intent != null) {
            restaurantId = intent.getStringExtra("id")
            restaurantName = intent.getStringExtra("name")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (restaurantId == null || restaurantName == null) {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnProceed.setOnClickListener {

            proceedToCart()

        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonObjectRequest = object :
                JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                    //Here we will handle the response

                    try {

                        progressLayout.visibility = View.GONE

                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val dishArray = data.getJSONArray("data")
                            for (i in 0 until dishArray.length()) {
                                val dishJsonObject = dishArray.getJSONObject(i)
                                val dishObject = Dish(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one")
                                   // , dishJsonObject.getString("restaurant_id")
                                )
                                dishInfoList.add(dishObject)
                                recyclerAdapter =
                                    DescriptionRecyclerAdapter(
                                        this@DescriptionActivity,
                                        dishInfoList,
                                        object : DescriptionRecyclerAdapter.OnItemClickListener {

                                            override fun onAddItemClick(dishObject: Dish) {
                                                orderList.add(dishObject)
                                                if (orderList.size > 0) {
                                                    btnProceed.visibility = View.VISIBLE
                                                    DescriptionRecyclerAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(dishObject: Dish) {
                                                orderList.remove(dishObject)
                                                if (orderList.size == 0) {
                                                    btnProceed.visibility = View.GONE
                                                    DescriptionRecyclerAdapter.isCartEmpty = true
                                                }
                                            }
                                        }
                                    )

                                recyclerDescription.adapter = recyclerAdapter

                                recyclerDescription.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(
                                this,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some Unexpected Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {
                    //Here we will handle the error
                    Toast.makeText(
                        this,
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

    fun proceedToCart() {
        val gson = Gson()
        val foodItems = gson.toJson(orderList)
        val async =
            CartItems(this@DescriptionActivity, restaurantId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {

            val intent = Intent(this@DescriptionActivity, CartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            startActivity(intent)

        } else {
            Toast.makeText(this@DescriptionActivity, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }

    }

    class CartItems(
        context: Context, val restaurantId: String, val foodItems: String,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    override fun onBackPressed() {

        if(orderList.size > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}