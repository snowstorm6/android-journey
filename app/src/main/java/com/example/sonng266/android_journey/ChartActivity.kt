package com.example.sonng266.android_journey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sonng266.chart.ChartView

class ChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        val chart = findViewById<ChartView>(R.id.chart)
    }
}