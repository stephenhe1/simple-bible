package com.andrewchelladurai.simplebible.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andrewchelladurai.simplebible.R;
import com.andrewchelladurai.simplebible.data.EntityBook;
import com.andrewchelladurai.simplebible.data.EntityVerse;
import com.andrewchelladurai.simplebible.ui.ops.SearchScreenOps;
import com.andrewchelladurai.simplebible.utils.Utils;

public class SearchResultAdapter
    extends RecyclerView.Adapter {

  private static final String TAG = "SearchResultAdapter";

  @NonNull
  private final String template;

  @NonNull
  private final SearchScreenOps ops;

  public SearchResultAdapter(@NonNull final SearchScreenOps ops,
                             @NonNull final String template) {
    this.ops = ops;
    this.template = template;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                    final int viewType) {
    return new SearchResultView(
        LayoutInflater.from(parent.getContext())
                      .inflate(R.layout.item_search_result, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
    ((SearchResultView) holder).updateContent(ops.getVerseAtPosition(position));
  }

  @Override
  public int getItemCount() {
    return ops.getResultCount();
  }

  private class SearchResultView
      extends RecyclerView.ViewHolder {

    private final TextView textView;

    private final View selectedView;

    private EntityVerse verse;

    SearchResultView(final View view) {
      super(view);
      textView = view.findViewById(R.id.item_search_result_text);
      selectedView = view.findViewById(R.id.item_search_result_selected);

      textView.setOnClickListener(v -> {
        if (ops.isSelected(verse.getReference())) {
          ops.removeSelection(verse.getReference());
          selectedView.setVisibility(View.GONE);
        } else {
          ops.addSelection(verse.getReference(), verse);
          selectedView.setVisibility(View.VISIBLE);
        }
        ops.updateSelectionActionsState();
      });

    }

    private void updateContent(@NonNull final EntityVerse entityVerse) {
      verse = entityVerse;

      final EntityBook book = Utils.getInstance().getCachedBook(verse.getBook());
      if (book == null) {
        Log.e(TAG, "updateContent: null book returned");
        return;
      }

      selectedView.setVisibility(
          ops.isSelected(verse.getReference()) ? View.VISIBLE : View.GONE);

      textView.setText(HtmlCompat.fromHtml(String.format(
          template, book.getName(), verse.getChapter(), verse.getVerse(), verse.getText()),
                                           HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

  }

}