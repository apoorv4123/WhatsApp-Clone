package com.example.whatsappclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.ScreenSlidePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = ScreenSlidePagerAdapter(this)

        // Set the action on tab. If you click any other tab, the fragment changes
        TabLayoutMediator(
            tabs,
            viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                when (position) {
                    0 -> tab.text = "CHATS"
                    1 -> tab.text = "PEOPLE"
                }
            }).attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}