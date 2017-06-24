package com.thanhduy.ophuot.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.model.FavoriteInfo;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 24/03/2017.
 */

public class BottomDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private PostInfo postInfo;
    private LinearLayout linearMyFavorite, linearCreateNewList;
    private DatabaseReference mDatabase;
    private int position;
    private PositionCallback positionCallback;
    private TextView txtFavoriteName, txtFavoriteNumber;
    private ImageView imgMyFavoritePoster;
    private EditText edtFavoriteName;
    private RecyclerView mRecycler;
    private List<String> listFavoriteId = new ArrayList<>();
    private FavoriteListAdapterForBottomDialog favoriteAdapter;
    public static BottomSheetDialogFragment instance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instance = this;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("BOTTOM", "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void setPositionCallback(PositionCallback positionCallback) {
        this.positionCallback = positionCallback;
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        Log.d("BOTTOM", "setUpDialog");
        super.setupDialog(dialog, style);
        //init views
        View contentView = View.inflate(getContext(), R.layout.activity_bottom_sheet_featured, null);
        linearMyFavorite = (LinearLayout) contentView.findViewById(R.id.linear_my_favorite);
        txtFavoriteName = (TextView) contentView.findViewById(R.id.txt_favorite_name);
        txtFavoriteNumber = (TextView) contentView.findViewById(R.id.txt_favorite_number);
        imgMyFavoritePoster = (ImageView) contentView.findViewById(R.id.img_my_favorite_poster);
        linearCreateNewList = (LinearLayout) contentView.findViewById(R.id.linear_create_new_list);
        mRecycler = (RecyclerView) contentView.findViewById(R.id.recycler_list_favorite);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent
        postInfo = (PostInfo) getArguments().getSerializable(Constants.POST_INFO);
        position = getArguments().getInt(Constants.POSITION);
        //event click
        linearMyFavorite.setOnClickListener(this);
        linearCreateNewList.setOnClickListener(this);
        dialog.setContentView(contentView);
        favoriteAdapter = new FavoriteListAdapterForBottomDialog(getActivity(), listFavoriteId, postInfo, position, positionCallback);
        //load data
        loadDataFavoriteList();

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(favoriteAdapter);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetCallback);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == linearMyFavorite) {
            if (InternetConnection.getInstance().isOnline(getActivity())) {
                if (postInfo != null) {
                    addDataToFavoriteHomestay(postInfo, BaseActivity.getUid());
                    positionCallback.positionCallBack(position);
                }
            } else {
                dismiss();
                ShowSnackbar.showSnack(getActivity(), getActivity().getResources().getString(R.string.noInternet));
            }
        } else if (v == linearCreateNewList) {
            showAlertDialogForCreateNewList();
        }
    }

    private void updateHomestayToMyFavoriteList(PostInfo postInfo, String uid) {
        /*
        save user node
         */
        //init data favorite
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
        data.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
        data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        //save homestay to list homestay favorite (yêu thích cảu tôi)
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(BaseActivity.getUid()).child(Constants.LIST_HOMESTAY).child(postInfo.getHomestayId()).updateChildren(data);
        /*
            save homestay node
         */
        //add data into homestay favorite (save homestay node)
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(uid, true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
        //close dialog fragment
        dismiss();
    }

    private void insertHomestayToMyFavoriteList(PostInfo postInfo, String uid) {
        /*
        save user node
         */
        //save data info favorite
        Map<String, Object> dataInfo = new HashMap<>();
        dataInfo.put(Constants.FAVORITE_NAME, getActivity().getResources().getString(R.string.myFavorite));
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(uid).child(Constants.INFO).updateChildren(dataInfo);
        //init data favorite
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
        data.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
        data.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        //save homestay to list homestay favorite (yêu thích cảu tôi)
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(BaseActivity.getUid()).child(Constants.LIST_HOMESTAY).child(postInfo.getHomestayId()).updateChildren(data);
        /*
            save homestay node
         */
        //add data into homestay favorite (save homestay node)
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(uid, true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
        //close dialog fragment
        dismiss();
    }


    private void addDataToFavoriteHomestay(final PostInfo postInfo, final String uid) {
        //kiểm tra nếu danh sách yêu thích chưa được tạo, thì tạo 1 danh sách
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateHomestayToMyFavoriteList(postInfo, uid);
                } else {
                    insertHomestayToMyFavoriteList(postInfo, uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadDataFavoriteList() {
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getKey().equals(BaseActivity.getUid())) {
                        FavoriteInfo favoriteInfo = dataSnapshot.child(Constants.INFO).getValue(FavoriteInfo.class);
                        if (favoriteInfo != null) {
                            txtFavoriteName.setText(favoriteInfo.getFavoriteName());
                            if (getActivity().getResources() != null) {
                                txtFavoriteNumber.setText(String.format("%d %s", dataSnapshot.child(Constants.LIST_HOMESTAY).getChildrenCount(), getActivity().getResources().getString(R.string.post)));
                            }
                        }
                        for (DataSnapshot listHomestay : dataSnapshot.child(Constants.LIST_HOMESTAY).getChildren()) {
                            PostInfo postInfo = listHomestay.getValue(PostInfo.class);
                            if (postInfo != null) {
                                mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                                        .child(postInfo.getHomestayId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            Homestay homestay = dataSnapshot.getValue(Homestay.class);
                                            if (homestay != null) {
                                                ImageLoader.getInstance().loadImageOther(getActivity(), homestay.getImages().get(0), imgMyFavoritePoster);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            break;
                        }
                    } else {
                        if (!listFavoriteId.contains(dataSnapshot.getKey())) {
                            listFavoriteId.add(dataSnapshot.getKey());
                            favoriteAdapter.notifyDataSetChanged();
                        }
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

    private void showAlertDialogForCreateNewList() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog_edit_favorite_name, null);
        builder.setView(v);
        //get instance of views in custom view
        edtFavoriteName = (EditText) v.findViewById(R.id.edt_favorite_name);
        TextView txtTitle = (TextView) v.findViewById(R.id.title);
        txtTitle.setText(getActivity().getResources().getText(R.string.createNewFavoriteListTitle));

        //event click
        builder.setPositiveButton(getResources().getString(R.string.createNew), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (InternetConnection.getInstance().isOnline(getActivity())) {
                    createNewList();
                    positionCallback.positionCallBack(position);
                    builder.create().dismiss();
                } else {
                    dismiss();
                    builder.create().dismiss();
                    ShowSnackbar.showSnack(getActivity(), getActivity().getResources().getString(R.string.noInternet));
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
            }
        });

        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //show dialog
        builder.create().show();
    }

    private void createNewList() {
        /*
        save user node
         */
        String key = mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).push().getKey();
        //update info
        String name = edtFavoriteName.getText().toString();
        Map<String, Object> dataInfo = new HashMap<>();
        dataInfo.put(Constants.FAVORITE_NAME, name);
        //save data
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).child(key).child(Constants.INFO)
                .updateChildren(dataInfo);
        //data homestay
        Map<String, Object> dataListHomestay = new HashMap<>();
        dataListHomestay.put(Constants.ID_PROVINCE, postInfo.getProvinceId());
        dataListHomestay.put(Constants.ID_DISTRICT, postInfo.getDistrictId());
        dataListHomestay.put(Constants.HOMESTAY_ID, postInfo.getHomestayId());
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FAVORITE).child(key).child(Constants.LIST_HOMESTAY)
                .child(postInfo.getHomestayId()).updateChildren(dataListHomestay);
        /*
        save homestay node
         */
         /*
            save homestay node
         */
        //add data into homestay favorite (save homestay node)
        Map<String, Object> dataFavoriteHomestay = new HashMap<>();
        dataFavoriteHomestay.put(BaseActivity.getUid(), true);
        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).updateChildren(dataFavoriteHomestay);
        dismiss();
    }
}
