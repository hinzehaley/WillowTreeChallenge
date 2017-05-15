package models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import hinzehaley.com.namegame.R;

/**
 * Created by haleyhinze on 5/15/17.
 * Class to load an image into an ImageView using picasso. Shows a ProgressBar while image is loading.
 */

public class PicassoLoader {

    /**
     * Loads image at url into imageView. While image is loading, shows progressBar.
     * @param context
     * @param imageView
     * @param progressBar
     * @param url
     */
    public static void loadInImage(Context context, ImageView imageView, final ProgressBar progressBar, String url){
        String modUrl = "http:" + url;
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(modUrl).error(R.drawable.ic_error).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
