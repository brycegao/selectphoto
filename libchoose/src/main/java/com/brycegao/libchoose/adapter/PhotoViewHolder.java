package com.brycegao.libchoose.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.brycegao.libchoose.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

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
  }

  public static PhotoViewHolder getInstance(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_grid_photo, null, false);
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

  public void updateData(Uri imageUri, boolean isSel) {
    if (isSel) {
      ivMark.setImageResource(R.drawable.icon_chat_album_selected);
    } else {
      ivMark.setImageResource(R.drawable.icon_chat_album_unselected);
    }

    RequestOptions options = new RequestOptions()
        .placeholder(R.drawable.icon_gridview_picture_normal)
        .error(R.drawable.icon_gridview_picture_normal);

    Glide.with(mContex)
        .load(imageUri)
        .apply(options)
        .into(ivImage);
  }
}
