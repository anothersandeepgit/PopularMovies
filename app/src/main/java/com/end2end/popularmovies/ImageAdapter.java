/**
 * Created by lendevsanadmin on 8/21/2015.
 */

package com.end2end.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by lendevsanadmin on 8/21/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    Integer[] mThumbsIds;
    String[] mThumbsUrl;

    public ImageAdapter(Context c, Integer[] thumbsIds) {
        mContext = c;
        mThumbsIds = thumbsIds;
    }
    public ImageAdapter(Context c, String[] thumbsUrls) {
        mContext = c;
        mThumbsUrl = thumbsUrls;
    }
    public int getCount() {
        return (mThumbsIds != null) ? mThumbsIds.length : mThumbsUrl.length;
    }
    public Object getItem(int position){
        return null;
    }
    public long getItemId(int position){
        return 0;
    }
    //create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if (convertView == null){
            //if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        if (mThumbsUrl != null) {
            //Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            Log.d("IMAGE_ADAPTER", position + " === " + mThumbsUrl[position]);
            Picasso.with(mContext)
                    .load(mThumbsUrl[position])
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.movie_poster_unavailable)
                    .into(imageView);
        }else {
            imageView.setImageResource(R.drawable.loading);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setTranslationX(40);
            imageView.setTranslationY(40);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(20, 20, 20, 20);
        }
        return imageView;
    }


}
