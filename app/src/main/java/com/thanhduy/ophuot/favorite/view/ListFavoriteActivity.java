package com.thanhduy.ophuot.favorite.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.list_homestay.ListHomestayAdapter;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.model.PostInfo;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 27/03/2017.
 */

public class ListFavoriteActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_homestay)
    RecyclerView mRecycler;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_no_result)
    ImageView imgNoResult;


    private ArrayList<PostInfo> postInfos;
    private DatabaseReference mDatabase;
    private List<Homestay> listHomestay;
    private ListHomestayAdapter listHomestayAdapter;
    private String favoriteName, favoriteId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_favorite);
        ButterKnife.bind(this);

        listHomestay = new ArrayList<>();
        listHomestayAdapter = new ListHomestayAdapter(this, listHomestay);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(listHomestayAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //get intent
        postInfos = (ArrayList<PostInfo>) getIntent().getSerializableExtra(Constants.LIST_POST_INFO);
        favoriteName = getIntent().getStringExtra(Constants.FAVORITE_NAME);
        favoriteId = getIntent().getStringExtra(Constants.FAVORITE_ID);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(favoriteName);

        initInfo();
    }

    private void initInfo() {
        if (InternetConnection.getInstance().isOnline(ListFavoriteActivity.this)) {
            if (postInfos != null) {
                getDataHomestay();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initInfo();
                        }
                    });
            snackbar.show();
        }
    }

    private void showItemData() {
        progressBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void hideItemData() {
        progressBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        imgNoResult.setVisibility(View.GONE);
    }

    private void getDataHomestay() {
        hideItemData();
        if (postInfos.size() > 0) {
            for (PostInfo postInfo : postInfos) {
                mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                        .child(postInfo.getHomestayId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Homestay homestay = dataSnapshot.getValue(Homestay.class);
                            if (homestay != null) {
                                listHomestay.add(homestay);
                                listHomestayAdapter.notifyDataSetChanged();
                            }
                        }
                        showItemData();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showItemData();
                    }
                });
                //Log.d("POST", "" + postInfo.getProvinceId() + "/" + postInfo.getDistrictId() + "/" + postInfo.getHomestayId());
            }
        } else {
            showItemData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_edit_favoriteName) {
            showAlertDialogForChangeFavoriteName();
        } else if (item.getItemId() == R.id.action_delete_favorite_list) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ListFavoriteActivity.this);
            builder.setMessage(R.string.confirmDelete)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (favoriteId.equals(getUid())) {
                                ShowAlertDialog.showAlert(getResources().getString(R.string.cannotDeleteList), ListFavoriteActivity.this);
                            } else {
                                deleteHomestayList(getUid(), favoriteId);
                                finish();
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialogForChangeFavoriteName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog_edit_favorite_name, null);
        builder.setView(v);
        //get instance of views in custom view
        final EditText edtFavoriteName = (EditText) v.findViewById(R.id.edt_favorite_name);
        //show favorite name into edittext
        edtFavoriteName.setText(favoriteName);

        //event click
        builder.setPositiveButton(getResources().getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteName = edtFavoriteName.getText().toString();
                //update data
                changeFavoriteName(favoriteName, favoriteId);
                builder.create().dismiss();
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

    private void changeFavoriteName(String name, String favoriteId) {
        //update title
        getSupportActionBar().setTitle(name);

        Map<String, Object> dataForChange = new HashMap<>();
        dataForChange.put(Constants.FAVORITE_NAME, name);
        //update data
        mDatabase.child(Constants.USERS).child(getUid()).child(Constants.FAVORITE).child(favoriteId).child(Constants.INFO).updateChildren(dataForChange);
    }

    private void deleteHomestayList(final String uid, String favoriteId) {
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(favoriteId).child(Constants.LIST_HOMESTAY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    PostInfo postInfo = dataSnapshot.getValue(PostInfo.class);
                    if (postInfo != null) {
                        //xóa thông tin người dùng trong node favorite của node lớn homestay
                        mDatabase.child(Constants.HOMESTAY).child(String.valueOf(postInfo.getProvinceId())).child(String.valueOf(postInfo.getDistrictId()))
                                .child(postInfo.getHomestayId()).child(Constants.FAVORITE).child(uid).removeValue();
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
        //delete homestay from node users
        mDatabase.child(Constants.USERS).child(uid).child(Constants.FAVORITE).child(favoriteId).removeValue();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            int position = data.getIntExtra(Constants.POSITION, 0);
            Homestay homestayIntent = (Homestay) data.getSerializableExtra(Constants.HOMESTAY);
            if (Constants.IS_CHANGE_LIST_FAVORITE) {
                Homestay homestay = listHomestay.get(position);
                homestay.getFavorite().clear();
                homestay.setFavorite(homestayIntent.getFavorite());
                if (homestay.getFavorite() != null) {
                    listHomestayAdapter.notifyDataSetChanged();
                } else {
                    Map<String, Boolean> dataFavorite = new HashMap<>();
                    dataFavorite.put(BaseActivity.getUid(), true);
                    homestay.setFavorite(dataFavorite);
                    homestay.getFavorite().put(BaseActivity.getUid(), true);
                    listHomestayAdapter.notifyDataSetChanged();
                }
                Constants.IS_CHANGE_LIST_FAVORITE = false;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
