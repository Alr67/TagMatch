package software33.tagmatch.AdCards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class BitmapWorkerTaskFromBitmap extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<AdapterAdvert> adapterAdvert;

    public BitmapWorkerTaskFromBitmap(AdapterAdvert parent) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        adapterAdvert = new WeakReference<AdapterAdvert>(parent);
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
        if (adapterAdvert != null && bitmap != null) {
            final AdapterAdvert imageView = adapterAdvert.get();
           // if (imageView != null) {imageView.newImageConverted(bitmap);
     //       }
        }
    }
}