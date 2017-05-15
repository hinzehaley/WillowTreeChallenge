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

import hinzehaley.com.namegame.R;
import models.PicassoLoader;

/**
 * Created by haleyhinze on 5/15/17.
 * DialogFragment to display face and name of correct answer
 */

public class CorrectAnswerDialog extends DialogFragment {

   public static CorrectAnswerDialog dialog;
    public static String name;
    public static String url;

    /**
     * Required empty public constructor
     */
    public CorrectAnswerDialog(){
    }

    /**
     * Sets fields and returns instance
     * @param name
     * @param url
     * @return
     */
    public static CorrectAnswerDialog getInstance(String name, String url){
        if(dialog == null){
            dialog = new CorrectAnswerDialog();
        }
        dialog.name = name;
        dialog.url = url;
        return dialog;
    }


    /**
     * Builds new AlertDialog with custom view. Loads in image and sets name
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_correct, null);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
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

    /**
     * Loads image using Picasso
     * @param imgImg
     * @param progressBar
     */
    private void loadInImage(ImageView imgImg, final ProgressBar progressBar){
        if(url != null) {
            PicassoLoader.loadInImage(getContext(), imgImg, progressBar, url);
        }
    }


}
