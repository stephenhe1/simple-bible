package com.andrewchelladurai.simplebible.data.dao;

import com.andrewchelladurai.simplebible.data.entities.Bookmark;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by : Andrew Chelladurai
 * Email : TheUnknownAndrew[at]GMail[dot]com
 * on : 04-Aug-2018 @ 9:06 PM.
 */

@Dao
public interface BookmarkDao {

    @Query("select distinct count(REFERENCE) from BOOKMARKS")
    int getRecordCount();

    @Query("select * from BOOKMARKS order by REFERENCE")
    List<Bookmark> getAllBookmarks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createNewBookmark(@SuppressWarnings("NullableProblems") @NonNull Bookmark bookmark);

}