package software33.tagmatch.Chat;

public class ListChatModel {
    private  String UserName="";
    private  String Image="";
    private  String TitleProduct="";

    public void setUserName(String CompanyName)
    {
        this.UserName = CompanyName;
    }

    public void setImage(String Image)
    {
        this.Image = Image;
    }

    public void setTitleProduct(String Url)
    {
        this.TitleProduct = Url;
    }

    public String getUserName()
    {
        return this.UserName;
    }

    public String getImage()
    {
        return this.Image;
    }

    public String getTitleProduct()
    {
        return this.TitleProduct;
    }
}
