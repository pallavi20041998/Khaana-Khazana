package com.pallavichaurasia26.khaanakhazana.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class ResetPasswordActivity : AppCompatActivity() {


    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmNewPassword: EditText
    lateinit var btnSubmit: Button
    var mobileNumber: String? = null
    var newPassword: String = ""
    var confirmNewPassword: String = ""
    var otp: String = ""
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSubmit.setOnClickListener {

            otp = etOTP.text.toString()

            newPassword = etNewPassword.text.toString()

            confirmNewPassword = etConfirmNewPassword.text.toString()

            if (intent != null) {
                mobileNumber = intent.getStringExtra("mobileNumber")
            }


            if (otp != "" && newPassword != "" && confirmNewPassword != "") {

                if (otp.length==4 && newPassword.length>=6 && newPassword==confirmNewPassword) {

                    val queue = Volley.newRequestQueue(this@ResetPasswordActivity)

                    val url = " http://13.235.250.119/v2/reset_password/fetch_result/"

                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", newPassword)
                    jsonParams.put("otp", otp)

                    if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {

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
                                            this@ResetPasswordActivity,
                                            "Password has successfully changed.",
                                            Toast.LENGTH_SHORT)
                                            .show()

                                        deleteAllSharedPrefs()

                                       val intent =
                                           Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                                    } else {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            "Incorrect Credentials!! Try Again",
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

                }
                else if(otp.length<4) {
                    Toast.makeText(this@ResetPasswordActivity, "OTP should be of 4 digits", Toast.LENGTH_SHORT).show()
                }
                else if(newPassword.length<6) {
                    Toast.makeText(this@ResetPasswordActivity, "Password should be of atleast 6 characters", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@ResetPasswordActivity, "Password and Confirm password should be same", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
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