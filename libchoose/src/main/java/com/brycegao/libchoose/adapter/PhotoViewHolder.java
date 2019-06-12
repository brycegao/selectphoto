package com.brycegao.libchoose.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.brycegao.libchoose.R;
import com.brycegao.libchoose.model.ImageItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
  private Context mContex;
  private ImageView ivImage;
  private ImageView ivMark;

  public PhotoViewHolder(@NonNull View itemView) {
    super(itemView);
  }

  public PhotoViewHolder(Context context, @NonNull View itemView) {
    super(itemView);

    mContex = context;
    initViews(itemView);
  }

  public static PhotoViewHolder getInstance(Context context, ViewGroup parent, int width) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_grid_photo, parent, false);
    view.getLayoutParams().width = width;
    view.getLayoutParams().height = width;
    return new PhotoViewHolder(context, view);
  }

  /**
   * 初始化各个View
   * @param view
   */
  private void initViews(View view) {
    ivImage = view.findViewById(R.id.iv_pic);
    ivMark  = view.findViewById(R.id.iv_selectbox);
  }

  public void updateData(ImageItem item, boolean isSel, int position) {
    if (isSel) {
      ivMark.setImageResource(R.drawable.icon_chat_album_selected);
    } else {
      ivMark.setImageResource(R.drawable.icon_chat_album_unselected);
    }

    RequestOptions options = new RequestOptions()
        .placeholder(R.drawable.icon_gridview_picture_normal)
        .error(R.drawable.icon_gridview_picture_normal);

    Glide.with(mContex)
        .load("file:///" + item.imagePath)
        .apply(options)
        .into(ivImage);
  }

  //只刷新选中按钮
  public void updateCheckStatus(boolean isSel) {
    if (isSel) {
      ivMark.setImageResource(R.drawable.icon_chat_album_selected);
    } else {
      ivMark.setImageResource(R.drawable.icon_chat_album_unselected);
    }
  }
}
