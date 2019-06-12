package com.brycegao.libchoose.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class ImageItem implements Serializable, Parcelable {
  private static final long serialVersionUID = 7640195868955275538L;

  public String imageId;
  public String imagePath;
  public String imageName;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.imageId);
    dest.writeString(this.imagePath);
    dest.writeString(this.imageName);
  }

  public ImageItem() {
  }

  protected ImageItem(Parcel in) {
    this.imageId = in.readString();
    this.imagePath = in.readString();
    this.imageName = in.readString();
  }

  public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
    @Override public ImageItem createFromParcel(Parcel source) {
      return new ImageItem(source);
    }

    @Override public ImageItem[] newArray(int size) {
      return new ImageItem[size];
    }
  };
}
