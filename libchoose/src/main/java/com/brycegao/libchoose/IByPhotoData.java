package com.brycegao.libchoose;

import com.brycegao.libchoose.model.ImageItem;
import java.util.List;

public interface IByPhotoData {
  /**
   * 选择数据的回调
   * @param list
   */
  void onDataSelected(List<ImageItem> list);
}
