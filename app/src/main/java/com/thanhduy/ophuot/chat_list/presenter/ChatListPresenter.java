package com.thanhduy.ophuot.chat_list.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thanhduy.ophuot.chat_list.view.ChatListFragment;
import com.thanhduy.ophuot.utils.Constants;

/**
 * Created by buivu on 08/04/2017.
 */

public class ChatListPresenter {
    private DatabaseReference mDatabase;
    private ChatListFragment view;

    public ChatListPresenter(ChatListFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

//    public Query getAllChat(String currentId){
//        return mDatabase.child(Constants.MESSAGES).child(currentId);
//    }

    public Query getAllChat(String currentId){
        return mDatabase.child(Constants.USERS).child(currentId).child(Constants.CHAT).orderByChild(Constants.TIMESTAMP);
    }
}
