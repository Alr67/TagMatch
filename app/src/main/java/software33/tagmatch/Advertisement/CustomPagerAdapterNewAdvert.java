package software33.tagmatch.Advertisement;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;

/**
 * Created by Cristina on 04/04/2016.
 */
public class CustomPagerAdapterNewAdvert extends CustomPagerAdapter {

    private NewAdvertisement parent;

    public CustomPagerAdapterNewAdvert(NewAdvertisement father, Integer height, Integer width) {
        super(height,width);
        mContext = father.getApplicationContext();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent = father;
    }

    public void addImage(String url) {
        BitmapWorkerTaskFromURL task = new BitmapWorkerTaskFromURL(this);

        task.execute(url,imageHeight.toString(),imageWidth.toString());
    }

    public void addImageBitmap(Bitmap image) {
        super.newImageConverted(image);
    }


    public void newImageConverted(Bitmap bitmap) {
        super.newImageConverted(bitmap);
        parent.newImageConverted(bitmap);
    }
}
