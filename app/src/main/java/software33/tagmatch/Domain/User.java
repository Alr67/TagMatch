package software33.tagmatch.Domain;

import android.media.Image;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Cristina on 03/04/2016.
 */
public class User {

    private String UID;
    private String userPhotoId;
    private String city;
    private String alias;
    private String descripcion;
    private String password;
    private Image imgPerfil;
    private String mail;
    private Integer valoration;
    private LatLng coord;
    ///Listado de usuarios que tienen bloqueado al usuario
    private List<String> usuariosBloqueados = new ArrayList<>();
    ///Listado de todos los ids de chats que tiene el usuario
    private List<String> chats = new ArrayList<>();
    ///Listado de los ids de todos los anuncios que tiene el usuario
    private List<String> listadoIDAnuncios = new ArrayList<>();

    public User(String ownerName, String userPhotoId, String city) {
        this.UID = "";
        this.alias = ownerName;
        //  this.imgPerfil = new Image();
        this.descripcion = "";
        this.password = "";
        this.valoration = 5;
        this.mail = "";
        this.userPhotoId = userPhotoId;
        this.city = city;
    }

    public String getUID() {
        return UID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPassword() {
        return password;
    }

    public Image getImgPerfil() {
        return imgPerfil;
    }

    public String getMail() {
        return mail;
    }

    public Integer getValoration() {
        return valoration;
    }

    public User(){
        this.UID = "";
        this.alias = "";
      //  this.imgPerfil = new Image();
        this.descripcion = "";
        this.password = "";
        this.valoration = 5;
        this.mail = "";
        this.userPhotoId = "";
        this.city = "";
    }

    public User(String username){
        this.UID = "";
        this.alias = username;
       // this.imgPerfil = UIImage()
        this.descripcion = "";
        this.password = "";
        this.valoration = 5;
        this.mail = "";
        this.userPhotoId = "";
        this.city = "";
    }

    public User(String username,String password){
        this.UID = "";
        this.alias = username;
     //   this.imgPerfil = UIImage();
        this.descripcion = "";
        this.password = password;
        this.valoration = 5;
        this.mail = "";
        this.userPhotoId = "";
        this.city = "";
    }

    public User(String uid, String alias, Image imgPerfil, String desc, String password,String mail){
        this.UID = uid;
        this.alias = alias;
        this.imgPerfil = imgPerfil;
        this.descripcion = desc;
        this.password = password;
        this.mail = mail;
        this.valoration = 5;
        this.userPhotoId = "";
        this.city = "";
    }

    ///Función que inidica si el usuario idUser permite abrir un chat con el usuario actual. True no lo tiene bloqueado
    public Boolean allowedToChat(String idUser) {
        return usuariosBloqueados.contains(idUser);
    }

    public void  newValoration(Integer newVal){
        //check its between limits
        valoration += newVal;
    }

    ///Permite añadir un id de usuario que lo haya bloqueado
    public void addUserBlocked(String idUser){
            usuariosBloqueados.add(idUser);
    }

    ///Obtiene todos los usuarios que han bloqueado al usuario
    public List<String> getUsersBlocked(){
        return usuariosBloqueados;
    }

    public String getAlias() {
        Log.i(Constants.DebugTAG,"User getAlias called, username: "+alias);
        return alias;
    }

    public LatLng getCoord(){return coord;}

    public void setCoord(LatLng coords) {coord = coords;}
}
