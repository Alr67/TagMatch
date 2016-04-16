package software33.tagmatch.Chat;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public abstract class FirebaseUtils {

    private static Firebase myFirebaseRef = new Firebase("https://torrid-torch-42.firebaseio.com/");
    private static Firebase chatsRef = myFirebaseRef.child("chats");
    private static Firebase usersRef = myFirebaseRef.child("users");

    private String myId = "56d3f1d3-6b50-473a-9aa3-ff0007b3df29";

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public Firebase getChatsRef() {
        return chatsRef;
    }

    public static Firebase getUsersRef() {
        return usersRef;
    }

    public static class ChatInfo {
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
}
