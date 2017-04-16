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
            displayPartnerMessage(holder, message);
        }

    }

    private void displayPartnerMessage(ChatViewHolder holder, Message message) {
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
        public TextView txtPartnerContent, txtMineContent;
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
            //event click
            imgMineImage.setOnClickListener(this);
            imgPartnerImage.setOnClickListener(this);
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
            }
        }
    }
}
