package software33.tagmatch.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import software33.tagmatch.R;

public class EditProfile extends AppCompatActivity {

    TableLayout tl;
    int lastTableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        lastTableID = 0;

        tl = (TableLayout) findViewById(R.id.edit_profile_table);

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void refreshID() {
        for(lastTableID = 0; lastTableID < tl.getChildCount(); lastTableID++){
            TableRow row = (TableRow) tl.getChildAt(lastTableID);
            Button btn = (Button) row.getChildAt(1);
            btn.setText(String.valueOf(lastTableID));
        }
    }

    public void addInterest(View view) {
        EditText et = (EditText) findViewById(R.id.edit_profile_et_interests);
        String interest = et.getText().toString();
        TextView tv = new TextView(this);
        tv.setText(interest);
        Button btn = new Button(this);
        btn.setBackground(getDrawable(android.R.drawable.ic_menu_delete));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(v);
            }
        });
        String sBtn = String.valueOf(lastTableID);
        btn.setText(sBtn);
        btn.setTextColor(getResources().getColor(android.R.color.transparent));
        TableRow row = new TableRow(this);
        row.addView(tv,0);
        row.addView(btn,1);
        tl.addView(row, lastTableID++);
        et.setText("");
    }

    private void deleteRow(View v){
        int rowID = Integer.parseInt(((Button) v).getText().toString());
        tl.removeViewAt(rowID);
        refreshID();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
