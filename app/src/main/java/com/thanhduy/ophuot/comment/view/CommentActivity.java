package com.thanhduy.ophuot.comment.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.comment.CommentAdapter;
import com.thanhduy.ophuot.comment.presenter.CommentPresenter;
import com.thanhduy.ophuot.model.Comment;
import com.thanhduy.ophuot.model.Homestay;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.MyLinearLayoutManager;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 22/03/2017.
 */

public class CommentActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_comment)
    RecyclerView mRecycler;
    @BindView(R.id.img_send)
    ImageView imgSend;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.rating_comment)
    RatingBar ratingComment;

    private Homestay homestay;
    private CommentPresenter presenter;
    private MyLinearLayoutManager myLinearLayoutManager;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        presenter = new CommentPresenter(this);
        //get intent
        homestay = (Homestay) getIntent().getSerializableExtra(Constants.HOMESTAY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (homestay.getComments() != null) {
            getSupportActionBar().setTitle(String.format("%s (%d)", getResources().getString(R.string.comment), homestay.getComments().getCommentCount()));
        } else {
            getSupportActionBar().setTitle(String.format("%s", getResources().getString(R.string.comment)));
        }

        commentAdapter = new CommentAdapter(this, commentList);
        myLinearLayoutManager = new MyLinearLayoutManager(this);
        mRecycler.setLayoutManager(myLinearLayoutManager);
        mRecycler.setAdapter(commentAdapter);
        loadDataComment();
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

    //hide keyboard when click send
    private void hideKeyboardWhenCommented(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //load all data comment
    private void loadDataComment() {
        presenter.getAllComments(homestay).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                        commentAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == imgSend) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                addComment();
                //hide keyboard
                hideKeyboardWhenCommented(v);
            } else {
                ShowAlertDialog.showAlert(getResources().getString(R.string.loginFirst), this);
            }
        }
    }

    //add comment into server
    private void addComment() {
        long commentTime = new Date().getTime();
        String content = edtContent.getText().toString();
        int rating = (int) ratingComment.getRating();
        //call presenter
        presenter.addComment(homestay, getUid(), content, rating, commentTime);
        //clear edittext
        edtContent.setText("");
        mRecycler.smoothScrollToPosition(commentAdapter.getItemCount());
        //update rating
        ratingComment.setRating(0);
    }
}
