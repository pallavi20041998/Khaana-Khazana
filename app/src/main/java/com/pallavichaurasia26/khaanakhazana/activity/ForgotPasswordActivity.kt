package com.pallavichaurasia26.khaanakhazana.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
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

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var imgLogo: ImageView
    lateinit var txtHeading: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    var mobileNumber: String = ""
    var email: String = ""
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        imgLogo = findViewById(R.id.imgLogo)
        txtHeading = findViewById(R.id.txtHeading)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        btnNext.setOnClickListener {

            mobileNumber = etMobileNumber.text.toString()
            email = etEmail.text.toString()

            if (mobileNumber != "" && email != "") {

                if (mobileNumber.length == 10) {

                    val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)

                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("email", email)

                    if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                        val jsonRequest = object : JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonParams,
                            Response.Listener { response ->

                                try {

                                    val data = response.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {

                                        val firstTry = data.getBoolean("first_try")
                                        if (firstTry) {
                                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                                            builder.setTitle("Information")
                                            builder.setMessage("Please check your registered Email for the OTP")
                                            builder.setCancelable(false)
                                            builder.setPositiveButton("Ok") { _, _ ->
                                                val intent = Intent(
                                                    this@ForgotPasswordActivity,
                                                    ResetPasswordActivity::class.java
                                                )
                                                intent.putExtra("mobileNumber",mobileNumber)
                                                startActivity(intent)
                                            }

                                            builder.create().show()
                                        } else {
                                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                                            builder.setTitle("Information")
                                            builder.setMessage("Please refer to the previous email for the OTP.")
                                            builder.setCancelable(false)
                                            builder.setPositiveButton("Ok") { _, _ ->
                                                val intent = Intent(
                                                    this@ForgotPasswordActivity,
                                                    ResetPasswordActivity::class.java
                                                )
                                                intent.putExtra("mobileNumber",mobileNumber)
                                                startActivity(intent)
                                            }
                                            builder.create().show()
                                        }

                                        /* Toast.makeText(
                                             this@ForgotPasswordActivity,
                                             "OTP send on registerd email",
                                             Toast.LENGTH_SHORT)
                                             .show()
                                     val intent =
                                         Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                                     intent.putExtra("mobileNumber",mobileNumber)

                                     startActivity(intent)*/
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Incorrect Credentials!!Try Again",
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

                } else {

                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Enter 10 digit mobile Number",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }

            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Enter mobile number and email",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}