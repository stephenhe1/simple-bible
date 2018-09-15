package com.andrewchelladurai.simplebible.data;

import android.content.Context;
import android.util.Log;

import com.andrewchelladurai.simplebible.data.entities.Book;
import com.andrewchelladurai.simplebible.data.entities.Bookmark;
import com.andrewchelladurai.simplebible.data.entities.Verse;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by : Andrew Chelladurai
 * Email : TheUnknownAndrew[at]GMail[dot]com
 * on : 04-Aug-2018 @ 9:26 PM.
 */

@Database(entities = {Verse.class, Book.class, Bookmark.class},
          // epoch time in seconds : date +%s
          version = 1520969806,
          // March 13, 2018 7:36:46 PM
          exportSchema = false)

public abstract class SbDatabase
    extends RoomDatabase {

    private static final String TAG = "SbDatabase";
    private static final String DATABASE_NAME = "SimpleBible.db";
    private static SbDatabase thisInstance = null;

    public static SbDatabase getInstance(@NonNull final Context context) {
        synchronized (SbDatabase.class) {
            if (thisInstance == null) {
                thisInstance = Room.databaseBuilder(context.getApplicationContext(),
                                                    SbDatabase.class, DATABASE_NAME)
                                   .fallbackToDestructiveMigration().build();
                Log.d(TAG, "getInstance: instantiated database");
            }
        }
        return thisInstance;
    }

/*
    public abstract VerseDao getVerseDao();

    public abstract BookDao getBookDao();

    public abstract BookmarkDao getBookmarkDao();
*/

}
