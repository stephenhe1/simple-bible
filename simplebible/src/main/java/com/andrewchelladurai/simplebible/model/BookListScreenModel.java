package com.andrewchelladurai.simplebible.model;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.andrewchelladurai.simplebible.data.SbDatabase;
import com.andrewchelladurai.simplebible.data.dao.BookDao;
import com.andrewchelladurai.simplebible.data.entities.Book;
import com.andrewchelladurai.simplebible.utils.BookUtils;

import java.util.List;

public class BookListScreenModel
    extends AndroidViewModel {

  private static final String TAG = "BookListScreenModel";

  private final BookDao bookDao;
  private ArrayAdapter<String> bookNameAdapter;

  public BookListScreenModel(@NonNull final Application application) {
    super(application);
    bookDao = SbDatabase.getDatabase(application).getBookDao();
  }

  public LiveData<List<Book>> getAllBooks() {
    return bookDao.getAllRecordsLive();
  }

  @IntRange(from = 0, to = BookUtils.EXPECTED_COUNT)
  public int getBookNumber(@NonNull final String bookName, @NonNull final List<?> list) {
    Book book;
    for (final Object o : list) {
      book = (Book) o;
      if (book.getName().equalsIgnoreCase(bookName)) {
        return book.getNumber();
      }
    }
    return 0;
  }

  public ArrayAdapter<String> getBookNameAdapter() {
    return bookNameAdapter;
  }

  public void setBookNameAdapter(@NonNull final ArrayAdapter<String> adapter) {
    bookNameAdapter = adapter;
    Log.d(TAG, "setBookNameAdapter: cached [" + bookNameAdapter.getCount() + "] records");
  }

}
