package com.thanhduy.ophuot.chat.presenter;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.chat.view.ChatActivity;
import com.thanhduy.ophuot.model.LastMessage;
import com.thanhduy.ophuot.model.Message;
import com.thanhduy.ophuot.utils.Constants;

import java.util.Map;

/**
 * Created by buivu on 06/04/2017.
 */

public class ChatPresenter {
    private ChatActivity view;
    private DatabaseReference mDatabase;

    public ChatPresenter(ChatActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void addData(String currentId, String partnerId, Message message, boolean isMine) {
        try {
            message.setIsMine(isMine);
            message.setReceiveBy(partnerId);
            String key = mDatabase.child(Constants.MESSAGES).child(currentId).child(partnerId).push().getKey();
            Map<String, Object> data = message.toMap();
            mDatabase.child(Constants.MESSAGES).child(currentId).child(partnerId).child(key).setValue(data);
        } catch (Exception e) {
            Log.d("CHAT", e.getMessage());
        }
    }

    public void addMessage(String partnerId, Message message) {
        addData(view.getUid(), partnerId, message, true);
        addData(partnerId, view.getUid(), message, false);
        //update last message
        updateLastMessage(view.getUid(), partnerId, message);
        updateLastMessage(partnerId, view.getUid(), message);
    }

    public Query loadAllDataImage(String currentId, String partnerId) {
        return mDatabase.child(Constants.MESSAGES).child(currentId).child(partnerId);
    }

    public void updateLastMessage(String currentId, String partnerId, Message message) {
        try {
            LastMessage lastMessage = new LastMessage(partnerId, message.getTimestamp() * -1);
            Map<String, Object> data = lastMessage.toMap();
            mDatabase.child(Constants.USERS).child(currentId).child(Constants.CHAT).child(partnerId).updateChildren(data);
        } catch (Exception e) {
            Log.d("CHAT", e.getMessage());
        }
    }
}
