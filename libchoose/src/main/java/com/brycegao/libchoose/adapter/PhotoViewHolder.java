package com.brycegao.libchoose.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.brycegao.libchoose.R;
import com.brycegao.libchoose.inter.IDrawCallBack;
import com.brycegao.libchoose.model.ImageItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
  private Context mContex;
  private ImageView ivImage;
  private ImageView ivMark;

  private ViewTreeObserver.OnDrawListener drawListener;

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

  public void updateData(ImageItem item, boolean isSel, final int position,
      final IDrawCallBack callBack) {
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
        .into(new CustomViewTarget<ImageView, Drawable>(ivImage) {
          @Override protected void onResourceCleared(@Nullable Drawable placeholder) {

          }

          @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {

          }

          @Override public void onResourceReady(@NonNull Drawable resource,
              @Nullable Transition<? super Drawable> transition) {
            if (resource != null) {
              ivImage.setImageDrawable(resource);

              if (position == 0) {
                drawListener = new ViewTreeObserver.OnDrawListener() {
                  @Override public void onDraw() {
                    if (callBack != null) {
                      callBack.onDrawComplete(System.currentTimeMillis());
                    }

                    ivImage.post(new Runnable() {
                      @Override public void run() {
                        ivImage.getViewTreeObserver().removeOnDrawListener(drawListener);
                      }
                    });
                  }
                };
                ivImage.getViewTreeObserver().addOnDrawListener(drawListener);
              }
            }
          }
        });
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
