package com.thanhduy.ophuot.chat.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.chat.ChatAdapter;
import com.thanhduy.ophuot.chat.presenter.ChatPresenter;
import com.thanhduy.ophuot.model.Message;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
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

    private ChatPresenter presenter;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String partnerId = "";
    private DatabaseReference mDatabase;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent
//        partnerId = getIntent().getStringExtra(Constants.USER_ID);
//        partnerName = getIntent().getStringExtra(Constants.NAME);
        user = (User) getIntent().getSerializableExtra(Constants.USERS);
        partnerId = user.getUid();
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(user.getName());
        //init
        presenter = new ChatPresenter(this);
        chatAdapter = new ChatAdapter(this, messageList, user.getAvatar());
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);
        //load data chat
        loadDataChat();
        //event click
        imgSend.setOnClickListener(this);
        //event text change
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgSend.setEnabled(false);
                } else {
                    imgSend.setEnabled(true);
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
        }
    }

    private void addDataMessage() {
        String content = edtContent.getText().toString();
        long timestamp = new Date().getTime();
        Message message = new Message(content, false, timestamp, getUid());
        //add message
        presenter.addMessage(partnerId, message);
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
}
