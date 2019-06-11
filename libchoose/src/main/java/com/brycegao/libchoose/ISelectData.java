package com.brycegao.libchoose;

import android.net.Uri;
import java.util.List;

public interface ISelectData {
  /**
   * 选中的图片数据
   * @param list
   */
  public void selectItem(List<Uri> list);
}
