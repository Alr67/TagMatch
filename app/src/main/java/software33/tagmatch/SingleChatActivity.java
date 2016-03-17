package software33.tagmatch;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        Bundle b = getIntent().getExtras();
        String userName = b.getString("UserName");
        String titleProduct = b.getString("TitleProduct");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_single_chat);

        TextView mytext = (TextView) findViewById(R.id.singleChatUserName);
        mytext.setText(userName);

        mytext = (TextView) findViewById(R.id.singleChatTitleProduct);
        mytext.setText(titleProduct);

        ImageView image = (ImageView) findViewById(R.id.singleChatImage);
        image.setImageResource(R.drawable.image0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
