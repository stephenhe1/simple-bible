package com.andrewchelladurai.simplebible.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.andrewchelladurai.simplebible.R;
import com.andrewchelladurai.simplebible.model.BookmarkViewModel;

public class BookmarkScreen
    extends Fragment {

  private static final String TAG = "BookmarkScreen";

  private BookmarkViewModel model;

  public static BookmarkScreen newInstance() {
    return new BookmarkScreen();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView:");
    return inflater.inflate(R.layout.bookmark_detail_screen, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    model = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookmarkViewModel.class);
  }

}
