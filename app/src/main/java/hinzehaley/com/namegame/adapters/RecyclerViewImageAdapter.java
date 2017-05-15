package hinzehaley.com.namegame.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import hinzehaley.com.namegame.Constants;
import hinzehaley.com.namegame.R;
import hinzehaley.com.namegame.listeners.PersonClickedListener;
import models.profiles.Items;

/**
 * Created by haleyhinze on 5/14/17.
 * Adapter for recyclerView
 */

public class RecyclerViewImageAdapter extends RecyclerView.Adapter {

    private Items[] profiles = new Items[Constants.NUM_FACES];
    private Context context;
    PersonClickedListener listener;


    public RecyclerViewImageAdapter(Items[] profiles, Context context, PersonClickedListener listener){
        this.profiles = profiles;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Creates a new PersonViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_image, parent, false);
        PersonViewHolder viewHolder = new PersonViewHolder(v);
        return viewHolder;
    }

    /**
     * Binds a new PersonViewHolder. Sets all of the data for the holder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PersonViewHolder personViewHolder = (PersonViewHolder) holder;
        personViewHolder.showProgress();
        if(profiles[position] != null) {
            personViewHolder.setImage(profiles[position].getHeadshot().getUrl(), context);
            if (profiles[position].getHeadshot().getUrl() == null) {
                Log.e("IMAGE", "null image for: " + profiles[position].getId());
            }
            personViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.personClicked(profiles[position]);
                }
            });
        }
    }

    /**
     * Returns the number of items in the adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return profiles.length;
    }


    /**
     * ViewHolder that contains a cardView with a photo and a progressBar
     */
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView personPhoto;
        ProgressBar progressBar;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Sets the image using Picasso. Shows a loading bar while image loads. If unable to load image,
         * shows error icon
         * @param url
         * @param context
         */
        public void setImage(String url, Context context){
            String modUrl = "http:" + url;

            Picasso.with(context).load(modUrl).error(R.drawable.ic_error).fit().centerCrop().into(personPhoto, new Callback() {
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

        public void showProgress(){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets new profiles
     * @param newProfiles
     */
    public void updateProfiles(Items[] newProfiles){
        profiles = newProfiles;
        notifyDataSetChanged();
    }


}
