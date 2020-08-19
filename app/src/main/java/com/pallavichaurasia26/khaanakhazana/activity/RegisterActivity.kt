package com.pallavichaurasia26.khaanakhazana.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var sharedPreferences: SharedPreferences

    var name:String=""
    var email:String=""
    var mobileNumber:String=""
    var address:String=""
    var password:String=""
    var confirmPassword:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_register)

        if (isLoggedIn) {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegister.setOnClickListener {

            name= etName.text.toString()
            email= etEmail.text.toString()
            mobileNumber= etMobileNumber.text.toString()
            address= etDeliveryAddress.text.toString()
            password= etPassword.text.toString()
            confirmPassword= etConfirmPassword.text.toString()

            if(name!="" && email!="" && mobileNumber!="" && address!="" && password!="" && confirmPassword!="") {

                if (name.length >= 3 && mobileNumber.length==10 && password==confirmPassword && password.length>=4) {

                    val queue = Volley.newRequestQueue(this@RegisterActivity)

                    val url = "http://13.235.250.119/v2/register/fetch_result/"

                    val jsonParams=JSONObject()

                    jsonParams.put("name",name)
                    jsonParams.put("mobile_number",mobileNumber)
                    jsonParams.put("password",password)
                    jsonParams.put("address",address)
                    jsonParams.put("email",email)


                    if(ConnectionManager().checkConnectivity(this)) {

                        val jsonRequest = object : JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonParams,
                            Response.Listener { response->

                                try {

                                    val data = response.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Successful",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()

                                        val intent =
                                            Intent(this@RegisterActivity, MainActivity::class.java)
                                        savePreferences(name,mobileNumber,email,address)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Already registered",
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
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this,
                                    "Some Unexpected Error Occurred!!",
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
                        queue.add(jsonRequest)
                    }
                    else{
                        val dialog= AlertDialog.Builder(this)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found")
                        dialog.setPositiveButton("Exit"){text,listener->
                            ActivityCompat.finishAffinity(this)

                        }
                        dialog.create()
                        dialog.show()
                    }

                }else if(name.length<3) {
                    Toast.makeText(this@RegisterActivity, "Name should be of atleast 3 characters", Toast.LENGTH_SHORT).show()
                }
                else if(mobileNumber.length<10) {
                    Toast.makeText(this@RegisterActivity, "Enter 10 digit mobile number ", Toast.LENGTH_SHORT).show()
                }
                else if(password.length<4) {
                    Toast.makeText(this@RegisterActivity, "Password should be of atleast 4 characters", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@RegisterActivity, "Password and Confirm password should be same", Toast.LENGTH_SHORT).show()
                }
            }

            else{
                Toast.makeText(this@RegisterActivity, "Fill all the details", Toast.LENGTH_SHORT).show()
            }


        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    fun savePreferences(name:String,mobileNumber:String,email:String,address:String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("mobileNumber",mobileNumber).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("address",address).apply()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}