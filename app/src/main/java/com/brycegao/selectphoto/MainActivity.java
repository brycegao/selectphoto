package com.brycegao.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.brycegao.libchoose.ByPhoto;
import com.brycegao.libchoose.activity.SelectPhotoActivity;

public class MainActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    ByPhoto.preloadData(this);

    findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SelectPhotoActivity.class);
        startActivity(intent);
      }
    });
  }
}
