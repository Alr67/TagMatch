package software33.tagmatch.Advertisement;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.R;

/**
 * Created by Cristina on 13/04/2016.
 */
public class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    Integer imageHeight, imageWidth;
    ViewGroup privateContainer;
    List<Bitmap> mResources;

    public CustomPagerAdapter(Integer height, Integer width ) {
        mResources = new ArrayList<>();
        imageHeight = height;
        imageWidth = width;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        privateContainer = container;
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = imageHeight;
        params.width = imageWidth;
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Log.v("DEBUG PagerAdapter","numImat: " + mResources.size() + ", posem pos: "+position);
        imageView.setImageResource(R.drawable.image_placeholder );
        imageView.setImageBitmap(mResources.get(position));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void addImage(String url) {
        BitmapWorkerTaskFromURL task = new BitmapWorkerTaskFromURL(this);
        task.execute(url,imageHeight.toString(),imageWidth.toString());
    }

    public void newImageConverted(Bitmap bitmap) {
        mResources.add(bitmap);
        this.notifyDataSetChanged();
        if (privateContainer != null)((ViewPager)privateContainer).setCurrentItem(mResources.size()-1);
     //   parent.newImageConverted(bitmap);
    }


}
