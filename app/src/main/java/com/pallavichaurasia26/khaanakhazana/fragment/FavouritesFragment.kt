package com.pallavichaurasia26.khaanakhazana.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.DashboardRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.adapter.FavouritesRecyclerAdapter
import com.pallavichaurasia26.khaanakhazana.database.RestaurantDatabase
import com.pallavichaurasia26.khaanakhazana.database.RestaurantEntity
import com.pallavichaurasia26.khaanakhazana.model.Restaurant

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite:RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouritesRecyclerAdapter
    lateinit var rlNoFav: RelativeLayout
    var dbRestaurantList= arrayListOf<Restaurant>()


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view=inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourite=view.findViewById(R.id.recyclerFavourite)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        rlNoFav=view.findViewById(R.id.rlNoFavorites)

        layoutManager= LinearLayoutManager(activity as Context)

        val backgroundList=RetrieveFavourites(activity as Context).execute().get()

        if(backgroundList.isEmpty()){
            progressLayout.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        }
        else{
        if(activity!=null) {
            for(i in backgroundList){
                dbRestaurantList.add(
                    Restaurant(i.restaurant_id,
                        i.restaurantName,
                        i.restaurantRating,
                        i.restaurantCostForOne,
                        i.restaurantImageUrl))
            }
            progressLayout.visibility=View.GONE
            recyclerAdapter=FavouritesRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFavourite.adapter=recyclerAdapter
            recyclerFavourite.layoutManager=layoutManager
        }
        }

        return view
    }

    class RetrieveFavourites(val context:Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}