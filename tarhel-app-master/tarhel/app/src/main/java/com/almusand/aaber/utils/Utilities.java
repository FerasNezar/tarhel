package com.almusand.aaber.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.ui.main.MainViewModel;
import com.almusand.aaber.ui.register.RegisterActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    public static String getCurrentDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime;
    }


    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static String convertFromTimeStamp(String time) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

//    public static void ShowSnacker(String tittle,String msg, Activity context) {
//
//        Sneaker.with(context) // Activity, Fragment or ViewGroup
//                .setTitle(tittle, R.color.colorPrimary) // Title and title color
//                .setMessage(msg, R.color.colorPrimary) // Message and message color
//                .setDuration(4000) // Time duration to show
//                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
//                .autoHide(true) // Auto hide Sneaker view
//                .setTypeface(ResourcesCompat.getFont(context, R.font.tajawal_medium)) // Custom font for title and message
//                .setCornerRadius(8,8) // Radius and margin for round corner Sneaker. - Version 1.0.2
//                .sneakSuccess();
//
//    }

    public static void showSnackbar(String msg, Context context, View view) {
        // Make and display Snackbar
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.tajawal_medium);

        // Set action text color
        snackbar.setActionTextColor(
                ContextCompat.getColor(context, R.color.white)
        );
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        // set no of text line
        textView.setMaxLines(2);
        //set text color
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        textView.setTypeface(typeface);
        //set text size
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        //Set Snackbar background color
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }

    public static void showAlertDialog(final Context context, String message, String title, final boolean fromCheckout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
//                        if (fromCheckout) {
//                            Intent i = new Intent(context.getApplicationContext(), MainFragment.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            context.startActivity(i);
//                            ((Activity) context).finish();
//                        }
                        dialog.cancel();
                    }
                });
        if (title != null)
            builder.setTitle(title);

        AlertDialog alert11 = builder.create();
        if (fromCheckout) {
            alert11.setCancelable(false);
            alert11.setCanceledOnTouchOutside(false);
        }
        alert11.show();
    }

    public static void showLogoutAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(context.getString((R.string.logout)))
                .setMessage(context.getResources().getText(R.string.msg_logout))
                .setPositiveButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppPreferences.logout(context);
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public static void showLogin(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(context.getString(R.string.login))
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        AppPreferences.logout(context);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public static String decodeFile(Context context,String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 100, 100, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 100, 100, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
            File mFolder = new File(extr + "/myTmpDir");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }


}
