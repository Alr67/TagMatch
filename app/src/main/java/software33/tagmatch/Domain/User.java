package software33.tagmatch.Domain;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
public class User {

    String UID;
    String alias;
    String descripcion;
    String password;
    Image imgPerfil;
    String mail;
    Integer valoration;
    ///Listado de usuarios que tienen bloqueado al usuario
    private List<String> usuariosBloqueados = new ArrayList<>();
    ///Listado de todos los ids de chats que tiene el usuario
    private List<String> chats = new ArrayList<>();
    ///Listado de los ids de todos los anuncios que tiene el usuario
    private List<String> listadoIDAnuncios = new ArrayList<>();


    public User(){
        this.UID = "";
        this.alias = "";
      //  this.imgPerfil = new Image();
        this.descripcion = "";
        this.password = "";
        this.valoration = 5;
        this.mail = "";
    }

    public User(String username){
        this.UID = "";
        this.alias = username;
       // this.imgPerfil = UIImage()
        this.descripcion = "";
        this.password = "";
        this.valoration = 5;
        this.mail = "";
    }

    public User(String username,String password){
        this.UID = "";
        this.alias = username;
     //   this.imgPerfil = UIImage();
        this.descripcion = "";
        this.password = password;
        this.valoration = 5;
        this.mail = "";
    }

    public User(String uid, String alias, Image imgPerfil, String desc, String password,String mail){
        this.UID = uid;
        this.alias = alias;
        this.imgPerfil = imgPerfil;
        this.descripcion = desc;
        this.password = password;
        this.mail = mail;
        this.valoration = 0;
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
}
