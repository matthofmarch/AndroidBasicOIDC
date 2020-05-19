package com.gmail.hofmarchermatthias.androidbasicoidc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.okta.oidc.*
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.SharedPreferenceStorage
import com.okta.oidc.util.AuthorizationException
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = OIDCConfig.Builder()
            .clientId("0oaclbqygEZYNsxgo4x6")
            .redirectUri("com.okta.dev-142408:/login")
            .endSessionRedirectUri("com.okta.dev-142408:/logout")
            .scopes("openid", "email", "profile", "offline_access")
            .discoveryUri("https://dev-142408.okta.com")
            .create()

        val webAuthClient = Okta.WebAuthBuilder()
            .withConfig(config)
            .withContext(this)
            .withStorage(SharedPreferenceStorage(this))
            .setRequireHardwareBackedKeyStore(false)
            .create()

        webAuthClient.registerCallback(object: ResultCallback<AuthorizationStatus,
                AuthorizationException>{
            override fun onSuccess(result: AuthorizationStatus) {
                when(result){
                    AuthorizationStatus.AUTHORIZED->{
                        Log.d(this.javaClass.simpleName, "SignInSuccess")
                        getUserInfo(webAuthClient)
                    }
                    AuthorizationStatus.SIGNED_OUT->{
                        Log.d(this.javaClass.simpleName, "SignOutSuccess")
                    }
                }

             }

            override fun onCancel() {
                Log.d(this.javaClass.simpleName, "SignInCancel")
            }

            override fun onError(msg: String?, exception: AuthorizationException?) {
                Log.d(this.javaClass.simpleName, "SignInError")
            }

        }, this)

        btn_signin.setOnClickListener {
            webAuthClient.signIn(this, null)
        }

        if(webAuthClient.sessionClient.isAuthenticated){
            getUserInfo(webAuthClient)
        }
    }

    private fun getUserInfo(webAuthClient: WebAuthClient) {
        webAuthClient.sessionClient.getUserProfile(object: RequestCallback<UserInfo,
                AuthorizationException>{
            override fun onSuccess(result: UserInfo) {
                tv_userinfo.text = result["email"] as String
            }

            override fun onError(error: String?, exception: AuthorizationException?) {

                Log.d(this.javaClass.simpleName, "Unable to get userinfo")
            }

        })
    }

}
