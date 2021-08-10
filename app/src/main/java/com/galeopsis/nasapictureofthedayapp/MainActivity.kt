package com.galeopsis.nasapictureofthedayapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.galeopsis.nasapictureofthedayapp.ui.NasaSearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NasaSearchFragment.newInstance())
                .commitNow()
        }
    }
}
