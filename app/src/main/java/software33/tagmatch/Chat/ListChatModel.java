package software33.tagmatch.Chat;

public class ListChatModel {
    private  String UserName="";
    private  String Image="";
    private  String TitleProduct="";
    private String idProduct="";
    private String Owner="";
    private int messages=0;
    private int NewOffer=0;

    public void setUserName(String CompanyName)
    {
        this.UserName = CompanyName;
    }

    public void setOwner(String owner)
    {
        this.Owner = owner;
    }

    public void setImage(String Image)
    {
        this.Image = Image;
    }

    public void setTitleProduct(String title)
    {
        this.TitleProduct = title;
    }

    public void setIdProduct(String id)
    {
        this.idProduct = id;
    }

    public void setNewOffer(int offer) { this.NewOffer = offer; }

    public void setMessages(int messages) { this.messages = messages; }

    public String getUserName()
    {
        return this.UserName;
    }

    public String getOwner()
    {
        return this.Owner;
    }

    public String getImage()
    {
        return this.Image;
    }

    public String getTitleProduct()
    {
        return this.TitleProduct;
    }

    public String getIdProduct()
    {
        return idProduct;
    }

    public int getNewOffer() {return this.NewOffer; }

    public int getMessages() {return this.messages; }
}
