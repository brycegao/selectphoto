package com.brycegao.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.brycegao.libchoose.ByPhoto;
import com.brycegao.libchoose.Constants;
import com.brycegao.libchoose.IByPhotoData;
import com.brycegao.libchoose.activity.SelectPhotoActivity;
import com.brycegao.libchoose.adapter.PhotoViewAdapter;
import com.brycegao.libchoose.model.ImageItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
  private RecyclerView mRecyclerView;
  private PhotoViewAdapter mAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    ByPhoto.preloadData(this);

    findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //Intent intent = new Intent(MainActivity.this, SelectPhotoActivity.class);
        //startActivity(intent);

        ByPhoto.with(MainActivity.this)
            .setMaxCheckPhoto(10)
            .setNumPerRow(3)
            .setListener(new IByPhotoData() {
              @Override public void onDataSelected(List<ImageItem> list) {
                Log.d("brycegao", "回调数据" + list);
                mAdapter.replaceWith(list);
              }
            })
            .begin();
      }
    });
    initViews();
  }

  private void initViews() {
    mRecyclerView = findViewById(R.id.recylerview);
    GridLayoutManager layoutManager = new GridLayoutManager(this,
        4, GridLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(layoutManager);

    //每行显示4个
    mAdapter = new PhotoViewAdapter(this, new ArrayList<ImageItem>(),
        Constants.getScreenWidth(this),
        4);
    mRecyclerView.setAdapter(mAdapter);
  }
}
