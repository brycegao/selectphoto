package com.brycegao.libchoose.inter;

import com.brycegao.libchoose.model.ImageItem;
import java.util.List;

public interface ILoadData {

  /**
   * 分段加载完成
   * @param items
   */
  public void segDataLoaded(ImageItem[] items);

  /**
   * 全部加载完成
   * @param list
   */
  public void completeDataLoaded(List<ImageItem> list);
}
