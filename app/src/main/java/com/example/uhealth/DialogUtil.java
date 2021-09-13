package com.example.uhealth;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


public class DialogUtil {

    public static Dialog showSelectDialog(Context context, String title, String pContent, String pLeftBtnStr,
                                          String pRightBtnStr,
                                          final DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(pContent)
                .setPositiveButton(pRightBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.confirm();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(pLeftBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.cancel();
                        dialog.dismiss();
//                        return;
                    }
                })
                .create();
        return dialog;
    }


    public interface DialogClickListener {

        public abstract void confirm();

        public abstract void cancel();

    }
}