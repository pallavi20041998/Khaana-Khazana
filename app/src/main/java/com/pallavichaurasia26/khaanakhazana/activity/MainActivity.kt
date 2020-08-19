package com.pallavichaurasia26.khaanakhazana.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pallavichaurasia26.khaanakhazana.*
import com.pallavichaurasia26.khaanakhazana.fragment.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var drawerHeader: View

    var previousMenuItem: MenuItem? = null

    var name: String? = "Saloni"
    var mobileNumber: String? = "9839841850"
    var email: String? = "saloni@gmail.com"
    var address: String? = "E 4247 Sector 12 RJPM LKO"
    lateinit var sharedPreferences: SharedPreferences

    lateinit var txtName: TextView
    lateinit var txtNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_main)

        name = sharedPreferences.getString("name", name)
        mobileNumber = sharedPreferences.getString("mobileNumber", mobileNumber)
        email = sharedPreferences.getString("email", email)
        address = sharedPreferences.getString("address", address)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        drawerHeader = navigationView.getHeaderView(0)

        setUpToolbar()

        txtName = drawerHeader.findViewById(R.id.txtName)
        txtNumber = drawerHeader.findViewById(R.id.txtNumber)

        txtName.text = name
        txtNumber.text="+91 $mobileNumber"

        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment(name, mobileNumber, email, address)
                        )
                        .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqsFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {

                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want logout?")
                        .setPositiveButton("Yes") { text, listener ->
                            deleteAllSharedPrefs()
                             val intentLogin = Intent(this@MainActivity, LoginActivity::class.java)
                             startActivity(intentLogin)

                        }
                        .setNegativeButton("No") { text, listener ->
                            openDashboard()
                        }
                        .create()
                        .show()

                    drawerLayout.closeDrawers()

                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        val fragment =
            DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()

        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is DashboardFragment -> openDashboard()

            else -> super.onBackPressed()
        }

    }

    fun deleteAllSharedPrefs() {
        val preferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
        finish()
    }


}