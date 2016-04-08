package software33.tagmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import software33.tagmatch.Advertisement.NewAdvertisement;
import software33.tagmatch.Chat.MainChatActivity;
import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.Utils.Helpers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button logout,xat,anunci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the Firebase library

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logout = (Button) findViewById(R.id.logout);
        anunci = (Button) findViewById(R.id.anunci);
        xat = (Button) findViewById(R.id.xat);
        logout.setOnClickListener(this);
        anunci.setOnClickListener(this);
        xat.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void startMainChatActivity(View view) {
      //  Inent intent = new Intent(this, MainChatActivity.class);
        //  startActivity(intent);t
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.xat):
                Intent intent = new Intent(this, MainChatActivity.class);
                startActivity(intent);
                finish();
                break;
            case (R.id.anunci):
                Intent intent2 = new Intent(this, NewAdvertisement.class);
                startActivity(intent2);
                finish();
                break;
            case (R.id.logout):
                new Helpers().logout(getApplicationContext());
                Intent intent3 = new Intent(this, Login.class);
                startActivity(intent3);
                finish();
                break;
        }
    }
}
