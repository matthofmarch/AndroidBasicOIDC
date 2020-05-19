package com.gmail.hofmarchermatthias.androidbasicoidc

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.okta.oidc.*
import com.okta.oidc.storage.SharedPreferenceStorage
import com.okta.oidc.util.AuthorizationException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = OIDCConfig.Builder()
            .clientId("0oach4dlaCXy1Azzu4x6")
            .redirectUri("com.okta.dev-403499:/login")
            .endSessionRedirectUri("com.okta.dev-403499:/logout")
            .scopes("openid", "profile", "offline_access")
            .discoveryUri("https://dev-403499.okta.com")
            .create()

        val webAuthClient = Okta.WebAuthBuilder()
            .withConfig(config)
            .withContext(this)
            .withStorage(SharedPreferenceStorage(this))
            .withCallbackExecutor(Executors.newSingleThreadExecutor())
            .supportedBrowsers("com.android.chrome", "org.mozilla.firefox")
            .create()

        webAuthClient.registerCallback(object: ResultCallback<AuthorizationStatus,
                AuthorizationException>{
            override fun onSuccess(result: AuthorizationStatus) {
                Toast.makeText(this@MainActivity, "Your in!", Toast.LENGTH_LONG).show()
                Log.d(this.javaClass.simpleName, "SignInSuccess")
             }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login cancelled!", Toast.LENGTH_LONG).show()
                Log.d(this.javaClass.simpleName, "SignInCancel")            }

            override fun onError(msg: String?, exception: AuthorizationException?) {
                Toast.makeText(this@MainActivity, "Something went wrong, please try again!", Toast.LENGTH_LONG).show()
                Log.d(this.javaClass.simpleName, "SignInError")
            }

        }, this)

        if (!webAuthClient.sessionClient.isAuthenticated) {
            webAuthClient.signIn(
                this, AuthenticationPayload.Builder()
                    .setLoginHint("Hint: Enter your password")
                    .build()
            )
        }
    }
}
