package com.pallavichaurasia26.khaanakhazana.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.OrderHistoryRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.model.OrderDetails

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var orderHistoryRecyclerAdapter: OrderHistoryRecyclerAdapter
    var orderHistoryList = ArrayList<OrderDetails>()
    lateinit var rlNoOrder: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout
    var userId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)

        rlNoOrder = view.findViewById(R.id.rlNoOrder)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        progressLayout= view?.findViewById(R.id.progressLayout) as RelativeLayout
        progressLayout.visibility = View.VISIBLE
        sharedPreferences =
            (activity as Context).getSharedPreferences(getString(R.string.preference_file_name) ,Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null).toString()
        sendRequest(userId)

        return view
    }

    fun sendRequest(userId:String){
        val queue = Volley.newRequestQueue(activity as Context)
        val url=" http://13.235.250.119/v2/orders/fetch_result/$userId"

        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            rlNoOrder.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getString("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {
                                    rlNoOrder.visibility = View.VISIBLE
                                } else {
                                    rlNoOrder.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryRecyclerAdapter = OrderHistoryRecyclerAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        val layoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerOrderHistory.layoutManager = layoutManager
                                        recyclerOrderHistory.adapter = orderHistoryRecyclerAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "8404b651f7c3ba"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}