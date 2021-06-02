package com.gamerguide.android.starterapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.gamerguide.android.starterapp.helpers.ThemeManager
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.gamerguide.android.starterapp.databinding.ActivityMainBinding
import com.gamerguide.android.starterapp.datastore.UserManager
import com.google.android.material.snackbar.Snackbar
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import kotlinx.coroutines.launch

//It is important to maintain only a singleton instance of the datastore for a context
private val Context.dataStore by preferencesDataStore("user_pref")

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    //Create ThemeManager to set custom background images
    private var themeManager: ThemeManager? = null

    //Create Binding object for this activity
    private lateinit var binding: ActivityMainBinding

    //Crate a global variable to manage our User Manager
    private lateinit var userManager: UserManager


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    //Restore the data stored in Bundle during configuration changes and implement your custom logic
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Build a ViewPump for hooking into font
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/sourcesanspro.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        //Create View Binding instance for the current activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        themeManager = ThemeManager()

        //Getting reference to the UserManager where we will manage storing and retrieving data from datastore
        userManager = UserManager(dataStore)

    }


    // OnResume is called when the app receive focus, Do all UI related work here
    override fun onResume() {
        super.onResume()

        themeManager!!.setupImageBlurBackground(this, binding.background)
        binding.title.text = "USER PROFILE"

        //Reload a new background theme when use clicks the refresh button
        binding.reload.setOnClickListener {
            themeManager!!.setupImageBlurBackground(
                this,
                binding.background,
                true
            )
        }

        binding.save.setOnClickListener {
            storeUser();
        }

        /**
         * This function will retrieve all user information stored in datastore
         * and populate them in view
         */
        observerData();


    }

    private fun observerData() {

        userManager.userNameFlow.asLiveData().observe(this){ name ->
            name?.let{
                binding.name.text = name;
            }
        }

        userManager.userAgeFlow.asLiveData().observe(this){ age ->
            age?.let{
                binding.age.text = age.toString();
            }
        }
    }

    //This function saves the data to the user preferences
    private fun storeUser() {

        val name = binding.editName.text.toString()
        val age = binding.editAge.text.toString().trim().toInt()

        /**
         * Store the values in the shared preferences.
         * The method to store is a susend function, But we do not need to declare this function
         * as suspend because the call is only happening inside a coroutine scope
         */

        lifecycleScope.launch {
            userManager.storeUserData(age,name)
            Toast.makeText(this@MainActivity,"Information Saved",Toast.LENGTH_SHORT).show()

            /**
             * After storing the information in storage, We need to clear data in our views
             */
            binding.editName.text.clear()
            binding.editAge.text.clear()

        }

    }

    // Store any data in this bundle when app configuration change occurs
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    // Check the result obtained from activity and compare which activity sent it and if was succes, If yes, Continue with your logic
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

    }


    // Check if user presses the back button and save any important work before the activity is destroyed
    override fun onBackPressed() {

        Snackbar.make(
            binding.root,
            "Save important data here before the Activity is killed..",
            Snackbar.LENGTH_SHORT
        ).setAction("OK", { super.onBackPressed() })
            .show()
    }


}