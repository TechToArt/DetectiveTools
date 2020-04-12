package com.ax.detectivetools

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ax.detectivetools.ImageInfo.ImageInfoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toImageInfo.setOnClickListener {
            intent = Intent(this, ImageInfoActivity::class.java)
            startActivity(intent)
        }
    }
}
