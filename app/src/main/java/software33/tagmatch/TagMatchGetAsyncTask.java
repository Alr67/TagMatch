package software33.tagmatch;

import android.os.AsyncTask;
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
import java.util.HashMap;
import java.util.Map;


public class TagMatchGetAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {
    private URL url;

    public TagMatchGetAsyncTask(String url2) {
        try {
            url = new URL(url2);
        } catch (MalformedURLException e) {
            Log.v("TagMatchGetAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(JSONObject... params) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(params[0].toString());
            wr.flush();
            wr.close();

            con.getOutputStream().close();
            con.connect();

            JSONObject aux;

            if (con.getResponseCode() >= 400)
                aux = new JSONObject(iStreamToString(con.getErrorStream()));
            else
                aux = new JSONObject(iStreamToString(con.getInputStream()));

            con.disconnect();

            return aux;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<>();
            map.put("error", "Unexpected Error");
            return new JSONObject(map);
        }

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
