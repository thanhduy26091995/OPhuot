package com.thanhduy.ophuot.chat.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.chat.ChatAdapter;
import com.thanhduy.ophuot.chat.presenter.ChatPresenter;
import com.thanhduy.ophuot.model.Message;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.push_notification.PushMessage;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.EncodeImage;
import com.thanhduy.ophuot.utils.SessionManagerUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by buivu on 06/04/2017.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.img_send)
    ImageView imgSend;
    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_more)
    ImageView imgMore;

    private ChatPresenter presenter;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String partnerId = "";
    private String deviceToken;
    private DatabaseReference mDatabase;
    private User user;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static int RESULT_LOAD_IMAGE = 1;
    private StorageReference mStorage;
    private LinearLayoutManager linearLayoutManager;
    private SessionManagerUser sessionManagerUser;
    private HashMap<String, String> hashDataUser = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        user = (User) getIntent().getSerializableExtra(Constants.USERS);
        partnerId = user.getUid();
        deviceToken = user.getDeviceToken();
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(user.getName());
        //init
        sessionManagerUser = new SessionManagerUser(this);
        hashDataUser = sessionManagerUser.getUserDetails();
        presenter = new ChatPresenter(this);
        chatAdapter = new ChatAdapter(this, messageList, user.getAvatar());
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(linearLayoutManager);
        recyclerChat.setAdapter(chatAdapter);
        //load data chat
        loadDataChat();
        //event click
        imgSend.setOnClickListener(this);
        imgMore.setOnClickListener(this);
        //event text change
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgSend.setVisibility(View.GONE);
                } else {
                    imgSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadDataChat() {
        presenter.loadAllDataImage(getUid(), partnerId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                        recyclerChat.scrollToPosition(messageList.size() - 1);
                        chatAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgSend) {
            addDataMessage();
        } else if (v == imgMore) {
            showMore(v);
        }
    }

    private void showMore(View v) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_chat_function, menuBuilder);
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, v);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();
        //item click
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                if (item.getItemId() == R.id.action_show_gallery) {
                    if (verifyStoragePermissions()) {
                        showGallery();
                    }
                } else if (item.getItemId() == R.id.action_open_camera) {
                    if (verifyOpenCamera()) {
                        openCamera();
                    }
                }
                return true;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        });
        //show menu


    }

    private void addDataMessage() {
        String content = edtContent.getText().toString();
        long timestamp = new Date().getTime();
        Message message = new Message(content, false, timestamp, getUid());
        message.setDisplayStatus(false);
        //add message
        presenter.addMessage(partnerId, message);
        recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount());
        //send push notification
        //send push notification
        String[] regIds = {deviceToken};

        JSONArray regArray = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            try {
                regArray = new JSONArray(regIds);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PushMessage.sendMessage(regArray, hashDataUser.get(SessionManagerUser.KEY_NAME), edtContent.getText().toString(), "", getUid(), hashDataUser.get(SessionManagerUser.KEY_DEVICE_TOKEN), hashDataUser.get(SessionManagerUser.KEY_AVATAR));
        //clear data
        edtContent.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

    private boolean verifyOpenCamera() {
        int camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA
            );

            return false;
        }
        return true;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            byte[] arrImageBytes = EncodeImage.encodeImage(getRealPathFromURI(data.getData()));
            String fileName = String.format("%d%s", new Date().getTime(), getUid());
            StorageReference storageForUpFile = mStorage.child(Constants.CHAT).child(fileName);
            UploadTask uploadTask = storageForUpFile.putBytes(arrImageBytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("CHAT_IMAGE", e.getMessage());
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Message message = new Message(taskSnapshot.getDownloadUrl().toString(), true, new Date().getTime(), getUid());
                    //add message
                    presenter.addMessage(partnerId, message);
                }
            });
        } else if (requestCode == Constants.CAMERA_INTENT && resultCode == RESULT_OK) {
            byte[] arrImageBytes = EncodeImage.encodeImage(getRealPathFromURI(data.getData()));
            String fileName = String.format("%d%s", new Date().getTime(), getUid());
            StorageReference storageForUpFile = mStorage.child(Constants.CHAT).child(fileName);
            UploadTask uploadTask = storageForUpFile.putBytes(arrImageBytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("CHAT_IMAGE", e.getMessage());
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Message message = new Message(taskSnapshot.getDownloadUrl().toString(), true, new Date().getTime(), getUid());
                    //add message
                    presenter.addMessage(partnerId, message);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
