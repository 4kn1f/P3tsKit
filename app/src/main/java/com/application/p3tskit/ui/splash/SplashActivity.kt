package com.application.p3tskit.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.application.p3tskit.MainActivity
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.databinding.ActivitySplashBinding
import com.application.p3tskit.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authPreferences = AuthPreferences.getInstance(this)

        Handler().postDelayed({
            lifecycleScope.launch {
                authPreferences.getSession().collect { user ->
                    if (user.isLogin && user.token.isNotEmpty()) {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }
            }
        }, 6000)
    }
}
