package software33.tagmatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainChatActivity extends AppCompatActivity {
    ListView list;
    CustomListChatAdapter adapter;
    public MainChatActivity CustomListView = null;
    public ArrayList<ListChatModel> CustomListViewValuesArr = new ArrayList<ListChatModel>();
    public ArrayList<ListChatModel> CustomListViewValuesArrSearch;
    boolean searching = false;

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

                /********* No query to do yet *********/

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CustomListViewValuesArrSearch = new ArrayList<ListChatModel>();
                for (ListChatModel listChatModel : CustomListViewValuesArr){
                    if (listChatModel.getUserName().toLowerCase().startsWith(newText.toLowerCase())) {
                        CustomListViewValuesArrSearch.add(listChatModel);
                    }
                }
                //Set the new list of chats
                Resources res =getResources();
                searching = !newText.isEmpty();

                if (searching)adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArrSearch,res );
                else adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );

                list.setAdapter( adapter );

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Set data in the array
    public void setListData()
    {

        for (int i = 0; i < 10; i++) {

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
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", tempValues.getUserName());
        b.putString("TitleProduct", tempValues.getTitleProduct());
        intent.putExtras(b);
        startActivity(intent);
    }

    public void onItemLongClick(int mPosition)
    {
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        AlertDialog alertDialog = createDeleteDialog(mPosition);
        alertDialog.show();
    }

    private void onPossitiveButtonClick(int mPosition) {
        Resources res =getResources();

        if (searching) {
            CustomListViewValuesArrSearch.remove(mPosition);
            adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArrSearch,res );
        }
        else {
            CustomListViewValuesArr.remove(mPosition);
            adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
        }
        list.setAdapter( adapter );
    }

    public AlertDialog createDeleteDialog(int position) {
        final int mPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_title_delete_chat)
                .setMessage(R.string.dialog_message_delete_chat)
                .setPositiveButton(R.string.dialog_confirm_delete_chat,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onPossitiveButtonClick(mPosition);
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel_delete_chat,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }

}
