package hinzehaley.com.namegame.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import hinzehaley.com.namegame.NameGameActivity;
import hinzehaley.com.namegame.R;
import hinzehaley.com.namegame.listeners.DialogButtonClickListener;

/**
 * Created by haleyhinze on 5/14/17.
 */

public class DialogManager {

    public static ProgressDialog mProgressDialog;

    public static void showAreYouSureYouWantToRestartDialog(Context context, final DialogButtonClickListener listener){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.restart));
        alertDialog.setMessage(context.getString(R.string.are_you_sure_you_want_to_restart));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.restart),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.buttonClicked();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * Shows a dialog telling the user to connect to a network
     */
    public static void showNetworkNotConnectedDialog(Context context){
        showBasicDialog(context.getString(R.string.not_connected), context.getString(R.string.network), context);
    }

    private static void showBasicDialog(String message, String title, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void showGameOverDialog(String message, Context context, final DialogButtonClickListener listener){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.game_over));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.restart),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.buttonClicked();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    /**
     * Shows a dialog telling the user there was an error retrieving the profiles
     * @param errorMessage
     */
    public static void showProfileErrorDialog(String errorMessage, Context context){
        showBasicDialog(context.getString(R.string.error_getting_profiles) + errorMessage, context.getString(R.string.error), context);
    }

    /**
     * Shows a progress dialog to display while retrieving profiles
     */
    public static void showProgressDialog(Context context){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
            mProgressDialog.setMessage(context.getString(R.string.get_profiles));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    /**
     * Hides progress dialog
     */
    public static void hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}


