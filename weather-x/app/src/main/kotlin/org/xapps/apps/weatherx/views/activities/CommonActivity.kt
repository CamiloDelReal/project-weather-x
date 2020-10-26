package org.xapps.apps.weatherx.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.weatherx.databinding.ActivityCommonBinding


@AndroidEntryPoint
class CommonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}