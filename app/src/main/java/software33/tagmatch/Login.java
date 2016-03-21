package software33.tagmatch;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {
    /*Declaracions ButterKnife*/
    @Bind(R.id.log) Button login;
    @Bind(R.id.reg) Button reg;
    @Bind(R.id.forg) Button forg;

    @Bind(R.id.name) EditText username;
    @Bind(R.id.pass) EditText passw;


    @OnClick(R.id.log)
    protected void validate_login() throws IOException {
        String name = new String();
        String pass = new String();
        if(username.getText() != null) {
            name = username.getText().toString();
        }
        if(passw.getText() != null) {
            pass = passw.getText().toString();
        }
        URL URL_connect = new URL("http://192.168.1.1/");

        Authenticator.setDefault(new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("myuser","mypass".toCharArray()); ////Cal modificar aixo
            }});

        HttpURLConnection urlConnection = (HttpURLConnection) URL_connect.openConnection();

        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(name);
        parameters.add(pass);

       // JSONArray jdata = parameters;
    }

    @OnClick(R.id.reg)
    protected void intent_reg() {
        Toast.makeText(getApplicationContext(),username.getText().toString(),Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.forg)
    protected void intent_forg() {
        Toast.makeText(getApplicationContext(),username.getText().toString(),Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this); //Necessari per a q funcioni tot lo de ButterKnife
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView app_header = (TextView) findViewById(R.id.app_header);
        /* MÈTODE PER FER SERVIR FONTS EXTERNES*/
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");
        app_header.setTypeface(face);
        /* MÈTODE PER FER SERVIR FONTS EXTERNES*/
    }

}
