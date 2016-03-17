package software33.tagmatch;

import android.content.res.Resources;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainChatActivity extends AppCompatActivity {
    ListView list;
    CustomListChatAdapter adapter;
    public MainChatActivity CustomListView = null;
    public ArrayList<ListChatModel> CustomListViewValuesArr = new ArrayList<ListChatModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setTitle(R.string.main_chat_activity_title);

        CustomListView = this;

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();

        Resources res =getResources();
        list= ( ListView )findViewById( R.id.list );

        /**************** Create Custom Adapter *********/
        adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_chat, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Set data in the array
    public void setListData()
    {

        for (int i = 0; i < 11; i++) {

            final ListChatModel sched = new ListChatModel();

            /******* Firstly take data in model object ******/
            sched.setUserName("Usuari "+i);
            sched.setImage("image"+0);
            sched.setTitleProduct("Producte "+i);

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add( sched );
        }

    }

    public void onItemClick(int mPosition)
    {
        ListChatModel tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);
        // SHOW ALERT
        Toast.makeText(CustomListView,
                ""+tempValues.getUserName()
                        +"Image:"+tempValues.getImage()
            +"Producte:"+tempValues.getTitleProduct(),
        Toast.LENGTH_LONG)
        .show();
    }

}
