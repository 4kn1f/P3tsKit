package com.application.p3tskit.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.application.p3tskit.MainActivity
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.pref.dataStore
import com.application.p3tskit.databinding.ActivitySplashBinding
import com.application.p3tskit.ui.profile.ProfilePreferences
import com.application.p3tskit.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var profilePreferences: ProfilePreferences
    private lateinit var authPreferences: AuthPreferences
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profilePreferences = ProfilePreferences.getInstance(dataStore)
        authPreferences = AuthPreferences.getInstance(this)

        Handler().postDelayed({
            lifecycleScope.launch {
                profilePreferences.getThemeSetting().collect { isDarkModeActive ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )

                    authPreferences.getSession().collect { user ->
                        val intent = if (user.isLogin && user.token.isNotEmpty()) {
                            Intent(this@SplashActivity, MainActivity::class.java)
                        } else {
                            Intent(this@SplashActivity, WelcomeActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }, 5000)
    }
}
