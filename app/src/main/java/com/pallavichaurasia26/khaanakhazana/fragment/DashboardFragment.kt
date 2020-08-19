package com.pallavichaurasia26.khaanakhazana.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.DashboardRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.model.Restaurant
import com.pallavichaurasia26.khaanakhazana.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerDashboard: RecyclerView

    lateinit var editTextSearch: EditText

    lateinit var cant_find:RelativeLayout

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    val restaurantInfoList = arrayListOf<Restaurant>()

    var ratingComparator = Comparator<Restaurant> { res1, res2 ->

        if (res1.restaurantRating.compareTo(res2.restaurantRating, true) == 0) {
            //sort acc to name if rating is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            res1.restaurantRating.compareTo(res2.restaurantRating, true)
        }

    }

    var costComparator = Comparator<Restaurant> { res1, res2 ->

        if (res1.restaurantCostForOne.compareTo(res2.restaurantCostForOne, true) == 0) {
            //sort acc to name if rating is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            res1.restaurantCostForOne.compareTo(res2.restaurantCostForOne, true)
        }

    }

    //var previousMenuItem: MenuItem? = null

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
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        editTextSearch=view.findViewById(R.id.editTextSearch)

        cant_find=view.findViewById(R.id.cant_find)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )


        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressBar.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        recyclerAdapter = DashboardRecyclerAdapter(activity as Context, restaurantInfoList)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object :
                JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
                    //Here we will handle the response

                    try {

                        progressLayout.visibility = View.GONE

                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val restaurantJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )

                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {
                    //Here we will handle the error

                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
            if (id == R.id.action_sort_cost_low_to_high) {
                item.isChecked = !item.isChecked
                Collections.sort(restaurantInfoList, costComparator)


            } else if (id == R.id.action_sort_cost_high_to_low) {
                item.isChecked = !item.isChecked
                Collections.sort(restaurantInfoList, costComparator)
                restaurantInfoList.reverse()
            } else if (id == R.id.action_rating) {
                item.isChecked = !item.isChecked
                Collections.sort(restaurantInfoList, ratingComparator)
                restaurantInfoList.reverse()
            }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    fun filterFun(strTyped:String){
        val filteredList= arrayListOf<Restaurant>()

        for (item in restaurantInfoList){
            if(item.restaurantName.toLowerCase().contains(strTyped.toLowerCase())){

                filteredList.add(item)

            }
        }

        if(filteredList.size==0){
           cant_find.visibility=View.VISIBLE
        }
        else{
            cant_find.visibility=View.INVISIBLE
        }

        recyclerAdapter.filterList(filteredList)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}