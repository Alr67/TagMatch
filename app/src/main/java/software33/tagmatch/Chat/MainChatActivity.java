package software33.tagmatch.Chat;

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
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;

public class MainChatActivity extends AppCompatActivity {
    ListView list;
    CustomListChatAdapter adapter;
    public MainChatActivity CustomListView = null;
    public ArrayList<ListChatModel> CustomListViewValuesArr = new ArrayList<ListChatModel>();
    public ArrayList<ListChatModel> CustomListViewValuesArrSearch;
    boolean searching = false;

    private Firebase myFirebaseRef;
    private Firebase chatsRef;
    private Firebase usersRef;

    private String myId;

    private ChildEventListener mListener;

    //Identified by IdProduct, UserName and have the id of the chat and it's user
    public Map<Pair<String, String>, Pair<String, String>> idChatsUser = new HashMap<>();

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

        //createUser("correu1@xd.com", "contra123");
        myId = "56d3f1d3-6b50-473a-9aa3-ff0007b3df29";

        //Accessing to the chats of my user
        mListener = this.usersRef.child(myId).child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                getChat(dataSnapshot.getKey());
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
        Resources res =getResources();
        adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
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

    String uid1;
    String uid2;

    boolean first = true;
    boolean second = false;
    private void setUID(String s){
        if (first) {
            uid1 = s;
            usersRef.child(uid1).setValue(new User("My User Name","",new HashMap<String, Object>(),new HashMap<String, Object>()));
            createUser("correu2@xd.com", "contra123");
            first = false;
            second = true;
        }
        else if (second) {
            uid2 = s;
            usersRef.child(uid2).setValue(new User("Usuari0","",new HashMap<String, Object>(),new HashMap<String, Object>()));
            createChat();
        }
    }

    public void createUser(final String email, final String password) {
        myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                myFirebaseRef.authWithPassword(
                        email,
                        password,
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                /*usersRef.child(authData.getUid()).setValue
                                        (new User(name,"",new HashMap<String, Object>(),new HashMap<String, Object>()));*/
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError error) {
                                // Should hopefully not happen as we just created the user.
                            }
                        }
                );
                setUID(result.get("uid").toString());
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"error Creating",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String createChat() {
        Firebase id = chatsRef.push();

        String id1 = uid1;
        String id2 = uid2;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, "My User Name");
        users.put(id2, "Usuari0");

        ChatInfo chatInfo = new ChatInfo("Anuncio Test", users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user
        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),"");
        usersRef.child(id1).child("chats").setValue(chats1);

        /*
        Map<String, Object> chats2 = new HashMap<>();
        chats2.put(id.getKey(),"");
        usersRef.child(id2).child("chats").setValue(chats2);
        */

        return id.getKey();
    }

    //Set data in the array
    public void setListData(String idProduct, Map<String, Object> users, String idChat) {
        String userName = "";
        for (Object o : users.values()){
            if (!o.toString().equals("My User Name")) {
                userName = o.toString();
            }
        }
        String idUser = "";
        for (String s : users.keySet()){
            if (!s.equals(myId)) {
                idUser = s;
            }
        }
        final ListChatModel sched = new ListChatModel();

        sched.setUserName(userName);
        //TODO: get image
        sched.setImage("image" + 0);
        sched.setTitleProduct(idProduct);

        CustomListViewValuesArr.add(sched);

        //Save the id
        idChatsUser.put(new Pair<String, String>(idProduct,userName),new Pair<String, String>(idChat,idUser));

        Resources res =getResources();
        adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
    }

    public void getChat(final String idChat) {
        //Accessing to the chat with idChat ONCE
        chatsRef.child(idChat).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ChatInfo c = snapshot.getValue(ChatInfo.class);
                setListData(c.getIdProduct(), c.getUsers(), idChat);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    public void onItemClick(int mPosition) {
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        Pair<String,String> ids = idChatsUser.get(new Pair<String, String>(tempValues.getTitleProduct(),tempValues.getUserName()));

        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", tempValues.getUserName());
        b.putString("TitleProduct", tempValues.getTitleProduct());
        b.putString("IdChat", ids.first);
        b.putString("IdUser", ids.second);
        intent.putExtras(b);

        startActivity(intent);
    }

    public void onItemLongClick(int mPosition) {
        AlertDialog alertDialog = createDeleteDialog(mPosition);
        alertDialog.show();
    }

    private void onPossitiveButtonClick(int mPosition) {
        Resources res =getResources();
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        //Remove from the list
        if (searching) {
            //Remove first from the non-search array
            ListChatModel lToRemove = new ListChatModel();
            for (ListChatModel l : CustomListViewValuesArr){
                if (l.equals(CustomListViewValuesArrSearch.get(mPosition)))
                    lToRemove = l;
            }
            CustomListViewValuesArrSearch.remove(mPosition);
            CustomListViewValuesArr.remove(lToRemove);
            adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArrSearch,res );
        }
        else {
            CustomListViewValuesArr.remove(mPosition);
            adapter=new CustomListChatAdapter( CustomListView, CustomListViewValuesArr,res );
        }

        //Remove from Firebase
        Pair<String,String> ids = idChatsUser.get(new Pair<String, String>(tempValues.getTitleProduct(),tempValues.getUserName()));
        usersRef.child(myId).child("chats").child(ids.first).setValue(new HashMap<String, Object>());

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
