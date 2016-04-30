/*
 * This file 'DatabaseUtility.java' is part of SimpleBible :
 *
 * Copyright (c) 2016.
 *
 * This Application is available at below locations
 * Binary : https://play.google.com/store/apps/developer?id=Andrew+Chelladurai
 * Source : https://github.com/andrewchelladurai/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * OR <http://www.gnu.org/licenses/gpl-3.0.txt>
 */

package com.andrewchelladurai.simplebible;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Andrew Chelladurai - TheUnknownAndrew[at]GMail[dot]com on 26-Feb-2016 @ 1:15 AM
 */
public class DatabaseUtility
        extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseUtility";

    private static final String DATABASE_NAME = "Bible_NIV.db";
    private static DatabaseUtility staticInstance = null;
    private static String DB_PATH;
    private static SQLiteDatabase database;
    private static Context context;

    private final String BIBLE_TABLE = "BIBLE_VERSES";
    private final static String COLUMNS[] = {"VERSE_NUMBER", "CHAPTER_NUMBER",
                                             "BOOK_NUMBER", "VERSE_TEXT"};
    private final static String BOOK_NUMBER = "BOOK_NUMBER";
    private final static String CHAPTER_NUMBER = "CHAPTER_NUMBER";
    private final static String VERSE_NUMBER = "VERSE_NUMBER";
    private final static String VERSE_TEXT = "VERSE_TEXT";

    private DatabaseUtility(Context context) {
        super(context, DATABASE_NAME, null, 1);
        DatabaseUtility.context = context;
        //Write a full path to the databases of your application
        DB_PATH = context.getDatabasePath(DATABASE_NAME).getParent();
        Log.d(TAG, "DatabaseUtility() called DB_PATH = [" + DB_PATH + "]");
        openDataBase();
    }

    private void openDataBase()
            throws SQLException {
        Log.d(TAG, "openDataBase: Entered");
        if (database == null) {
            createDataBase();
            String path = DB_PATH + File.separatorChar + DATABASE_NAME;
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
    }

    private void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            Log.d(TAG, "createDataBase: DB Does not Exist");
            getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.d(TAG, "createDataBase: Exception Copying Bible.db");
                throw new Error("Error copying Bible.db!");
            }
        } else {
            Log.d(TAG, "createDataBase: Database already exists");
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        String path = DB_PATH + File.separatorChar + DATABASE_NAME;
        Log.d(TAG, "checkDataBase: at path" + path);
        File f = new File(path);
        if (f.exists()) {
            try {
                checkDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLException sqle) {
                Log.d(TAG, "checkDataBase: " + sqle.getLocalizedMessage());
                sqle.printStackTrace();
            } finally {
                //Android does not like resource leaks, everything should be closed
                if (checkDb != null) {
                    checkDb.close();
                }
            }
            Log.d(TAG, "checkDataBase: Exited returning checkDb != null");
            return checkDb != null;
        } else {
            return false;
        }
    }

    private void copyDataBase()
            throws IOException {
        Log.d(TAG, "copyDataBase: Called");

        InputStream assetDatabase = context.getAssets().open(DATABASE_NAME);
        Log.i(TAG, "copyDataBase : externalDBStream" + assetDatabase.toString());
        String outFileName = DB_PATH + File.separatorChar + DATABASE_NAME;
        Log.i(TAG, "copyDataBase : outFileName = " + outFileName);

        OutputStream localDatabase = new FileOutputStream(outFileName);

        //Copying the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = assetDatabase.read(buffer)) > 0) {
            localDatabase.write(buffer, 0, bytesRead);
        }
        localDatabase.close();
        assetDatabase.close();
        Log.d(TAG, "copyDataBase: Finished");
    }

    public static DatabaseUtility getInstance(Context context)
            throws NullPointerException {
        if (staticInstance == null) {
            if (context == null) {
                throw new NullPointerException("NULL Context passed for instantiating DB");
            }
            staticInstance = new DatabaseUtility(context);
        }
        return staticInstance;
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public ArrayList<String> getAllVersesOfChapter(
            final int pBookNumber, final int pChapterNumber) {
        Log.d(TAG, "getAllVersesOfChapter() called BookNumber = [" + pBookNumber +
                   "], ChapterNumber = [" + pChapterNumber + "]");

        final SQLiteDatabase db = getReadableDatabase();

        String[] selectCols = COLUMNS;
        String whereCondition = BOOK_NUMBER + " = ? AND " +
                                CHAPTER_NUMBER + " = ?";
        String[] conditionParams = {pBookNumber + "", pChapterNumber + ""};

        Cursor cursor = db.query(BIBLE_TABLE, selectCols, whereCondition,
                                 conditionParams, null, null, VERSE_NUMBER, null);

        ArrayList<String> list = new ArrayList<>(0);

        if (null != cursor && cursor.moveToFirst()) {
            int verseIndex = cursor.getColumnIndex(VERSE_TEXT);
            int verseIdIndex = cursor.getColumnIndex(VERSE_NUMBER);
            //            int chapterIdIndex = cursor.getColumnIndex("ChapterId");
            //            int bookIdIndex = cursor.getColumnIndex("BookId");
            do {
                list.add(cursor.getInt(verseIdIndex) + " - " + cursor.getString(verseIndex));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public ArrayList<String> findText(String pInput) {
        Log.d(TAG, "findText() called  pInput = [" + pInput + "]");
        ArrayList<String> values = new ArrayList<>(0);
        final SQLiteDatabase db = getReadableDatabase();
        String[] selectCols = COLUMNS;
        String whereCondition = VERSE_TEXT + " like ?";
        String[] conditionParams = {"%" + pInput + "%"};

        Cursor cursor = db.query(BIBLE_TABLE, selectCols, whereCondition, conditionParams,
                                 null, null, BOOK_NUMBER);

        if (cursor != null && cursor.moveToFirst()) {
            int verseTextIndex = cursor.getColumnIndex(VERSE_TEXT);
            int verseNumberIndex = cursor.getColumnIndex(VERSE_NUMBER);
            int chapterIndex = cursor.getColumnIndex(CHAPTER_NUMBER);
            int bookIndex = cursor.getColumnIndex(BOOK_NUMBER);
            int bookValue, chapterValue, verseValue;
            StringBuilder entry = new StringBuilder();
            do {
                bookValue = cursor.getInt(bookIndex);
                chapterValue = cursor.getInt(chapterIndex);
                verseValue = cursor.getInt(verseNumberIndex);
                entry.append(Book.getBookDetails(bookValue).getName())
                        .append(" (")
                        .append(chapterValue)
                        .append(":")
                        .append(verseValue)
                        .append(") ")
                        .append(cursor.getString(verseTextIndex));
                values.add(entry.toString());
                entry.delete(0, entry.length());
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "findText() returned: " + values.size());
        return values;
    }
}
