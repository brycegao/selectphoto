package com.brycegao.libchoose.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.brycegao.libchoose.inter.ITouchEventListener;

public class MyLinearLayout extends LinearLayout {
  private ITouchEventListener mListener;

  public MyLinearLayout(Context context) {
    super(context);
  }

  public MyLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyLinearLayout(Context context, AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setCallBack(ITouchEventListener listener) {
    mListener = listener;
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mListener != null) {
      boolean result = mListener.onMotionEvent(ev);
      if (result) {
        return true;
      }
    }
    return super.onInterceptTouchEvent(ev);
  }
}
