package software33.tagmatch;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {

    private String uri;

    public ServerConnection(){
        uri = "http://" + "192.168.1.132" + ":8080";
    }

    public String userRegister(String username, String mail, String password) throws Exception {
        JSONObject jObject = new JSONObject();
        jObject.put("username", username);
        jObject.put("password", password);
        jObject.put("email", mail);

        Log.i("json", "json: " + jObject.toString());

        URL url = new URL(uri+"/users");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
        wr.write(jObject.toString());
        wr.flush();
        wr.close();

        con.getOutputStream().close();
        con.connect();

        /*>=400 errorStream
        else inputStream*/

        con.disconnect();

        if(con.getResponseCode() >= 400)
            return iStreamToString(con.getErrorStream());
        else
            return null;
    }

    public String iStreamToString(InputStream is1)
    {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb =  new StringBuilder();
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
