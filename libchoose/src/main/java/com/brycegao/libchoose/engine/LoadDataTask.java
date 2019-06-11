package com.brycegao.libchoose.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.brycegao.libchoose.Constants;
import com.brycegao.libchoose.inter.ILoadData;
import com.brycegao.libchoose.model.ImageItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadDataTask extends AsyncTask<String, ImageItem, List<ImageItem>> {
  private Context mContext;

  //加载数据的回调
  private ILoadData mListener;

  public LoadDataTask(Context context, ILoadData callback) {
    super();
    mContext = context;
    mListener = callback;
  }

  private static final String[] ACCEPTABLE_IMAGE_TYPES = new String[] {
      "image/jpeg", "image/png", "image/bmp"
  };

  private static final String WHERE_CLAUSE =
      "(" + MediaStore.Images.Media.MIME_TYPE + " in (?, ?, ?,?))";

  @Override protected List<ImageItem> doInBackground(String... strings) {
    List<ImageItem> itemList = new ArrayList<>();

    int i = 0;
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver()
          .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, WHERE_CLAUSE,
              ACCEPTABLE_IMAGE_TYPES, MediaStore.Images.Media.DATE_MODIFIED);

      if (cursor == null ) {
        return itemList;
      }

      while (cursor.moveToNext()) {
        ImageItem imageItem = new ImageItem();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        /* 排除掉路径存在于系统数据库，但是图片不存在的情况 */
        if (path != null && new File(path).exists()) {
          /* 获取图片ID */
          String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
          String imageName =
              cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
          imageItem.imageId = imageId;
          imageItem.imageName = imageName;
          imageItem.imagePath = path;
          itemList.add(imageItem);

          i++;

          //每隔10个图片报一次
          if (i % Constants.PHOTO_COUNT_PER_TIME == 0 && i > 0) {
            ImageItem[] segData = new ImageItem[Constants.PHOTO_COUNT_PER_TIME];
            for (int k = 0; k < Constants.PHOTO_COUNT_PER_TIME; k++) {
              segData[k] = itemList.get(i - Constants.PHOTO_COUNT_PER_TIME +   k);
            }
            publishProgress(segData);
          }
        }
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      if (cursor != null) {
        cursor.close();
        cursor = null;
      }
    }
    return null;
  }

  @Override protected void onProgressUpdate(ImageItem... values) {
    super.onProgressUpdate(values);
    mListener.segDataLoaded(values);
  }

  @Override protected void onPostExecute(List<ImageItem> list) {
    super.onPostExecute(list);
    mListener.completeDataLoaded(list);
  }
}
