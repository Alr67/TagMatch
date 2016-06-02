package software33.tagmatch.Settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import software33.tagmatch.AdCards.Home;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;



public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TwitterLoginButton twitterLoginButton;
    private TwitterSession session;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "hGoEDMHh7OIOFJGZIQFEeiILX";
    private static final String TWITTER_SECRET = "qE0fbRktylzz0KGUxmph9Hs76ywJ6CAMUp1ylBkR2EKtsGxdV3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        String[] permissions = {Manifest.permission.INTERNET};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.nav_settings);
        loginButton = (LoginButton) findViewById(R.id.Fblogin_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        Helpers.setNavHeader(nav_header,getApplicationContext());
        navigationView.addHeaderView(nav_header);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback( callbackManager ,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                connectUserWithFacebook(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Error on Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                connectUserWithTwitter(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "Error on Facebook", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences prefs =  this.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString("twitterUser" , null);
        if(username != null){
            Log.i("lo hago " , "dsi");
            twitterLoginButton.setText("Logged in as "+ username);
            twitterLoginButton.setClickable(false);
        }
    }

    public void goToPasswordChange(View view){
        Intent intent = new Intent(this, PasswordChange.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    private void connectUserWithFacebook(LoginResult loginResult){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("facebookToken",loginResult.getAccessToken());
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users", this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error, getApplicationContext());
                    } catch (JSONException ignored) {}
                }
            }.execute(jObject);
        } catch (Exception ignored) {}

    }

    private void connectUserWithTwitter(Result<TwitterSession> result){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("twitterToken",result.data.getAuthToken());
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users", this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error, getApplicationContext());
                    } catch (JSONException ignored) {}
                }
            }.execute(jObject);
        } catch (Exception ignored) {}
        SharedPreferences.Editor editor = this.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        String username = result.data.getUserName();
        editor.putString("twitterUser", username);
        editor.apply();
        twitterLoginButton.setText("Logged in as "+ result.data.getUserName());
        twitterLoginButton.setClickable(false);
    }
}
