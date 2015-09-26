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
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        if (mThumbsUrl != null) {
            //Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            Log.d("IMAGE_ADAPTER", position + " === " + mThumbsUrl[position]);
            Picasso.Builder  picassoBuilder = new Picasso.Builder(mContext);

            picassoBuilder.indicatorsEnabled(true);
            Picasso picasso = picassoBuilder.build();

//????????Looks like this needs to be moved to a different thread from UI-thread otherwise crash:
// 09-13 10:38:13.731    3770-3770/com.end2end.popularmovies D/IMAGE_ADAPTER﹕ 7 === http://image.tmdb.org/t/p/w185/t99CkTVNCrIFC8FvzvD2UcGcbd8.jpg
// 09-13 10:32:04.611  29707-30988/com.end2end.popularmovies A / Looper﹕Could not create wake pipe.  errno=24
// 09-13 10:32:04.611  29707-30988/com.end2end.popularmovies A/libc﹕ Fatal signal 6 (SIGABRT) at 0x0000740b (code=-6), thread 30988 (Picasso-Dispatc)
            picasso
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
