package com.brycegao.libchoose.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.brycegao.libchoose.BuildConfig;
import com.brycegao.libchoose.ByPhoto;
import com.brycegao.libchoose.Constants;
import com.brycegao.libchoose.R;
import com.brycegao.libchoose.adapter.PhotoViewAdapter;
import com.brycegao.libchoose.engine.LoadDataTask;
import com.brycegao.libchoose.inter.ILoadData;
import com.brycegao.libchoose.inter.ITouchEventListener;
import com.brycegao.libchoose.model.ImageItem;
import com.brycegao.libchoose.widget.MyLinearLayout;
import com.brycegao.libchoose.widget.MyRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectPhotoActivity extends Activity implements ILoadData {
  private MyRecyclerView mRvPhotos;
  private PhotoViewAdapter mAdaper;
  private GridLayoutManager mGridmanager;

  //查看大图
  private TextView mTvViewDetail;

  //选中个数
  private TextView mTvCount;

  //默认每行显示4个图片
  private final int DEFAULT_LINE_COUNT = 3;

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
  private MyLinearLayout mContainer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_selectphoto_main);
    mContainer = findViewById(R.id.ll_container);
    findViewById(R.id.lyt_title_bar).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    mRvPhotos = findViewById(R.id.recylerview);
    mTvViewDetail = findViewById(R.id.tv_name);
    mTvCount = findViewById(R.id.tv_send_count);

    mFlags = getIntent().getIntExtra(Constants.KEY_FLAGS, 0);

    //网格布局，默认是纵向； 横向有4个图片
    mGridmanager = new GridLayoutManager(this,
        DEFAULT_LINE_COUNT, GridLayoutManager.VERTICAL, false);
    mRvPhotos.setLayoutManager(mGridmanager);
    mAdaper = new PhotoViewAdapter(this, new ArrayList<ImageItem>(),
        getWindow().getDecorView().getWidth());

    mRvPhotos.setAdapter(mAdaper);

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

    mRvPhotos.setCallBack(new ITouchEventListener() {
      @Override public boolean onMotionEvent(MotionEvent event) {
        return processTouchEvent(event);
      }
    });
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

    double distance = Math.sqrt(Math.abs(x-mLastX)*Math.abs(x-mLastX)
        + Math.abs(y-mLastY)*Math.abs(y-mLastY));
    if (distance > MIN_DISTANCE) {
      mLastY = y;
      mLastX = x;

      //判断 todo
      //if (ByPhoto.isOneFingerSlideEnabled(mFlags)) {
        doCheckSingleFinger(x, y);
      //}
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
      if (holder.itemView.getLeft() < x && holder.itemView.getRight() > x
         && holder.itemView.getTop() < y && holder.itemView.getBottom() > y) {
        //(x,y)在 （left，top)和(right,bottom)之间
        Log.d("brycegao", "找到匹配的ViewHolder：" + i);
        mAdaper.slideOverItem(i);
      }
    }
    Log.d("brycegao", "当前显示范围：" + firstPos + "-" + lastPos);
  }
}
