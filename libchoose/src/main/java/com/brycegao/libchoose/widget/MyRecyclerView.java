package com.brycegao.libchoose.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.brycegao.libchoose.inter.ITouchEventListener;

public class MyRecyclerView extends RecyclerView {
  private ITouchEventListener mListener;


  public MyRecyclerView(@NonNull Context context) {
    super(context);
  }

  public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setCallBack(ITouchEventListener listener) {
    mListener = listener;
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    if (mListener != null) {
      boolean result = mListener.onMotionEvent(ev);
      if (result) {
       // return true;
      }
    }
    return super.dispatchTouchEvent(ev);
  }


}
