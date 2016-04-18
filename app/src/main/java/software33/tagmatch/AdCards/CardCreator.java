package software33.tagmatch.AdCards;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by rafa on 15/04/2016.
 */
public class CardCreator {


   /* public AdvertContent createCard(Context context, String name, String path, String option, Double price, Integer id) {
        File f = new File(context.getCacheDir()+"/"+path); // Això haruà de ser algo de bitmaps?? Será diferent amb Heroku
        if (!f.exists()) try {

            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }
        return new AdvertContent(name,f.getPath(),option,price, id);
    } */

    public AdvertContent createCard(Context context, String name, String path, String option) {
        File f = new File(context.getCacheDir()+"/"+path); // Això haruà de ser algo de bitmaps?? Será diferent amb Heroku
        if (!f.exists()) try {

            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        return new AdvertContent(name,f.getPath(),option);
    }
}
