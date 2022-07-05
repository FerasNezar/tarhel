package com.almusand.aaber.ui.addComplaint;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.almusand.aaber.R;
import com.almusand.aaber.ui.base.BaseActivity;
import com.almusand.aaber.utils.Constants;
import com.almusand.aaber.utils.LocaleManager;
import com.almusand.aaber.utils.ProgressButton;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

import static com.almusand.aaber.utils.Constants.SELECT_IMAGE;

public class AddComplaintActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private RoundedImageView imgComplaint;
    private TextInputLayout lyNote;
    private TextInputEditText etNote;
    private TextView tvAddImage;

    private Uri imageUri;
    private String imagePath;
    private HashMap<String, File> mapFiles;
    private HashMap<String, String> map;
    private String conversationId;

    View view;
    private ProgressButton progressButton;
    private AddComplaintViewModel viewModel;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        viewModel = new ViewModelProvider(this).get(AddComplaintViewModel.class);

        conversationId = getIntent().getExtras().getString("conversationId");

        initialViews();


    }

    private void initialViews() {
        imgBack = findViewById(R.id.img_back);
        imgComplaint = findViewById(R.id.img_complaint);
        lyNote = findViewById(R.id.ly_note);
        etNote = findViewById(R.id.et_note);
        view = findViewById(R.id.bt_add);
        tvAddImage = findViewById(R.id.tv_add_image);

        progressButton = new ProgressButton(AddComplaintActivity.this, view);
        progressButton.setButtonTittle(AddComplaintActivity.this.getResources().getString(R.string.add_complaint));

        view.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        tvAddImage.setOnClickListener(this);

    }


    public void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture To Send"), Constants.SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                Glide.with(AddComplaintActivity.this).load(imageUri).into(imgComplaint);
                mapFiles = new HashMap<String, File>();
                imagePath = getActualPath(AddComplaintActivity.this, imageUri);

                Luban.compress(this, new File(imagePath))
                        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
                        .launch(new OnCompressListener() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                imageFile = file;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                mapFiles.put("file", imageFile);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_add:

                if (validInput()) {
                    addCompalint();
                }
                break;

            case R.id.tv_add_image:
                checkStoragePermission();
                break;

            case R.id.img_back:
                finish();
                break;
        }
    }

    private boolean validInput() {

        if (etNote.getText().toString().isEmpty()) {
            lyNote.setError(AddComplaintActivity.this.getResources().getText(R.string.msg_write_note));
            return false;
        }

        return true;
    }

    private void addCompalint() {
        view.setEnabled(false);
        progressButton.buttonActivated();
        map = new HashMap<>();
        map.put("conversation_id", conversationId);
        map.put("note", etNote.getText().toString());

        viewModel.addComplaint(AddComplaintActivity.this, map, mapFiles);
        viewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s.equals("done")) {
                    progressButton.buttonFinished();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    progressButton.buttonDactivated();
                }
            }
        });


    }


    private void checkStoragePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                OpenGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toasty.info(AddComplaintActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(AddComplaintActivity.this.getResources().getString(R.string.msg_add_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
}
