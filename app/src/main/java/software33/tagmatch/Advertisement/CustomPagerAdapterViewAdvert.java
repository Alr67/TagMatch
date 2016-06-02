package software33.tagmatch.Advertisement;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;

/**
 * Created by Cristina on 04/04/2016.
 */
public class CustomPagerAdapterViewAdvert extends CustomPagerAdapter {

    private ViewAdvert parent;

    public CustomPagerAdapterViewAdvert(ViewAdvert father, Integer height, Integer width) {
        super(height, width);
        mContext = father.getApplicationContext();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent = father;
    }

    public void addImage(Bitmap image) {
        super.newImageConverted(image);

    }


 /*   public void newImageConverted(Bitmap bitmap) {
        mResources.add(bitmap);
        this.notifyDataSetChanged();
        ((ViewPager)privateContainer).setCurrentItem(mResources.size()-1);
        parent.newImageConverted(bitmap);
    } */
}
