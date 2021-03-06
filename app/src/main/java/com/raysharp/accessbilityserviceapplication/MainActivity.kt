package com.raysharp.accessbilityserviceapplication
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raysharp.accessbilityserviceapplication.databinding.ActivityMainBinding
import com.raysharp.accessbilityserviceapplication.service.AccessbilityServiceImp
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.CheckBox
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import com.blankj.utilcode.util.ToastUtils


class MainActivity : AppCompatActivity() {

    private val serviceConn: ServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("MainActivity", "onServiceConnected -->" )
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("MainActivity", "onServiceDisconnected -->" )
        }

    }
    private lateinit var binding: ActivityMainBinding

    private var allCheckBoxes = arrayListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allCheckBoxes.add(binding.dailyTask)
        allCheckBoxes.add(binding.commonInvite)
        allCheckBoxes.add(binding.surviveTask)
        allCheckBoxes.add(binding.friendTask)
        allCheckBoxes.add(binding.luckTask)
        allCheckBoxes.add(binding.challengeTask)
        allCheckBoxes.add(binding.seniorInvite)
        allCheckBoxes.add(binding.arenaTask)
        allCheckBoxes.add(binding.searchTask)
        allCheckBoxes.add(binding.taskBar)


        if (!isAccessibilitySettingsOn(this)){
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.allCheckboxStates.setOnCheckedChangeListener { _, b ->
            allCheckBoxes.forEach {
                it.isChecked = b
            }
        }

        binding.championTried.setOnCheckedChangeListener { _, b ->
            if (b){
                val intent = Intent()
                // ???????????????????????????
                val status = ArrayList<Int>()
                status.add(200)
                intent.putIntegerArrayListExtra("status", status)
                // ????????????????????????????????????????????????????????????????????????????????????
                intent.action = Command.ACTION_MODIFTY
                // ????????????
                sendBroadcast(intent)
                ToastUtils.showShort("????????????")
            }
        }
        binding.uploadModify.setOnClickListener {
            val status = getAllCheckBoxState()
            val intent = Intent()
            // ???????????????????????????
            intent.putIntegerArrayListExtra("status", status)
            // ????????????????????????????????????????????????????????????????????????????????????
            intent.action = Command.ACTION_MODIFTY
            // ????????????
            sendBroadcast(intent)
            ToastUtils.showShort("????????????")
        }

        val intent = Intent(this,AccessbilityServiceImp::class.java)
        startService(intent)
        GameNotification.createNotificaton(this)
        binding.accessibilityService.setOnClickListener {
            val intent = Intent()
            // ????????????????????????????????????????????????????????????????????????????????????
            intent.action = Command.ACTION_STOP
            // ????????????
            sendBroadcast(intent)
            ToastUtils.showShort("????????????")
        }
    }


    private fun getAllCheckBoxState(): ArrayList<Int>{
        val states = ArrayList<Int>()
        if(allCheckBoxes.isNotEmpty()){
            states.addAll(allCheckBoxes.map {
                if (it.isChecked)
                    1
                else
                    0
            }.toList())
        }
        return states
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConn)
    }


    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        // TestService??????????????????
        val service = packageName + "/" + AccessbilityServiceImp::class.java.canonicalName
        Log.i(TAG, "service:$service")
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.v(
                        TAG,
                        "-------------- > accessibilityService :: $accessibilityService $service"
                    )
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(
                            TAG,
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }

}