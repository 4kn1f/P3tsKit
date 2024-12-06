package com.application.p3tskit.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.application.p3tskit.databinding.ActivityRegisterBinding
import com.application.p3tskit.ui.ViewModelFactory
import com.application.p3tskit.ui.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegister()
        observeViewModel()
    }

    private fun observeViewModel() {
        registerViewModel.registerResult.observe(this){ registerResult ->
            if (registerResult != null){
                AlertDialog.Builder(this).apply {
                    setTitle("Horray")
                    setMessage("You've successfully created an account! Login now:D")
                    setPositiveButton("Login"){ _, _ ->
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    show()
                }
            }
        }

        registerViewModel.isLoading.observe(this){ isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.registerButton.isEnabled = !isLoading
        }

        registerViewModel.errorMessage.observe(this){ errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRegister() {
        binding.apply {
            registerButton.setOnClickListener{
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                   Toast.makeText(this@RegisterActivity, "Please fill in all field first", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("RegisterActivity", "Name: $name, Email: $email, Password: $password")
                    lifecycleScope.launch {
                        registerViewModel.register(name, email, password)
                    }
                }
            }
            tvLoginRedirect.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}