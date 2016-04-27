package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import software33.tagmatch.R;

public class TagMatchGetBitmapAsyncTask extends AsyncTask<JSONObject, Void, Bitmap> {
    private URL url;
    private Context context;

    public TagMatchGetBitmapAsyncTask(String url2, Context coming_context) {
        try {
            url = new URL(url2);
            context = coming_context;
        } catch (MalformedURLException e) {
            Log.v("TagMatchGetAsyncTask", "", e);
        }
    }

    protected Bitmap doInBackground(final JSONObject... params) {

        try {
            final String user = params[0].getString("username").toString();
            final String password = params[0].getString("password").toString();

            String userPass = user + ":" + password;

            String basicAuth;
            if (url.getHost().contains("heroku")) {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.NO_WRAP));
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestProperty("Authorization", basicAuth);

                con.connect();

                InputStream aux;

                if (con.getResponseCode() >= 400){
                    aux = con.getErrorStream();
                }
                else
                    aux = con.getInputStream();

                con.disconnect();

                return Picasso.with(context).load(con.getHeaderField("Location")).error(R.drawable.image0).get();
            }
            else {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.DEFAULT));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Authorization", basicAuth);

                con.connect();

                InputStream aux;

                if (con.getResponseCode() >= 400){
                    aux = con.getErrorStream();
                }
                else
                    aux = con.getInputStream();

                con.disconnect();

                return Picasso.with(context).load(con.getHeaderField("Location")).error(R.drawable.image0).get();
            }
        } catch (IOException | JSONException e) {
            Log.e("error", e.getMessage());
            return null;
        }
    }
}
