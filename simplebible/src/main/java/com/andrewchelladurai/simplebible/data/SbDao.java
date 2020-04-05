package com.andrewchelladurai.simplebible.data;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andrewchelladurai.simplebible.utils.Utils;

import java.util.List;

@Dao
public interface SbDao {

  @Query("select sum(record_count) from ("
         + " select count(number) as record_count from sb_books "
         + " where lower(name) = lower(:bookNameFirst) and number = 1 "
         + " union all "
         + " select count(number) as record_count from sb_books "
         + " where lower(name) = lower(:bookNameLast) and number = 66 "
         + " union all "
         + " select count(distinct number) as record_count from sb_books "
         + " where number = :lastBookNumber "
         + " union all "
         + " select count(distinct book||'~'||chapter||'~'||verse) as record_count from sb_verses "
         + " where book||'~'||chapter||'~'||verse = :lastVerseReference"
         + " );")
  LiveData<Integer> validateTableData(@NonNull final String bookNameFirst,
                                      @NonNull final String bookNameLast,
                                      @NonNull final String lastBookNumber,
                                      @NonNull final String lastVerseReference);

  @Insert(entity = EntityBook.class, onConflict = OnConflictStrategy.REPLACE)
  void createBook(@NonNull EntityBook entityBook);

  @Insert(entity = EntityVerse.class, onConflict = OnConflictStrategy.REPLACE)
  void createVerse(@NonNull EntityVerse entityVerse);

  @Insert(entity = EntityBookmark.class, onConflict = OnConflictStrategy.REPLACE)
  void createBookmark(@NonNull EntityBookmark entityBookmark);

  @Query("select * from sb_books order by number")
  LiveData<List<EntityBook>> getAllBookRecords();

  @Query("select * from sb_verses "
         + "where book=:bookNum and chapter=:chapterNum and verse=:verseNumb")
  LiveData<EntityVerse> getVerse(@IntRange(from = 1, to = Utils.MAX_BOOKS) final int bookNum,
                                 @IntRange(from = 1) final int chapterNum,
                                 @IntRange(from = 1) final int verseNumb);

  @Query("select * from sb_verses "
         + "where book=:bookNum and chapter=:chapterNum order by verse asc")
  LiveData<List<EntityVerse>> getChapter(
      @IntRange(from = 1, to = Utils.MAX_BOOKS) final int bookNum,
      @IntRange(from = 1) final int chapterNum);

}
