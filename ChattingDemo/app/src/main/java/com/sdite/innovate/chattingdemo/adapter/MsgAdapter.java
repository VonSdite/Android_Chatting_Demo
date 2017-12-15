package com.sdite.innovate.chattingdemo.adapter;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdite.innovate.chattingdemo.R;
import com.sdite.innovate.chattingdemo.module.Msg;

import java.util.List;

/**
 * Created by Sdite on 17/12/9.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;
    private ContentResolver cr;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        ImageView leftImg;

        ImageView rightImg;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_msg);
            rightMsg = (TextView) view.findViewById(R.id.right_msg);
            leftImg = (ImageView) view.findViewById(R.id.left_img);
            rightImg = (ImageView) view.findViewById(R.id.right_img);
        }
    }

    public MsgAdapter(List<Msg> msgList, ContentResolver cr) {
        mMsgList = msgList;
        this.cr = cr;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent,
                false);
        return new ViewHolder(view);
    }

    private static final String TAG = "MsgAdapter";

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftImg.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightImg.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.TYPE_SENT_PIC) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setVisibility(View.GONE);

            String imagePath = msg.getContent();
            if (imagePath != null) {
                // 显示图片
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                holder.rightImg.setImageBitmap(bitmap);
            }
        } else if (msg.getType() == Msg.TYPE_RECEIVED_PIC) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMsg.setVisibility(View.GONE);
            String imagePath = msg.getContent();
            if (imagePath != null) {
                // 显示图片
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                holder.leftImg.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}