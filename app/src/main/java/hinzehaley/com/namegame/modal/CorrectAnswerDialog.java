package hinzehaley.com.namegame.modal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import hinzehaley.com.namegame.R;
import models.PicassoLoader;

/**
 * Created by haleyhinze on 5/15/17.
 */

public class CorrectAnswerDialog extends DialogFragment {

   public static CorrectAnswerDialog dialog;
    public static String name;
    public static String url;

    public CorrectAnswerDialog(){

    }

    public static CorrectAnswerDialog getInstance(String name, String url){
        if(dialog == null){
            dialog = new CorrectAnswerDialog();
        }
        dialog.name = name;
        dialog.url = url;
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_correct, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        Dialog dialog = builder.create();
        ImageView imgImg = (ImageView) v.findViewById(R.id.img_correct_img);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        loadInImage(imgImg, progressBar);
        TextView txtName = (TextView) v.findViewById(R.id.txt_correct_name);
        txtName.setText(name);

        return dialog;
    }

    private void loadInImage(ImageView imgImg, final ProgressBar progressBar){
        if(url != null) {
            PicassoLoader.loadInImage(getContext(), imgImg, progressBar, url);
        }
    }


}
