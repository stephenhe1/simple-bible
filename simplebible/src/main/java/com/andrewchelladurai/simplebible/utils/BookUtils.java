package com.andrewchelladurai.simplebible.utils;

import android.os.AsyncTask;

import com.andrewchelladurai.simplebible.data.dao.BookDao;
import com.andrewchelladurai.simplebible.data.entity.EntityBook;

public class BookUtils {

  public static final int EXPECTED_COUNT = 66;
  public static final String SETUP_FILE = "init_data_books.txt";
  public static final String SETUP_FILE_RECORD_SEPARATOR = "~";
  public static final int SETUP_FILE_RECORD_SEPARATOR_COUNT = 5;

  private static final String TAG = "BookUtils";
  private static final BookUtils THIS_INSTANCE = new BookUtils();

  private BookUtils() {
  }

  public static BookUtils getInstance() {
    return THIS_INSTANCE;
  }

  public void createBook(final BookDao dao, final EntityBook book) {
    new CreateBookTask(dao).execute(book);
  }

  private static class CreateBookTask
      extends AsyncTask<EntityBook, Void, Void> {

    private final BookDao dao;

    CreateBookTask(final BookDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(final EntityBook... books) {
      for (final EntityBook book : books) {
        dao.createBook(book);
      }
      return null;
    }

  }

}
