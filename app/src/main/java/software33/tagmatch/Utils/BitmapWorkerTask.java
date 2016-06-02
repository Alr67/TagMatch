package software33.tagmatch.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public BitmapWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);

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
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}