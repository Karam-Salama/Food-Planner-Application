package com.example.foodplannerapplication.modules.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.utils.functions.Validation
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.facebook.AccessToken
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
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


    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }

    override fun onStart() {
        super.onStart()
        checkUserState()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        initViews()
        setupGoogleSignIn()
        setupFacebookSignIn()
        setUpListeners()

    }


    private fun checkUserState() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
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

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
                signInWithEmailAndPassword(
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim()
                )
            }
        }

        btnGoogleLogin.setOnClickListener {
            signIn()
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

    private fun signInWithEmailAndPassword(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login successfully.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithGoogle:success")
                    Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Log.e(TAG, "signInWithGoogle:failure", task.exception)
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // لمعالجة تسجيل الدخول بالفيسبوك
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // لمعالجة تسجيل الدخول بجوجل
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
                signInAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In Failed", e)
                Toast.makeText(this, "Google Sign-In Failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logOut() // لتجنب تسجيل الدخول التلقائي

        btnFacebookLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this, listOf("email", "public_profile")
            )
        }

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                Toast.makeText(this@LoginActivity, "Facebook login canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "facebook:onError", error)
                Toast.makeText(this@LoginActivity, "Facebook login failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithFacebook:success")
                    Toast.makeText(this, "Facebook Sign-In successful", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Log.e(TAG, "signInWithFacebook:failure", task.exception)
                    Toast.makeText(this, "Facebook Sign-In failed", Toast.LENGTH_LONG).show()
                }
            }
    }
}






