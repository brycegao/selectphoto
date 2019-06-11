package com.brycegao.libchoose.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.brycegao.libchoose.model.ImageItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PhotoViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  //数据源
  private List<ImageItem> mListData;

  private List<ImageItem> mSelItems = new ArrayList<>();

  private Context mContext;

  //recyclerview占用的屏幕宽度
  private int mScreenWidth;

  public PhotoViewAdapter(Context context, List<ImageItem> items, int width) {
    mContext = context;
    mListData = items;
    mScreenWidth = width;
  }

  /**
   * 按照下标拿到数据
   *
   * @return 对应数据
   */
  public ImageItem getObject(int position) {
    if (mListData == null) {
      return null;
    }

    return mListData.get(position);
  }

  /**
   * 获取适配器器持有的整个数据源集合
   */
  public List<ImageItem> getListData() {
    return mListData;
  }

  /**
   * 强制刷新全部数据
   */
  public void refreshDataList(List<ImageItem> items) {
    if (items == null) {
      return;
    }

    mListData = items;
    notifyDataSetChanged();
  }

  /**
   * 在指定位置添加一条记录
   *
   * @param position, 在指定位置插入记录
   * @param item， 记录
   * @return 添加的位置
   */
  public synchronized void add(int position, ImageItem item) {
    if (mListData == null) {
      mListData = new ArrayList<>();
    }

    mListData.add(position, item);
    notifyItemRangeInserted(position, mListData.size() - position);
  }

  /**
   * 在末尾添加一条记录并刷新列表
   */
  public synchronized boolean add(ImageItem object) {
    int lastIndex = mListData.size();

    if (mListData.add(object)) {
      notifyItemInserted(lastIndex);
      return true;
    } else {
      return false;
    }
  }

  /**
   * 在指定位置添加多个记录
   */
  public synchronized boolean addAll(int location, Collection<ImageItem> collection) {
    if (mListData.addAll(location, collection)) {
      notifyItemRangeInserted(location, collection.size());
      return true;
    } else {
      return false;
    }
  }

  /**
   * 在尾部追加数据
   */
  public synchronized boolean addAll(Collection<ImageItem> collection) {
    int lastIndex = mListData.size();
    if (mListData.addAll(collection)) {
      notifyItemRangeInserted(lastIndex, collection.size());
      return true;
    } else {
      return false;
    }
  }

  /**
   * 清空列表
   */
  public synchronized void clearAll() {
    mListData.clear();
    notifyDataSetChanged();
  }

  /**
   * 删除一条数据并刷新列表
   */
  public synchronized boolean remove(ImageItem model) {
    int index = mListData.indexOf(model);
    if (mListData.remove(model)) {
      notifyItemRemoved(index);
      return true;
    } else {
      return false;
    }
  }

  /**
   * 删除若干条数据
   *
   * @param collection, 要删除的model
   * @return true删除成功并刷新列表， false没删除
   */
  public synchronized boolean removeAll(@NonNull Collection<ImageItem> collection) {
    boolean modified = false;
    Iterator<ImageItem> iterator = mListData.iterator();
    while (iterator.hasNext()) {
      ImageItem item = iterator.next();
      if (collection.contains(item)) {
        int index = mListData.indexOf(item);
        iterator.remove();
        notifyItemRemoved(index);

        modified = true;
      }
    }
    return modified;
  }

  /**
   * 删除现有列表中collection没有的记录
   *
   * @param collection， 新数据
   * @return 删除了无效数据
   */
  public synchronized boolean retainAll(@NonNull Collection<?> collection) {
    boolean modified = false;

    Iterator<ImageItem> iterator = mListData.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (!collection.contains(object)) {
        int index = mListData.indexOf(object);
        iterator.remove();
        notifyItemRemoved(index);

        modified = true;
      }
    }

    return modified;
  }

  /**
   * 更新一条记录
   *
   * @param object， 新model
   * @return 旧数据
   */
  public synchronized ImageItem update(int location, ImageItem object) {
    ImageItem origin = mListData.set(location, object);
    notifyItemChanged(location);

    return origin;
  }

  /**
   * 使用新数据刷新列表， 增删改
   */
  public void replaceWith(List<ImageItem> data) {
    if (data.isEmpty() && mListData.isEmpty()) {
      return;
    }

    //现在列表是空的， 直接添加
    if (mListData.isEmpty()) {
      addAll(data);
      return;
    }

    //如果新数据是空，则删除所有item即可
    if (data.isEmpty()) {
      clearAll();
    }

    //原列表有、新列表没有则要删除
    retainAll(data);

    //如果原列表是空的，则直接添加
    if (mListData.isEmpty()) {
      addAll(data);
      return;
    }

    // 然后遍历新列表，对旧列表的数据更新、移动、增加
    for (int indexNew = 0; indexNew < data.size(); indexNew++) {
      ImageItem item = data.get(indexNew);

      int indexOld = mListData.indexOf(item);

      if (indexOld == -1) {
        add(indexNew, item);
      } else if (indexOld == indexNew) {
        update(indexNew, item);
      } else {
        mListData.remove(indexOld);
        mListData.add(indexNew, item);
        notifyItemMoved(indexOld, indexNew);
      }
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //只有一种类型的卡片
    return PhotoViewHolder.getInstance(mContext);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ImageItem item = mListData.get(position);
    boolean isSel = mSelItems.contains(item);

    ((PhotoViewHolder)holder).updateData(item, isSel);
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }

  @Override
  public int getItemCount() {
    if (mListData == null) {
      return 0;
    }
    return mListData.size();
  }
}

