package com.thanhduy.ophuot.chat_list;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.chat.view.ChatActivity;
import com.thanhduy.ophuot.model.Message;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by buivu on 08/04/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    public Activity activity;
    public List<String> listUid;
    private DatabaseReference mDatabase;
    private HashMap<String, Boolean> hashForLoadFirstTime;
    private int count = 0;

    public ChatListAdapter(Activity activity, List<String> listUid) {
        this.activity = activity;
        this.listUid = listUid;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        hashForLoadFirstTime = new HashMap<>();
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(activity, R.layout.item_chat_list, null);
        return new ChatListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatListViewHolder holder, final int position) {
        count = 0;
        final String partnerId = listUid.get(position);
        //load infor partner
        mDatabase.child(Constants.USERS).child(partnerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.txtName.setText(user.getName());
                        ImageLoader.getInstance().loadImageAvatar(activity, user.getAvatar(), holder.imgAvatar);
                    }
                    //event click
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, ChatActivity.class);
                            intent.putExtra(Constants.USERS, user);
                            activity.startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //load last message
        mDatabase.child(Constants.MESSAGES).child(BaseActivity.getUid()).child(partnerId).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Message message = data.getValue(Message.class);
                        //save data
//                        hashForLoadFirstTime.put(partnerId, true);
                        count++;
                        if (count <= listUid.size()) {
                            if (message != null) {
                                holder.txtTime.setText(caculateTimeAgo(message.getTimestamp()));
                                if (message.getIsMine()) {
                                    if (message.getIsImage()) {
                                        holder.txtContent.setText(String.format("%s %s", activity.getResources().getString(R.string.you),
                                                activity.getResources().getString(R.string.sendImage)));
                                    } else {
                                        holder.txtContent.setText(String.format("%s %s", activity.getResources().getString(R.string.you),
                                                message.getContent()));
                                    }
                                } else {
                                    if (message.getIsImage()) {
                                        holder.txtContent.setText(String.format("%s %s", activity.getResources().getString(R.string.you),
                                                activity.getResources().getString(R.string.receiveImage)));
                                    } else {
                                        holder.txtContent.setText(String.format("%s",
                                                message.getContent()));
                                    }
                                }
                            }
                        } else {
                            String key = "";
                            if (message.getIsMine()) {
                                key = message.getReceiveBy();
                            } else {
                                key = message.getSendBy();
                            }
                            updateLastMessage(key);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean checkDataComplete() {
        for (String s : listUid) {
            if (hashForLoadFirstTime.get(s) == null) {
                return false;
            }
        }
        return true;
    }

    private String caculateTimeAgo(long timestamp) {
        long currentDate = new Date().getTime();
        long realTime = (currentDate - timestamp) / 1000;
        if (realTime < 60) {
            return "Vừa xong";
        } else if (realTime < 3600) {
            long minutes = (long) Math.floor(realTime / 60);
            return String.format("%d phút", minutes);
        } else if (realTime < 3600 * 24) {
            long hours = (long) Math.floor(realTime / 3600);
            return String.format("%d giờ", hours);
        } else {
            return new SimpleDateFormat("dd/MM").format(new Date(timestamp));
        }
    }

    private void updateLastMessage(String key) {
        int pos = getPosition(key);
        Log.d("CHAT_", "" + pos + "/" + key);
        if (pos >= 0) {
            listUid.remove(pos);
            listUid.add(0, key);
            notifyDataSetChanged();
        } else {
            listUid.add(0, key);
            notifyDataSetChanged();
        }
    }


    private int getPosition(String key) {
        int i = 0;
        for (String item : listUid) {
            if (item.equals(key)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return listUid.size();
    }

public class ChatListViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgAvatar;
    public TextView txtName, txtContent, txtTime;

    public ChatListViewHolder(View itemView) {
        super(itemView);

        imgAvatar = (ImageView) itemView.findViewById(R.id.img_mess_avatar);
        txtName = (TextView) itemView.findViewById(R.id.txt_mess_name);
        txtContent = (TextView) itemView.findViewById(R.id.txt_mess_content);
        txtTime = (TextView) itemView.findViewById(R.id.txt_mess_time);
    }
}
}
