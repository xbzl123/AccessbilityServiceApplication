package com.raysharp.accessbilityserviceapplication

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.raysharp.accessbilityserviceapplication.ui.main.SectionsPagerAdapter
import com.raysharp.accessbilityserviceapplication.databinding.ActivityMainBinding
import com.raysharp.accessbilityserviceapplication.service.AccessbilityServiceImp
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.util.Log

import android.view.accessibility.AccessibilityManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab
        AutoTouch.width = this.windowManager.defaultDisplay.width
        AutoTouch.height = this.windowManager.defaultDisplay.height
//        val order = arrayOf("input", "tap", "" + 500.0, "" + 1500.0)
        Log.d("MainActivity", "checkRootPermission -->" +
                AutoTouch.width+",AutoTouch.height = "+AutoTouch.height)

        val intent = Intent(this,AccessbilityServiceImp::class.java)
        startService(intent)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        GameNotification.createNotificaton(this)
    }

    private fun enabled(name: String): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val serviceInfos =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        val installedAccessibilityServiceList = am.installedAccessibilityServiceList
        for (info in installedAccessibilityServiceList) {
            Log.d("MainActivity", "all -->" + info.id)
            if (name == info.id) {
                return true
            }
        }
        return false
    }
}