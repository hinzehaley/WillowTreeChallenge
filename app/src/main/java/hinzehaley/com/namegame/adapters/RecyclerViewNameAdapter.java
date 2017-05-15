package hinzehaley.com.namegame.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import hinzehaley.com.namegame.Constants;
import hinzehaley.com.namegame.R;
import hinzehaley.com.namegame.listeners.PersonClickedListener;
import models.profiles.Items;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class RecyclerViewNameAdapter extends RecyclerView.Adapter{

    private Items[] profiles = new Items[Constants.NUM_FACES];
    PersonClickedListener listener;


    public RecyclerViewNameAdapter(Items[] profiles, PersonClickedListener listener){
        this.profiles = profiles;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_name, parent, false);
        RecyclerViewNameAdapter.PersonViewHolder viewHolder = new RecyclerViewNameAdapter.PersonViewHolder(v);
        return viewHolder;
    }

    /**
     * Binds a new PersonViewHolder. Sets all of the data for the holder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecyclerViewNameAdapter.PersonViewHolder personViewHolder = (RecyclerViewNameAdapter.PersonViewHolder) holder;
        personViewHolder.setName(profiles[position].getFirstName() + " " + profiles[position].getLastName());
        if(profiles[position] != null) {
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
        TextView txtName;


        PersonViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
        }

        public void setName(String name){
            txtName.setText(name);
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
