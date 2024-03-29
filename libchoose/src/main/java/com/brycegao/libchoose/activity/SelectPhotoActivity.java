package com.brycegao.libchoose.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.brycegao.libchoose.ByPhoto;
import com.brycegao.libchoose.Constants;
import com.brycegao.libchoose.R;
import com.brycegao.libchoose.adapter.PhotoViewAdapter;
import com.brycegao.libchoose.adapter.PhotoViewHolder;
import com.brycegao.libchoose.engine.LoadDataTask;
import com.brycegao.libchoose.inter.IClickItem;
import com.brycegao.libchoose.inter.IDrawCallBack;
import com.brycegao.libchoose.inter.ILoadData;
import com.brycegao.libchoose.inter.ITouchEventListener;
import com.brycegao.libchoose.model.ImageItem;
import com.brycegao.libchoose.widget.MyRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectPhotoActivity extends Activity implements ILoadData, View.OnClickListener {
  private MyRecyclerView mRvPhotos;
  private PhotoViewAdapter mAdaper;
  private GridLayoutManager mGridmanager;

  //查看大图
  private TextView mTvViewDetail;

  //选中个数
  private TextView mTvCount;

  private LoadDataTask mTask;

  private final int REQUEST_PERMISSION = 1;

  //前一个左边
  private int mLastX = 0;
  private int mLastY = 0;

  //点击屏幕时记录坐标
  private int mDownX = 0;
  private int mDownY = 0;

  //每隔10像素处理一次，避免判断次数过多
  private final double MIN_DISTANCE = 10.0;

  private int mFlags;

  //每行最多显示几张图片
  private int mMaxLineCount;

  //最多选中几张图片
  private int mMaxCheckedCount;

  @Override protected void onCreate(Bundle savedInstanceState) {
    //在子线程加载数据
    initData();

    super.onCreate(savedInstanceState);

    final long beginTime = System.currentTimeMillis();
    Log.d("brycegao", "SelectPhotoActivity onCreate begin");

    setContentView(R.layout.activity_selectphoto_main);
    findViewById(R.id.lyt_title_bar).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.btn_send).setOnClickListener(this);
    mTvViewDetail = findViewById(R.id.btn_preview);
    mTvViewDetail.setOnClickListener(this);

    mRvPhotos = findViewById(R.id.recylerview);
    mTvViewDetail = findViewById(R.id.tv_name);
    mTvCount = findViewById(R.id.tv_send_count);

    mFlags = getIntent().getIntExtra(Constants.KEY_FLAGS, 0);
    mMaxCheckedCount = getIntent().getIntExtra(Constants.KEY_MAX_CHECKED_PHOTO_COUNT,
        Integer.MAX_VALUE);
    mMaxLineCount = getIntent().getIntExtra(Constants.KEY_PHOTO_COUNT_PER_LINE,
        Constants.DEFAULT_LINE_COUNT);

    //网格布局，默认是纵向； 横向有4个图片
    mGridmanager = new GridLayoutManager(this,
        mMaxLineCount, GridLayoutManager.VERTICAL, false);
    mRvPhotos.setLayoutManager(mGridmanager);
    mAdaper = new PhotoViewAdapter(this, new ArrayList<ImageItem>(), getScreenWidth(),
        mMaxLineCount);

    mRvPhotos.setAdapter(mAdaper);
    mRvPhotos.setCallBack(new ITouchEventListener() {
      @Override public boolean onMotionEvent(MotionEvent event) {
        return processTouchEvent(event);
      }
    });
    mAdaper.setClickListener(new IClickItem() {
      @Override public void clickItem(int position) {
        //如果选中记录总数超过最大值则跳出循环
        if (mAdaper.getSelectNums() > mMaxCheckedCount
            && !mAdaper.isItemChecked(position)) {
          return;
        }
        mAdaper.clickItem(position);
        mTvCount.setText(mAdaper.getSelectNums() + "");
      }
    });
    mAdaper.setDrawListener(new IDrawCallBack() {
      @Override public void onDrawComplete(long time) {
        long diff = time - beginTime;
        Log.d("brycegao", "渲染第一张图片用时：" + diff + "毫秒");
      }
    });

    Log.d("brycegao", "SelectPhotoActivity onCreate end");
  }

  private int getScreenWidth() {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    return metrics.widthPixels;
  }

  @Override public void onClick(View v) {
    if (v.getId() == R.id.btn_send) {
      ArrayList<ImageItem> list = mAdaper.getAllSelItems();

      if (ByPhoto.getByCallBack() != null) {
        ByPhoto.getByCallBack().onDataSelected(list);
        ByPhoto.release();  //释放回调，避免内存泄漏
      }

      Intent intent = new Intent();
      intent.putParcelableArrayListExtra(Constants.KEY_IMAGE_LIST, list);
      setResult(RESULT_OK, intent);
      finish();
    } else if (v.getId() == R.id.btn_preview) {
      Toast.makeText(this, "待实现", Toast.LENGTH_LONG).show();
    }
  }

  //申请权限并启动子线程加载数据
  private void initData() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED
          || checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        //请求权限
        requestPermissions(new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION);
      } else {
        mTask = new LoadDataTask(this, this);
        mTask.execute();
      }
    } else {
      mTask = new LoadDataTask(this, this);
      mTask.execute();
    }

  }

  private boolean processTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    //记录点击屏幕时的初始坐标
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      mDownX = x;
      mDownY = y;
    }

    //抬起手指时重置
    if (event.getAction() == MotionEvent.ACTION_UP
        || event.getAction() == MotionEvent.ACTION_CANCEL) {
      mDownY = 0;
      mDownX = 0;
      mLastX = 0;
      mLastY = 0;
    }

    if (event.getAction() == MotionEvent.ACTION_MOVE) {
      double distance = Math.sqrt(Math.abs(x - mLastX) * Math.abs(x - mLastX)
          + Math.abs(y - mLastY) * Math.abs(y - mLastY));
      //如果是横向滑动且滑动距离超过阈值，则判断经过的item并勾选
      if (distance > MIN_DISTANCE && Math.abs(x - mDownX) > Math.abs(y - mDownY)) {
        mLastY = y;
        mLastX = x;

        doCheckSingleFinger(x, y);
      }
    }
    return false;
  }

  @Override public void onRequestPermissionsResult(int requestCode,
       String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (grantResults != null && grantResults.length > 0 && permissions.length == grantResults.length) {
      boolean isAllGranted = false;
      for (int i = 0; i < grantResults.length; i++) {
        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
          isAllGranted = false;
          break;
        }
      }

      if (isAllGranted) {
        mTask = new LoadDataTask(this, this);
        mTask.execute();
      } else {
        Toast.makeText(this, "缺少存储权限", Toast.LENGTH_LONG).show();
        finish();
      }
    }
  }

  @Override protected void onDestroy() {
    if (mTask != null) {
      mTask.cancel(true);
    }
    super.onDestroy();
  }

  @Override public void segDataLoaded(ImageItem[] items) {
    //加载完成部分数据
    if (items == null || items.length == 0) {
      return;
    }

    mAdaper.addAll(Arrays.asList(items));
  }

  @Override public void completeDataLoaded(List<ImageItem> list) {
    if (list != null && list.size() > 0) {
      mAdaper.refreshDataList(list);
    }
  }

  /**
   * 当前手指滑动的坐标
   * @param x
   * @param y
   */
  private void doCheckSingleFinger(int x, int y) {
     //判断(x,y)到(mDownX, mDownY)斜线对应矩形区域内， 所有图片都要被选中
    Log.d("brycegao", "doCheckSingleFinger x = " + x + ", y = " + y);

    int firstPos = mGridmanager.findFirstVisibleItemPosition();
    int lastPos = mGridmanager.findLastVisibleItemPosition();

    for (int i = firstPos; i <= lastPos; i++) {
      RecyclerView.ViewHolder holder = mRvPhotos.findViewHolderForAdapterPosition(i);

      //如果选中记录总数超过最大值则跳出循环, 而且焦点在当前item范围内
      if (holder.itemView.getLeft() < x && holder.itemView.getRight() > x
         && holder.itemView.getTop() < y && holder.itemView.getBottom() > y
         && mAdaper.getSelectNums() < mMaxCheckedCount) {
        //(x,y)在 （left，top)和(right,bottom)之间
        Log.d("brycegao", "找到匹配的ViewHolder：" + i);
        //mAdaper.slideOverItem(i);
        mAdaper.refreshDataSlideOnly(i);
        boolean isCheck = mAdaper.isItemChecked(i);
        ((PhotoViewHolder)holder).updateCheckStatus(isCheck);

        mTvCount.setText(String.valueOf(mAdaper.getSelectNums()));
      }
    }
    Log.d("brycegao", "当前显示范围：" + firstPos + "-" + lastPos);
  }
}
