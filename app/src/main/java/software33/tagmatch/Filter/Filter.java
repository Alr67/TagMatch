package software33.tagmatch.Filter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import software33.tagmatch.R;

public class Filter extends AppCompatActivity implements View.OnClickListener {

    private CheckBox ch1,ch2,ch3,ch4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ch1 = (CheckBox) findViewById(R.id.checkBox);
        ch1.setOnClickListener(this);
        ch2 = (CheckBox) findViewById(R.id.checkBox2);
        ch2.setOnClickListener(this);
        ch3 = (CheckBox) findViewById(R.id.checkBox3);
        ch3.setOnClickListener(this);
        ch4 = (CheckBox) findViewById(R.id.checkBox4);
        ch4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.checkBox):
                if(((CheckBox) v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Bro, try Android :)", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.checkBox2):
                if(((CheckBox) v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Bro, try Android :)", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.checkBox3):
                if(((CheckBox) v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Bro, try Android :)", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.checkBox4):
                if(((CheckBox) v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Bro, try Android :)", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
