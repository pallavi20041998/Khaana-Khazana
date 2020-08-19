package com.pallavichaurasia26.khaanakhazana.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.pallavichaurasia26.khaanakhazana.R
import com.pallavichaurasia26.khaanakhazana.adapter.DescriptionRecyclerAdapter

class PlaceOrderActivity : AppCompatActivity() {

    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        btnOk=findViewById(R.id.btnOk)

        btnOk.setOnClickListener{
            val intent= Intent(this@PlaceOrderActivity,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@PlaceOrderActivity)
        alterDialog.setTitle("Alert!")
        alterDialog.setMessage("Click on Ok")
        alterDialog.setPositiveButton("Okay") { text, listener ->
        }
        alterDialog.show()
    }
}