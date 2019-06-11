package com.brycegao.libchoose.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 宽、高一致
 */
public class SquareLayout extends RelativeLayout {

  public SquareLayout(Context context) {
    super(context);
  }

  public SquareLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
    int childWidthSize = getMeasuredWidth();
    heightMeasureSpec = widthMeasureSpec =
        MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);// 将高度和宽度保持一致
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
