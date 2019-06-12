package com.brycegao.libchoose;

import android.content.Context;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Constants {

  //子线程每次加载多少张图片后通知UI刷新
  public static final int PHOTO_COUNT_PER_TIME = 5;

  public static final String KEY_FLAGS = "KEY_FLAGS";

  //默认每行显示4个图片
  public static final int DEFAULT_LINE_COUNT = 3;

  //默认预加载多少张图片
  public static final int MAX_PRELOAD_PHOTO_NUMS = 15;

  //筛选的图片类型
  public static final String[] ACCEPTABLE_IMAGE_TYPES = new String[] {
      "image/jpeg", "image/png", "image/bmp"
  };

  //预加载前10条图片
  public static final String WHERE_CLAUSE =
      "(" + MediaStore.Images.Media.MIME_TYPE + " in (?, ?, ?,?))";

  public static int getScreenWidth(Context context) {
    DisplayMetrics metrics = new DisplayMetrics();
    WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getMetrics(metrics);
    return metrics.widthPixels;
  }
}
