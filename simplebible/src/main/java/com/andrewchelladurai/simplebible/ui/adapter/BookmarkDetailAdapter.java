package com.andrewchelladurai.simplebible.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrewchelladurai.simplebible.R;
import com.andrewchelladurai.simplebible.data.entity.EntityVerse;
import com.andrewchelladurai.simplebible.ui.ops.RecyclerViewAdapterOps;
import com.andrewchelladurai.simplebible.ui.ops.ScreenBookmarkDetailOps;

import java.util.ArrayList;
import java.util.List;

public class BookmarkDetailAdapter
    extends RecyclerView.Adapter
    implements RecyclerViewAdapterOps {

  private static final String TAG = "BookmarkDetailAdapter";
  private final ArrayList<EntityVerse> list = new ArrayList<>();
  private final ScreenBookmarkDetailOps ops;

  public BookmarkDetailAdapter(final ScreenBookmarkDetailOps ops) {
    this.ops = ops;
  }

  @Override
  public void updateList(@NonNull final List<?> list) {
    clearList();
    for (final Object o : list) {
      this.list.add((EntityVerse) o);
    }
    Log.d(TAG, "updateList: filled [" + getItemCount() + "] records");
  }

  @Override
  public void filterList(@NonNull final String searchTerm) {
    // TODO: 11/8/19
  }

  @Override
  public void clearList() {
    list.clear();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                    final int viewType) {
    return new BookmarkVerse(LayoutInflater.from(parent.getContext())
                                           .inflate(R.layout.screen_bookmark_detail_list_item,
                                                    parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
    ((BookmarkVerse) holder).updateView(list.get(position), position);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  private class BookmarkVerse
      extends RecyclerView.ViewHolder
      implements ItemHolderOps {

    private final View rootView;
    private EntityVerse verse;
    private int position;

    BookmarkVerse(final View rootView) {
      super(rootView);
      this.rootView = rootView;
    }

    @Override
    public void updateView(final Object object, final int position) {
      verse = (EntityVerse) object;
      this.position = position;

      ops.updateBookmarkVerseView(verse, rootView.findViewById(
          R.id.screen_bookmark_detail_list_item_content));
    }

  }

}
