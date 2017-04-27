package com.thanhduy.ophuot.chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.IconTextView;
import com.thanhduy.ophuot.chat.view.DisplayImageActivity;
import com.thanhduy.ophuot.model.Message;
import com.thanhduy.ophuot.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buivu on 06/04/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Activity activity;
    private List<Message> messageList;
    private String avatarPartner;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    private boolean isClickContentPartner = false;
    private boolean isClickContentMine = false;

    public ChatAdapter(Activity activity, List<Message> messageList, String avatarPartner) {
        this.activity = activity;
        this.messageList = messageList;
        this.avatarPartner = avatarPartner;
        imageLoader = ImageLoader.getInstance();
        //init config
        configImageLoader(activity);

    }

    public ChatAdapter(Activity activity, List<Message> messageList) {
        this.activity = activity;
        this.messageList = messageList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(activity, R.layout.message_row, null);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.setItem(message);
        if (message.getIsMine()) {
            displayMineMessage(holder, message);
        } else {
            displayPartnerMessage(holder, message, position);
        }

    }

    private void displayPartnerMessage(ChatViewHolder holder, Message message, int position) {
        //show avatar
        com.thanhduy.ophuot.base.ImageLoader.getInstance().loadImageAvatar(activity, avatarPartner, holder.imgPartnerAvatar);
        //hide view
        holder.txtMineContent.setVisibility(View.GONE);
        holder.icRightArrow.setVisibility(View.GONE);
        holder.imgMineImage.setVisibility(View.GONE);
        holder.relaMine.setVisibility(View.GONE);
        //show views
        holder.relaPartner.setVisibility(View.VISIBLE);
        holder.txtPartnerContent.setVisibility(View.VISIBLE);
        holder.icLeftArrow.setVisibility(View.VISIBLE);
        holder.imgPartnerImage.setVisibility(View.VISIBLE);
        holder.imgPartnerAvatar.setVisibility(View.VISIBLE);
        //check show status
        if (message.isDisplayStatus()) {
            holder.txtPartnerStatus.setVisibility(View.VISIBLE);
            holder.txtPartnerStatus.setText(caculateTimeAgo(message.getTimestamp()));
        } else {
            holder.txtPartnerStatus.setVisibility(View.GONE);
        }
        //handle data
        if (message.getIsImage()) {
            holder.icLeftArrow.setVisibility(View.GONE);
            holder.txtPartnerContent.setVisibility(View.GONE);
            holder.imgPartnerImage.setVisibility(View.VISIBLE);
            //load image
            imageLoader.displayImage(message.getContent(), holder.imgPartnerImage);
            //  com.thanhduy.ophuot.base.ImageLoader.getInstance().loadImageChat(activity, message.getContent(), holder.imgPartnerImage);
        } else {
            holder.icLeftArrow.setVisibility(View.VISIBLE);
            holder.txtPartnerContent.setVisibility(View.VISIBLE);
            holder.imgPartnerImage.setVisibility(View.GONE);
            //set text
            holder.txtPartnerContent.setText(message.getContent());
        }
        //check is the same send by
        if (isSameSendBy(position, message)) {
            holder.icLeftArrow.setVisibility(View.GONE);
            holder.imgPartnerAvatar.setVisibility(View.GONE);
        }
    }

    private boolean isSameSendBy(int position, Message messageCurrent) {
        if (position > 0) {
            Message messagePre = messageList.get(position - 1);
            if (messageCurrent.getSendBy().equals(messagePre.getSendBy())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void displayMineMessage(ChatViewHolder holder, Message message) {
        //hide view
        holder.txtMineContent.setVisibility(View.VISIBLE);
        holder.icRightArrow.setVisibility(View.VISIBLE);
        holder.imgMineImage.setVisibility(View.VISIBLE);
        holder.relaMine.setVisibility(View.VISIBLE);
        //show views
        holder.relaPartner.setVisibility(View.GONE);
        holder.txtPartnerContent.setVisibility(View.GONE);
        holder.icLeftArrow.setVisibility(View.GONE);
        holder.imgPartnerImage.setVisibility(View.GONE);
        holder.imgPartnerAvatar.setVisibility(View.GONE);
        //check show status
        if (message.isDisplayStatus()) {
            holder.txtMineStatus.setVisibility(View.VISIBLE);
            holder.txtMineStatus.setText(caculateTimeAgo(message.getTimestamp()));
        } else {
            holder.txtMineStatus.setVisibility(View.GONE);
        }
        //handle data
        if (message.getIsImage()) {
            holder.txtMineContent.setVisibility(View.GONE);
            holder.icRightArrow.setVisibility(View.GONE);
            holder.imgMineImage.setVisibility(View.VISIBLE);
            //load image
            holder.imgMineImage.layout(0, 0, 0, 0);
            //  ImageLoader.getInstance().loadImageChat(activity, message.getContent(), holder.imgMineImage);
            imageLoader.displayImage(message.getContent(), holder.imgMineImage);
        } else {
            holder.txtMineContent.setVisibility(View.VISIBLE);
            holder.icRightArrow.setVisibility(View.VISIBLE);
            holder.imgMineImage.setVisibility(View.GONE);
            //set text
            holder.txtMineContent.setText(message.getContent());
        }
    }

    private void configImageLoader(Activity activity) {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.no_image)
                .showImageOnLoading(R.drawable.no_image)
                .build();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(activity.getApplicationContext())
                .defaultDisplayImageOptions(displayImageOptions)
                .memoryCache(new WeakMemoryCache()).build();
        imageLoader.init(imageLoaderConfiguration);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtPartnerContent, txtMineContent, txtPartnerStatus, txtMineStatus;
        public IconTextView icLeftArrow, icRightArrow;
        public ImageView imgPartnerImage, imgMineImage;
        public CircleImageView imgPartnerAvatar;
        public RelativeLayout relaPartner, relaMine;
        private Message message;

        public ChatViewHolder(View itemView) {
            super(itemView);

            txtPartnerContent = (TextView) itemView.findViewById(R.id.txt_partner_content_chat);
            txtMineContent = (TextView) itemView.findViewById(R.id.txt_mine_content);
            icLeftArrow = (IconTextView) itemView.findViewById(R.id.ic_left_arrow);
            icRightArrow = (IconTextView) itemView.findViewById(R.id.ic_right_arrow);
            imgPartnerImage = (ImageView) itemView.findViewById(R.id.img_partner_image);
            imgMineImage = (ImageView) itemView.findViewById(R.id.img_mine_image);
            imgPartnerAvatar = (CircleImageView) itemView.findViewById(R.id.img_avatar_partner);
            relaPartner = (RelativeLayout) itemView.findViewById(R.id.rela_partner_chat);
            relaMine = (RelativeLayout) itemView.findViewById(R.id.rela_mine_chat);
            txtMineStatus = (TextView) itemView.findViewById(R.id.txt_mine_status);
            txtPartnerStatus = (TextView) itemView.findViewById(R.id.txt_partner_status);
            //event click
            imgMineImage.setOnClickListener(this);
            imgPartnerImage.setOnClickListener(this);
            txtPartnerContent.setOnClickListener(this);
            txtMineContent.setOnClickListener(this);
        }

        public void setItem(Message message) {
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            if (v == imgMineImage || v == imgPartnerImage) {
                Intent intent = new Intent(activity, DisplayImageActivity.class);
                intent.putExtra(Constants.IMAGES, message.getContent());
                activity.startActivity(intent);
            } else if (v == txtPartnerContent) {
                isClickContentPartner = !isClickContentPartner;
                if (isClickContentPartner) {
                    txtPartnerStatus.setVisibility(View.VISIBLE);
                    txtPartnerStatus.setText(caculateTimeAgo(message.getTimestamp()));
                } else {
                    txtPartnerStatus.setVisibility(View.GONE);
                }
                message.setDisplayStatus(isClickContentPartner);
            } else if (v == txtMineContent) {
                isClickContentMine = !isClickContentMine;
                if (isClickContentMine) {
                    txtMineStatus.setVisibility(View.VISIBLE);
                    txtMineStatus.setText(caculateTimeAgo(message.getTimestamp()));
                } else {
                    txtMineStatus.setVisibility(View.GONE);
                }
                message.setDisplayStatus(isClickContentMine);
            }
        }
    }

    private String caculateTimeAgo(long timestamp) {
        long currentDate = new Date().getTime();
        long realTime = (currentDate - timestamp) / 1000;
        if (realTime < 3600 * 24) {
            return new SimpleDateFormat("HH:mm").format(new Date(timestamp));
        } else {
            return new SimpleDateFormat("dd/MM HH:mm").format(new Date(timestamp));
        }
    }
}
