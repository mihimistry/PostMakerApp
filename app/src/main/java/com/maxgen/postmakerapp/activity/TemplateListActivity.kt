package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maxgen.postmakerapp.R
import kotlinx.android.synthetic.main.activity_template_list.*

class TemplateListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cv_fixed.setOnClickListener {
            startActivity(Intent(this, Template1Activity::class.java))
        }

        cv_logo.setOnClickListener {
            startActivity(Intent(this, Template2Activity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}