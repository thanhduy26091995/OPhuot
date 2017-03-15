package com.thanhduy.ophuot.manage_homestay;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.ImageLoader;

import java.util.List;

/**
 * Created by tantr on 2/15/2017.
 */

public class AdapterViewPager extends PagerAdapter {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private List<String> list;

    public AdapterViewPager(Context c, List<String> list) {
        ctx = c;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.activity_custom_swip, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.swip_image_view);

        if (list.size() > 0) {
//            Picasso.with(ctx).load(list.get(position))
//                    .fit()
//                    .placeholder(R.drawable.no_image)
//                    .error(R.drawable.no_image)
//                    .into(imageView);
            ImageLoader.getInstance().loadImageOther((Activity) ctx, list.get(position), imageView);
        } else if (list.size() < 0) {
            imageView.setImageResource(R.drawable.no_image);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == object);
    }
}
