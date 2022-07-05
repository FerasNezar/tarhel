package com.almusand.aaber.ui.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.almusand.aaber.utils.LocaleManager;
import com.apkfuns.xprogressdialog.XProgressDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BaseActivity extends AppCompatActivity {


    private static String lang;
    private boolean isDestroyed;
    //Dialog window shows when API calls eg. login, registering etc
    private Dialog mProgressDialog;

    public static XProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        progressDialog = new XProgressDialog(this);

    }

    public static void showProgressDialog(String s) {
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        progressDialog.hide();
    }

    public static String ConvertTimeStampToDate(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateTime = new Date(Long.valueOf(time) * 1000);
        return dateFormat.format(dateTime);
    }

    public static long getCurrentTimeStamp() {
        return (System.currentTimeMillis() / 1000);
    }

    public void switchActivity(Class<?> destinationActivity, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, destinationActivity);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }


    public void switchActivity(Class<?> destinationActivity, int requestCode) {
        Intent intent = new Intent(this, destinationActivity);
        startActivityForResult(intent, requestCode);
    }


    public void backActivityWithResultOk() {
        backActivityWithResultOk(null);
    }


    public void backActivityWithResultOk(Bundle bundle) {
        if (bundle != null) {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        } else
            setResult(RESULT_OK);
        finish();
    }

    /**
     * Method used to display short duration toast
     *
     * @param message message to be displayed
     */
    public void displayToast(String message) {
        if (TextUtils.isEmpty(message) || message.equalsIgnoreCase("null"))
            return;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method used to display short duration toast
     *
     * @param resId resource id of the message string to be displayed
     */
    public void displayToast(int resId) {
        displayToast(getString(resId));
    }

    /**
     * extract text written on a text view
     *
     * @param viewId
     * @return
     */
    public String getTextForView(int viewId) {
        TextView view = (TextView) findViewById(viewId);
        return view.getText().toString().trim();
    }

    /**
     * set text on a text view
     *
     * @param viewId
     * @param text
     * @return
     */
    public void setTextOnView(int viewId, String text) {
        TextView view = (TextView) findViewById(viewId);
        view.setText(text);


    }

    public static boolean isValid(String s) {
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public void makeCall(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("error", e.getMessage());
        }

    }

    public void sendEmail(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + "//AppConstants.FAWRUN_EMAIL"));
        context.startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    public void goToUrl(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url)));
    }


    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    public static String getDistance(Double startLat, Double startLng, float endLat, float endLng) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(startLat);
        startPoint.setLongitude(startLng);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(endLat);
        endPoint.setLongitude(endLng);

        double distance = startPoint.distanceTo(endPoint) / 1000;

        String txtDistance = String.format(Locale.US, "%.2f", distance);
        return txtDistance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getActualPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getRootDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }


    public static void requestAllPermissions(Activity context) {
        TedPermission.with(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO)
                .check();
    }

}
