package com.brycegao.libchoose;

import android.app.Activity;
import android.content.Context;

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


  public ByPhoto(Context context) {
    mCtx = context;
  }

  public static ByPhoto with(Context context) {
    return new ByPhoto(context);
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
