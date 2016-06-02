package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;
import software33.tagmatch.Utils.Helpers;

public abstract class TagMatchPutAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

    private URL url;
    private Context context;

    public TagMatchPutAsyncTask(String url, Context context) {
        try {
            this.url = new URL(url);
            this.context = context;
        } catch (MalformedURLException e) {
            Log.e("TagMatchPutAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(JSONObject... params) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            connectUser(con);
            con.setConnectTimeout(35000);
            con.setReadTimeout(35000);
            con.setDoInput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(params[0].toString());
            wr.flush();
            wr.close();

            con.getOutputStream().close();
            con.connect();

            /*>=400 errorStream
            else inputStream*/

            JSONObject aux;

            Log.i("put status code", Integer.toString(con.getResponseCode()));

            if (con.getResponseCode() >= 400)
                aux = new JSONObject(iStreamToString(con.getErrorStream()));
            else
                aux = new JSONObject(iStreamToString(con.getInputStream()));

            con.disconnect();

            return aux;

        } catch (IOException | JSONException e) {
            Log.e("error", e.getMessage());
            Map<String, String> map = new HashMap<>();
            if(e.getMessage().contains("failed to connect to")){
                if(e.getMessage().contains("Network is unreachable")){
                    map.put("error", context.getString(R.string.no_network_connection));
                } else {
                    map.put("error", context.getString(R.string.connection_timeout));
                }
            } else {
                map.put("error", "Unexpected Error");
            }
            return new JSONObject(map);
        }
    }

    private void connectUser(HttpURLConnection c) {
        ArrayList<String> personalData = new ArrayList<String>();
        personalData = new Helpers().getPersonalData(context);
        final String user = personalData.get(0);
        final String password = personalData.get(1);

        String userPass = user + ":" + password;
        c.setRequestProperty("Authorization", "Basic " +
                new String(Base64.encode(userPass.getBytes(), Base64.DEFAULT)));
    }

    public String iStreamToString(InputStream is1) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }

}
