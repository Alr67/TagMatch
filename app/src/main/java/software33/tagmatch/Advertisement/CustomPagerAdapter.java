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
import software33.tagmatch.Utils.BitmapWorkerTask;

/**
 * Created by Cristina on 04/04/2016.
 */
public class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Bitmap> mResources;
    private Integer imageHeight, imageWidth;
    private ViewGroup privateContainer;
    private NewAdvertisement parent;
 //   List<String> mURLResources;

    public CustomPagerAdapter( NewAdvertisement father, Integer height, Integer width) {
        mContext = father.getApplicationContext();
        mResources = new ArrayList<>();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageHeight = height;
        this.imageWidth = width;
        parent = father;
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
        BitmapWorkerTask task = new BitmapWorkerTask(this);

        task.execute(url,imageHeight.toString(),imageWidth.toString());
    }


    public void newImageConverted(Bitmap bitmap) {
        mResources.add(bitmap);
        this.notifyDataSetChanged();
        ((ViewPager)privateContainer).setCurrentItem(mResources.size()-1);
        parent.newImageConverted(bitmap);
    }
}
