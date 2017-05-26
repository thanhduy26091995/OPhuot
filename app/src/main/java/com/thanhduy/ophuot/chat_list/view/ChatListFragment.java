package com.thanhduy.ophuot.chat_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.chat_list.ChatListAdapter;
import com.thanhduy.ophuot.chat_list.presenter.ChatListPresenter;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 08/04/2017.
 */

public class ChatListFragment extends Fragment {

    private View rootView;
    private ChatListPresenter presenter;
    private RecyclerView mRecycler;
    private List<String> listUid;
    private ChatListAdapter chatListAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_chat_list, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_chat);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        presenter = new ChatListPresenter(this);
        listUid = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getActivity(), listUid);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (InternetConnection.getInstance().isOnline(getActivity())) {
                loadDataChatList();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            ShowAlertDialog.showAlert(getActivity().getResources().getString(R.string.loginFirst), getActivity());
            progressBar.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(chatListAdapter);
        return rootView;
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    private void loadDataChatList() {
        presenter.getAllChat(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideItemData();
                    presenter.getAllChat(BaseActivity.getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot != null) {
                                if (!listUid.contains(dataSnapshot.getKey())) {
                                    listUid.add(dataSnapshot.getKey());
                                    chatListAdapter.notifyDataSetChanged();
                                }
                            }
                            showItemData();
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
                            showItemData();
                        }
                    });
                } else {
                    mRecycler.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
