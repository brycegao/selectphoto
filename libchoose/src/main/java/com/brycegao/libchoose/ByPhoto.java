package com.brycegao.libchoose;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;

public class ByPhoto {
  private Context mCtx;

  private int mFlags;

  //一行显示多少张图片， 最少1个，最多5个
  private int mLineCount;

  //单击选中
  public static final int FLAG_ONE_FINGER_CHOOSE = 0X00000001;

  //单指横向或纵向滑动， 沿着滑动轨迹选中经过的图片
  public static final int FLAG_ONE_FINGER_SLIDE = 0X00000002;

  //单指斜向滑动， 选中矩形区域内的图片
  public static final int FLAG_ONE_FINGER_OBLIQUE = 0X00000004;

  //双指滑动，双指间的图片被选中； 且拦截滑动事件，控件不能滚动
  public static final int FLAG_DOUBLE_FINGER_SLIDE = 0X00000008;

  private static Handler sHandler = new Handler();


  public ByPhoto(Context context) {
    mCtx = context;
  }

  public static ByPhoto with(Context context) {
    return new ByPhoto(context);
  }

  /**
   * 预加载图片库的资源到Glide缓存中，从而能减少首帧近500ms的渲染时间
   */
  public static void preloadData(final Context ctx) {
    if (ctx == null) {
      return;
    }

    //使用进程上下文
    final Context context;
    if (ctx instanceof Activity) {
      context = ctx.getApplicationContext();
    } else {
      context = ctx;
    }

    new AsyncTask<Object, Object, Object>() {
      @Override protected Object doInBackground(Object[] objects) {
        Cursor cursor = null;
        int i = 0;
        try {
          cursor = context.getContentResolver()
              .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, Constants.WHERE_CLAUSE,
                  Constants.ACCEPTABLE_IMAGE_TYPES, MediaStore.Images.Media.DATE_MODIFIED);

          if (cursor == null ) {
            return null;
          }

          while (cursor.moveToNext() && i < Constants.MAX_PRELOAD_PHOTO_NUMS) {
            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            if (path != null && new File(path).exists()) {
              Log.d("brycegao", "文件已存在：" + path);
            }
            sHandler.post(new Runnable() {
              @Override public void run() {

                Glide.with(ctx)
                    .load(new File(path))
                    .addListener(new RequestListener<Drawable>() {
                      @Override
                      public boolean onLoadFailed(@Nullable GlideException e, Object model,
                          Target<Drawable> target, boolean isFirstResource) {
                        Log.d("brycegao", "加载失败：" + path);
                        return false;
                      }

                      @Override public boolean onResourceReady(Drawable resource, Object model,
                          Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("brycegao", "加载成功：" + path);

                        return false;
                      }
                    })
                    //.preload();
                    .preload(Constants.getScreenWidth(context), Constants.getScreenWidth(context));
              }
            });

            i++;
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
        } finally {
          if (cursor != null) {
            cursor.close();
            cursor = null;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * 设置支持的选中方式
   * @param flag
   * @return
   */
  public ByPhoto addFlags(int flag) {
    this.mFlags &= flag;
    return this;
  }

  public ByPhoto setFlags(int flag) {
    this.mFlags = flag;
    return this;
  }

  public ByPhoto setLineCount(int count) {
    this.mLineCount = count;
    return this;
  }

  /**
   * 单指点击是否使能
   * @param flag
   * @return
   */
  public static boolean isClickChooseEnabled(int flag) {
    int result = flag & FLAG_ONE_FINGER_CHOOSE;
    return result != 0? true:false;
  }

  //单指滑动是否使能
  public static boolean isOneFingerSlideEnabled(int flag) {
    int result = flag & FLAG_ONE_FINGER_SLIDE;
    return result != 0? true:false;
  }

  /**
   * 斜向滑动是否打开
   * @param flag
   * @return
   */
  public static boolean isObliqueEnabled(int flag) {
    int result = flag & FLAG_ONE_FINGER_OBLIQUE;
    return result != 0? true:false;
  }

  /**
   * 双指滑动是否打开
   * @param flag
   * @return
   */
  public static boolean isTowFinglerEnabled(int flag) {
    int result = flag & FLAG_DOUBLE_FINGER_SLIDE;
    return result != 0? true:false;
  }
}
