package software33.tagmatch.Domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import software33.tagmatch.Advertisement.CustomPagerAdapter;

/**
 * Created by Cristina on 04/04/2016.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<CustomPagerAdapter> ActivityReference;

    public BitmapWorkerTask(CustomPagerAdapter parent) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        ActivityReference = new WeakReference<CustomPagerAdapter>(parent);

    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        String path = params[0];
        int wp, hp;
        hp = Integer.parseInt(params[1]);
        wp = Integer.parseInt(params[2]);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > hp || width > wp) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > hp && (halfWidth / inSampleSize) > wp) {
                inSampleSize *= 2;
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(path, options);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (ActivityReference != null && bitmap != null) {
            final CustomPagerAdapter parent = ActivityReference.get();
            if (parent != null) {
                parent.newImageConverted(bitmap);
            }
        }
    }
}
