package com.example.foodplannerapplication.modules.auth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.helpers.SnackbarHelper
import com.example.foodplannerapplication.core.functions.Validation
import com.example.foodplannerapplication.modules.auth.ViewModels.LoginViewModel
import com.example.foodplannerapplication.modules.auth.ViewModels.LoginViewModelFactory
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var tvSignup: TextView
    private lateinit var tvForgetPassword: TextView
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnFacebookLogin: Button
    private lateinit var btnGoogleLogin: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onStart() {
        super.onStart()
        checkUserState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.loginWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                viewModel.setErrorState("Google Sign-In Failed: ${e.statusCode}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        initViews()
        setupGoogleSignIn()
        setupFacebookSignIn()
        setupViewModel()
        observeLoginState()
        setUpListeners()
    }

    private fun initViews() {
        tvSignup = findViewById(R.id.tv_signup)
        tvForgetPassword = findViewById(R.id.tv_forgetPassword)
        etEmail = findViewById(R.id.edt_Email)
        etPassword = findViewById(R.id.edt_Password)
        btnLogin = findViewById(R.id.btn_login)
        btnGoogleLogin = findViewById(R.id.btn_google)
        btnFacebookLogin = findViewById(R.id.btn_facebook)
    }

    private fun setUpListeners() {
        tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            if (validateInputs()) {
                viewModel.loginWithEmail(
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim()
                )
            }
        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun validateInputs(): Boolean {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        var isValid = true

        val emailError = Validation.validateEmail(email)
        etEmail.error = emailError
        if (emailError != null) isValid = false

        val passwordError = Validation.validatePassword(password)
        etPassword.error = passwordError
        if (passwordError != null) isValid = false

        return isValid
    }

    private fun setupViewModel() {
        val repository = AuthRepository()
        viewModel = ViewModelProvider(this, LoginViewModelFactory(repository)).get(LoginViewModel::class.java)
    }

    private fun observeLoginState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    SnackbarHelper.showLoadingSnackbar(
                        findViewById(android.R.id.content),
                        state.message
                    )
                }
                is LoginViewModel.LoginState.Success -> {
                    SnackbarHelper.dismissCurrentSnackbar()
                    navigateToHome()
                }
                is LoginViewModel.LoginState.Error -> {
                    SnackbarHelper.dismissCurrentSnackbar()
                    SnackbarHelper.showErrorSnackbar(
                        findViewById(android.R.id.content),
                        state.message,
                        "Retry",
                        { retryLogin() }
                    )
                }
                is LoginViewModel.LoginState.EmailNotVerified -> {
                    SnackbarHelper.dismissCurrentSnackbar()
                    Firebase.auth.signOut()
                    handleEmailNotVerified()
                }
            }
        }
    }

    private fun retryLogin() {
        if (etEmail.text?.isNotEmpty() == true && etPassword.text?.isNotEmpty() == true) {
            viewModel.loginWithEmail(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }
    }

    private fun handleEmailNotVerified() {
        DialogHelper.showGenericDialog(
            context = this,
            message = "Please verify your email first. Check your inbox.",
            positiveButtonText = "Resend Verification",
            negativeButtonText = "OK",
            onPositiveAction = { resendVerificationEmail() }
        )
    }

    private fun resendVerificationEmail() {
        Firebase.auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserState() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logOut()

        btnFacebookLogin.setOnClickListener {
            SnackbarHelper.showLoadingSnackbar(
                findViewById(android.R.id.content),
                "Connecting to Facebook..."
            )
            LoginManager.getInstance().logInWithReadPermissions(
                this, listOf("email", "public_profile")
            )
        }

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                viewModel.loginWithFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                SnackbarHelper.dismissCurrentSnackbar()
                viewModel.setErrorState("Facebook login canceled")
            }

            override fun onError(error: FacebookException) {
                SnackbarHelper.dismissCurrentSnackbar()
                viewModel.setErrorState("Facebook login failed: ${error.message}")
            }
        })
    }
}