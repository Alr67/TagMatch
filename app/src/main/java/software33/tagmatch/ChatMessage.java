package software33.tagmatch;

public class ChatMessage {
    public boolean left;
    public String message;
    public String userName;

    public ChatMessage(boolean left, String userName, String message) {
        super();
        this.left = left;
        this.message = message;
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public String getMessage(){
        return message;
    }
}
