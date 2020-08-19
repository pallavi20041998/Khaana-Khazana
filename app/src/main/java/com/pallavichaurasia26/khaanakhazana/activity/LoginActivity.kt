package com.pallavichaurasia26.khaanakhazana.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegisterYourself: TextView
    var mobileNumber: String = ""
    var password: String = ""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_login)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegister)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"

        btnLogin.setOnClickListener {

            mobileNumber = etMobileNumber.text.toString()

            password = etPassword.text.toString()

            if (mobileNumber != "" && password != "") {

                if (mobileNumber.length == 10 && password.length >= 4) {

                    val queue = Volley.newRequestQueue(this@LoginActivity)

                    val url = "http://13.235.250.119/v2/login/fetch_result/"

                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", password)

                    if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                        val jsonRequest = object : JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonParams,
                            Response.Listener { response ->

                                try {

                                    val data = response.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Logged In Successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)

                                        val d=data.getJSONObject("data")
                                        val name=d.getString("name")
                                        val mobile=d.getString("mobile_number")
                                        val email=d.getString("email")
                                        val address=d.getString("address")
                                        val userId=d.getString("user_id")
                                        savePreferences(name,mobile,email,address,userId)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Incorrect Credentials",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
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

                } else if (mobileNumber.length<10) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter 10 digit mobile number",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter password whose length is more than 3",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {
                Toast.makeText(this, "Enter mobile and password", Toast.LENGTH_SHORT).show()
            }
        }

        txtForgotPassword.setOnClickListener {

            Toast.makeText(this@LoginActivity, "Set your new password", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(intent)
        }

        txtRegisterYourself.setOnClickListener {

            Toast.makeText(this@LoginActivity, "Register Yourself", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreferences(name:String,mobileNumber:String,email:String,address:String,userId:String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("mobileNumber",mobileNumber).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("address",address).apply()
        sharedPreferences.edit().putString("user_id",userId).apply()
    }
}