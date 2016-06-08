package software33.tagmatch.Chat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.Offer;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class MainChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    //Identified by IdProduct, UserName and have the id of the chat and it's user
    public Map<Pair<String, String>, Pair<String, String>> idChatsUser = new HashMap<>();
    public Map<Pair<String, String>, String> imageChatsUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_chats);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        Helpers.setNavHeader(nav_header,getApplicationContext(),this);
        navigationView.addHeaderView(nav_header);

        setTitle(R.string.main_chat_activity_title);

        CustomListView = this;
        list = (ListView) findViewById(R.id.list_chats);
        Firebase.setAndroidContext(this);
        //Get Firebase Reference
        myFirebaseRef = FirebaseUtils.getMyFirebaseRef();

        chatsRef = myFirebaseRef.child("chats");
        usersRef = myFirebaseRef.child("users");

        //createUser("correu1@xd.com", "contra123");

        myId = FirebaseUtils.getMyId(this);

        //Accessing to the chats of my user

        /*Resources res =getResources();
        adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArr,res );
        list.setAdapter( adapter );*/

        getUserChats();

    }

    public void getUserChats(){
        Log.i("DebugChat","my id is: "+myId);
        if (myId != null) {
            FirebaseUtils.getUsersRef().child(myId).child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        Log.i("DebugChat","no chats");
                        Resources res = getResources();
                        adapter = new CustomListChatAdapter(CustomListView, myId, CustomListViewValuesArr, res);
                        list.setAdapter(adapter);
                    } else {
                        boolean noValidChats = true;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.getValue().equals(true)) {
                                getChat(dataSnapshot1.getKey());
                                Log.i("DebugChat","The chat is: "+dataSnapshot1.getValue().toString());
                                noValidChats = false;
                            }
                        }
                        if (noValidChats) {
                            Log.i("DebugChat","no valid chats");
                            Resources res = getResources();
                            adapter = new CustomListChatAdapter(CustomListView, myId, CustomListViewValuesArr, res);
                            list.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
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

                if (searching)adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArrSearch,res );
                else adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArr,res );

                list.setAdapter( adapter );

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.i("DEBUG","item selected");
        return NavigationController.onItemSelected(item.getItemId(),this);
    }

    //Set data in the array
    public void setListData(String idUser, String idProduct, String titleProduct, String owner, String userName, String idChat, String img,int messages, int newOffer){
        final ListChatModel sched = new ListChatModel();

        sched.setUserName(userName);
        sched.setOwner(owner);
        sched.setImage(img);
        sched.setIdProduct(idProduct);
        sched.setTitleProduct(titleProduct);
        sched.setNewOffer(newOffer);
        sched.setMessages(messages);

        boolean found = false;
        for (int i = 0; i < CustomListViewValuesArr.size(); i++)
        {
            if (CustomListViewValuesArr.get(i).getUserName().equals(userName) &&
                    CustomListViewValuesArr.get(i).getIdProduct().equals(idProduct)){
                CustomListViewValuesArr.set(i,sched);
                found = true;
            }
        }
        if (!found)CustomListViewValuesArr.add(sched);

        //Save the id
        idChatsUser.put(new Pair<String, String>(idProduct,userName),new Pair<String, String>(idChat,idUser));
        imageChatsUser.put(new Pair<String, String>(idProduct,userName), img);

        Resources res =getResources();
        adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
    }

    public void getChat(final String idChat) {
        //Accessing to the chat with idChat for every change
        final Context context = this;
        chatsRef.child(idChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FirebaseUtils.ChatInfo c = snapshot.child("info").getValue(FirebaseUtils.ChatInfo.class);
                int messages = 0;
                for (DataSnapshot dataSnapshot1 : snapshot.child("messages").getChildren()) {
                    FirebaseUtils.ChatText ct = dataSnapshot1.getValue(FirebaseUtils.ChatText.class);
                    if (!ct.getRead() && !ct.getSenderId().equals(myId)) {
                        ++messages;
                    }
                }
                //if (messages > 0) FirebaseUtils.displayNotification(context);

                String idUser = "";
                for (String s : c.getUsers().keySet()) {
                    if (!s.equals(myId)) {
                        idUser = s;
                    }
                }

                //1: Offer 2: Pending 3: Closed 4: Rated
                int newOffer = 0;
                if (snapshot.hasChild("offer")){
                    FirebaseUtils.ChatOffer o = snapshot.child("offer").getValue(FirebaseUtils.ChatOffer.class);
                    getOfferFromServer(o.getOfferId(), o.getValoration(), idUser, c.getIdProduct(), c.getTitleProduct(), c.getOwner(), idUser, idChat, messages);
                }
                else {
                    getUser(idUser, c.getIdProduct(), c.getTitleProduct(), c.getOwner(), idUser, idChat, messages, newOffer);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    public void getUser(final String idUser, final String idProduct, final String titleProduct, final String owner, final String userName, final String idChat, final int messages, final int newOffer) {
        usersRef.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FirebaseUtils.User u = snapshot.getValue(FirebaseUtils.User.class);
                setListData(idUser, idProduct, titleProduct, owner, userName, idChat, "", messages, newOffer);
                downloadImageFromServer(idUser, idProduct, titleProduct, owner, userName, idChat, messages, newOffer);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void onItemClick(int mPosition) {
        ListChatModel tempValues;
        if (searching) tempValues = ( ListChatModel ) CustomListViewValuesArrSearch.get(mPosition);
        else tempValues = ( ListChatModel ) CustomListViewValuesArr.get(mPosition);

        Pair<String,String> ids = idChatsUser.get(new Pair<String, String>(tempValues.getIdProduct(),tempValues.getUserName()));
        String img = imageChatsUser.get(new Pair<String, String>(tempValues.getIdProduct(),tempValues.getUserName()));

        /*Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", tempValues.getUserName());
        b.putString("TitleProduct", tempValues.getTitleProduct());
        b.putString("IdChat", ids.first);
        b.putString("IdUser", ids.second);
        b.putString("ImageChat", img);
        intent.putExtras(b);

        startActivity(intent);*/

        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", tempValues.getUserName());
        b.putString("IdProduct", tempValues.getIdProduct());
        b.putString("TitleProduct", tempValues.getTitleProduct());

        b.putString("IdChat", ids.first);
        b.putString("IdUser", ids.second);
        if (tempValues.getOwner().equals(myId)) b.putBoolean("isMyAdv", true);
        else b.putBoolean("isMyAdv", false);

        FirebaseUtils.setChatImage(img,this);

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
            adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArrSearch,res );
        }
        else {
            CustomListViewValuesArr.remove(mPosition);
            adapter=new CustomListChatAdapter( CustomListView, myId, CustomListViewValuesArr,res );
        }

        //Remove from Firebase putting the value to false
        Pair<String,String> ids = idChatsUser.get(new Pair<String, String>(tempValues.getIdProduct(),tempValues.getUserName()));
        Map<String, Object> value = new HashMap<>();
        value.put(ids.first,false);
        usersRef.child(myId).child("chats").updateChildren(value);

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

    private void getOfferFromServer(int offerId, final Map<String, Integer> valoration,final String idUser, final String idProduct, final String titleProduct,
                                    final String owner, final String userName, final String idChat, final int messages){
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url = Constants.IP_SERVER+"/offer/"+offerId;
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("offerId")){
                            Offer offer = Helpers.convertJSONToOffer(jsonObject);
                            int newOffer;
                            if (!offer.isAccepted()) {
                                if (offer.getDestinedUser().equals(myId)) newOffer = 1;
                                else newOffer = 2;
                            }
                            else {
                                if (!valoration.isEmpty()) {
                                    newOffer = 3;
                                }
                                else newOffer = 4;
                            }

                            getUser(idUser, idProduct, titleProduct, owner, userName, idChat, messages, newOffer);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadImageFromServer(final String idUser, final String idProduct, final String titleProduct,
                                    final String owner, final String userName, final String idChat, final int messages, final int newOffer){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + userName + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    setListData(idUser, idProduct, titleProduct, owner, userName, idChat, url, messages, newOffer);
                }
            }.execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //chatArrayAdapter.cleanup();
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

}
