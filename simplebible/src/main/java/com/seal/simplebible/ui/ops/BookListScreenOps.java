package com.seal.simplebible.ui.ops;

import androidx.annotation.Nullable;

import com.seal.simplebible.model.Book;

public interface BookListScreenOps {

  void handleBookSelection(@Nullable Book book);

}
