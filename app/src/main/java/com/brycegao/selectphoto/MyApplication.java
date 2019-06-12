package com.brycegao.selectphoto;

import android.app.Application;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

public class MyApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    Glide.init(this, new GlideBuilder());
  }
}
