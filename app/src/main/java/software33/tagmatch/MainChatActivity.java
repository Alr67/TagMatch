package software33.tagmatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainChatActivity extends AppCompatActivity {
    ListView list;
    CustomListChatAdapter adapter;
    public MainChatActivity CustomListView = null;
    public ArrayList<ListChatModel> CustomListViewValuesArr = new ArrayList<ListChatModel>();
    public ArrayList<ListChatModel> CustomListViewValuesArrSearch;
    boolean searching = false;
    boolean first = true;

    private Firebase myFirebaseRef;
    private Firebase chatsRef;
    private Firebase usersRef;

    private ChildEventListener mListener;

    //Identified by IdProduct, UserName
    public Map<Pair<String, String>, String> idChats = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setTitle(R.string.main_chat_activity_title);

        CustomListView = this;
        list= ( ListView )findViewById( R.id.list );

        //Get Firebase Reference
        myFirebaseRef =
                new Firebase("https://torrid-torch-42.firebaseio.com/");

        chatsRef = myFirebaseRef.child("chats");
        usersRef = myFirebaseRef.child("users");

        createChat();

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        //Update Data
        mListener = this.chatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ChatInfo c = dataSnapshot.child("info").getValue(ChatInfo.class);
                setListData(c.getIdProduct(), c.getUsers(), dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_chat, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.hint_single_chat));
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

    private static class ChatInfo {
        String idProduct;
        Map<String, Object> users = new HashMap<>();

        public ChatInfo() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }

        public ChatInfo(String idProduct, Map<String, Object> users) {
            this.idProduct = idProduct;
            this.users = users;
        }

        public String getIdProduct() {
            return idProduct;
        }

        public Map<String, Object> getUsers(){
            return users;
        }
    }

    private static class User {
        String alias;
        String img;
        Map<String, Object> blockeds = new HashMap<>();
        Map<String, Object> chats = new HashMap<>();
        public User() {}
        public User(String alias, String img, Map<String, Object> blockeds, Map<String, Object> chats) {
            this.alias = alias;
            this.img = img;
            this.blockeds = blockeds;
            this.chats = chats;
        }
        public String getAlias() {
            return alias;
        }
        public String getImg() {
            return img;
        }
        public Map<String, Object> getBloqueados(){
            return blockeds;
        }
        public Map<String, Object> getChats(){
            return chats;
        }
    }

    public String createUser(String name) {
        Firebase id = usersRef.push();
        Map<String, Object> blockeds = new HashMap<>();
        //blockeds.put("user","xds");
        Map<String, Object> chats = new HashMap<>();
        //chats.put("chat","chat1");
        User user = new User(name,"",blockeds,chats);
        id.setValue(user);
        return id.getKey();
    }

    public String createChat() {
        Firebase id = chatsRef.push();
        String id1 = createUser("My User Name");
        String id2 = createUser("Usuari0");

        Map<String, Object> users = new HashMap<>();
        users.put(id1, "My User Name");
        users.put(id2, "Usuari0");

        ChatInfo chatInfo = new ChatInfo("Anuncio Test", users);
        id.child("info").setValue(chatInfo);
        return id.getKey();
    }

    //Set data in the array
    public void setListData(String idProduct, Map<String, Object> users, String id) {
        if (users.containsValue("My User Name")) {
            String userName = "";
            for (Object o : users.values()){
                if (!o.toString().equals("My User Name"))
                    userName = o.toString();
            }
            final ListChatModel sched = new ListChatModel();

            sched.setUserName(userName);
            //TODO: get image
            sched.setImage("image" + 0);
            sched.setTitleProduct(idProduct);

            CustomListViewValuesArr.add(sched);

            //Save the id
            idChats.put(new Pair<String, String>(idProduct,userName),id);

            /**************** Create Custom Adapter *********/
            Resources res =getResources();
            adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
            list.setAdapter( adapter );
        }
    }

    public void onItemClick(int mPosition) {
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        String id = idChats.get(new Pair<String, String>(tempValues.getTitleProduct(),tempValues.getUserName()));

        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", tempValues.getUserName());
        b.putString("TitleProduct", tempValues.getTitleProduct());
        b.putString("IdChat", id);
        intent.putExtras(b);

        startActivity(intent);
    }

    public void onItemLongClick(int mPosition) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //chatArrayAdapter.cleanup();
    }
}
