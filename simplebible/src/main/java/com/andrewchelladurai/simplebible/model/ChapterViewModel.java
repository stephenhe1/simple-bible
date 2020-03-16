package com.andrewchelladurai.simplebible.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ChapterViewModel
    extends AndroidViewModel {

  private static final String TAG = "ChapterViewModel";

  public ChapterViewModel(@NonNull final Application application) {
    super(application);
    Log.d(TAG, "ChapterViewModel:");
  }

}
