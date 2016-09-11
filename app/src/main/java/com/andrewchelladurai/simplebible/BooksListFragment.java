package com.andrewchelladurai.simplebible;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrewchelladurai.simplebible.model.BooksList;
import com.andrewchelladurai.simplebible.adapter.BooksListAdapter;
import com.andrewchelladurai.simplebible.interaction.BooksListFragmentInterface;
import com.andrewchelladurai.simplebible.presentation.BooksListFragmentPresenter;

import java.util.List;

public class BooksListFragment
        extends Fragment
        implements BooksListFragmentInterface {

    private static final String TAG              = "SB_BLFragment";
    private static final String ARG_COLUMN_COUNT = "COLUMN_COUNT";
    private static BooksListFragmentPresenter mPresenter;
    private int mColumnCount = 2;

    public BooksListFragment() {
    }

    public static BooksListFragment newInstance(int columnCount) {
        BooksListFragment fragment = new BooksListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void init() {
        Log.d(TAG, "init() called");
        mPresenter = new BooksListFragmentPresenter(this);
        boolean value = mPresenter.init();
        Log.d(TAG, "init: " + value);
    }

    @Override
    public void refresh() {
        mPresenter.refresh();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            // Try to create the adapter using the list
            List<BooksList.BookItem> items = BooksList.getListItems();
            if (items.size() != 66) {
                Toast.makeText(getContext(), "BooksList could not be populated",
                               Toast.LENGTH_SHORT).show();
            } else {
                recyclerView.setAdapter(new BooksListAdapter(items, this));
            }

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }

    public void handleInteraction(BooksList.BookItem item) {
        Log.d(TAG, "handleInteraction() called for item [" + item.getBookName() + "]");
    }

    @Override
    public String getBookNameTemplateString() {
        String value = getString(R.string.book_item_name_template);
        if (value == null) {
            return "%s";
        }
        return value;
    }

    @Override
    public String chapterCountTemplateString() {
        String value = getString(R.string.book_item_chapter_count_template);
        if (value == null) {
            return "%d";
        }
        return value;
    }

    /**
     * This will return the resource array books_n_chapter_count_array The format of the items must
     * be like this : Book_Name:Chapter_Count Example Genesis:50
     *
     * @return String array
     */
    @Override
    public String[] getBookNameChapterCountArray() {
        String array[] = getResources().getStringArray(R.array.books_n_chapter_count_array);
        if (null != array && array.length > 0) {
            return array;
        }
        return new String[0];
    }
}