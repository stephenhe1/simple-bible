package com.seal.simplebible.ui.ops;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.seal.simplebible.model.Verse;

public interface BookmarkScreenOps {

  @Nullable
  Verse getVerseAtPosition(@IntRange(from = 0) int position);

  @IntRange(from = 0)
  int getCachedVerseListSize();

}
