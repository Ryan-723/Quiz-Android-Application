package com.example.group2_final_project.helpers;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetAlert {
    Context context;
    SweetAlertDialog sweetAlertDialog;

    public  SweetAlert(Context context) {
        this.context = context;
    }

    public void showSuccessAlert(String title, String content, String buttonText) {
       sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(buttonText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
       sweetAlertDialog.show();
    }

    // Method to display an error alert dialog
    public void showErrorAlert(String title, String content, String buttonText) {
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(buttonText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        sweetAlertDialog.show();
    }

    // Method to show a loader with a  text
    public void  showLoader(String text) {
        sweetAlertDialog= new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setContentText(text);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    // Method to hide the loader
    public void hideLoader() {
        sweetAlertDialog.hide();
    }
}
