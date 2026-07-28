package com.vfite.football

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vfite.football.R
import org.kodein.di.conf.DIGlobalAware

class MainActivity : AppCompatActivity(), DIGlobalAware {

    lateinit var rootView: LinearLayout
    lateinit var txtData: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtData = findViewById(R.id.txtData)
        rootView = findViewById<LinearLayout>(R.id.rootView)
    }

}