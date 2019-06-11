package com.brycegao.libchoose.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.brycegao.libchoose.BuildConfig;
import com.brycegao.libchoose.R;
import com.brycegao.libchoose.adapter.PhotoViewAdapter;
import com.brycegao.libchoose.engine.LoadDataTask;
import com.brycegao.libchoose.inter.ILoadData;
import com.brycegao.libchoose.model.ImageItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectPhotoActivity extends Activity implements ILoadData {
  private RecyclerView mRvPhotos;
  private PhotoViewAdapter mAdaper;

  //查看大图
  private TextView mTvViewDetail;

  //选中个数
  private TextView mTvCount;

  //默认每行显示4个图片
  private final int DEFAULT_LINE_COUNT = 3;

  private LoadDataTask mTask;

  private final int REQUEST_PERMISSION = 1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_selectphoto_main);
    findViewById(R.id.lyt_title_bar).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    mRvPhotos = findViewById(R.id.recylerview);
    mTvViewDetail = findViewById(R.id.tv_name);
    mTvCount = findViewById(R.id.tv_send_count);

    //网格布局，默认是纵向； 横向有4个图片
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
        DEFAULT_LINE_COUNT, GridLayoutManager.VERTICAL, false);
    mRvPhotos.setLayoutManager(gridLayoutManager);
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
}
