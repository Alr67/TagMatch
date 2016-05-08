package software33.tagmatch.Chat;

public class ChatMessage {
    public boolean left;
    public String message;
    public String userName;
    public boolean read;

    public ChatMessage(boolean left, String userName, String message, boolean read) {
        super();
        this.left = left;
        this.message = message;
        this.userName = userName;
        this.read = read;
    }

    public String getUserName(){
        return userName;
    }

    public String getMessage(){
        return message;
    }

    public boolean getRead() { return read; }
}
