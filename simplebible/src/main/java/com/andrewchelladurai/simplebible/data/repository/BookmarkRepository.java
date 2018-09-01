package com.andrewchelladurai.simplebible.data.repository;

import android.app.Application;
import android.util.Log;

import com.andrewchelladurai.simplebible.data.SbDatabase;
import com.andrewchelladurai.simplebible.data.entities.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Created by : Andrew Chelladurai
 * Email : TheUnknownAndrew[at]GMail[dot]com
 * on : 15-Aug-2018 @ 6:57 PM.
 */
public class BookmarkRepository
    extends AndroidViewModel {

    private static final String TAG = "BookmarkRepository";
    private static BookmarkRepository THIS_INSTANCE;

    private final List<Bookmark>            mCacheList = new ArrayList<>();
    private final HashMap<String, Bookmark> mCacheMap  = new HashMap<>();
    private static LiveData<List<Bookmark>> mLiveData;

    public BookmarkRepository(final Application application) {
        super(application);
        mLiveData = SbDatabase.getInstance(getApplication()).getBookmarkDao().getAllRecords();
        THIS_INSTANCE = this;
        Log.d(TAG, "BookmarkRepository: initialized");
    }

    public static BookmarkRepository getInstance() {
        if (THIS_INSTANCE == null) {
            throw new UnsupportedOperationException("Singleton Instance is not yet initiated");
        }
        return THIS_INSTANCE;
    }

    public boolean populateCache(@NonNull final List<?> list, @NonNull Object... cacheParams) {
        clearCache();
        Bookmark bookmark;
        for (final Object object : list) {
            bookmark = (Bookmark) object;
            mCacheList.add(bookmark);
            mCacheMap.put(bookmark.getReference(), bookmark);
        }
        Log.d(TAG, "populateCache: updated cache with [" + getCacheSize() + "] bookmarks");
        return true;
    }

    public void clearCache() {
        mCacheList.clear();
        mCacheMap.clear();
    }

    public boolean isCacheEmpty() {
        return mCacheList.isEmpty() && mCacheMap.isEmpty();
    }

    public int getCacheSize() {
        return (mCacheList.size() == mCacheMap.size()) ? mCacheList.size() : -1;
    }

    public Bookmark getCachedRecordUsingKey(@NonNull final Object key) {
        final String bookmarkReference = (String) key;
        return mCacheMap.get(bookmarkReference);
    }

    @Nullable
    public Bookmark getCachedRecordUsingValue(@NonNull final Object value) {
        final String note = (String) value;
        for (final Bookmark bookmark : mCacheList) {
            if (bookmark.getNote().equalsIgnoreCase(note)) {
                return bookmark;
            }
        }
        return null;
    }

    public List<Bookmark> getCachedList() {
        return mCacheList;
    }

    public LiveData<List<Bookmark>> queryDatabase(@NonNull final Object... cacheParams) {
        return mLiveData;
    }

    public boolean createRecord(final Object entityObject) {
        SbDatabase.getInstance(getApplication()).getBookmarkDao()
                  .createRecord((Bookmark) entityObject);
        return true;
    }

    public boolean deleteRecord(final Object entityObject) {
        SbDatabase.getInstance(getApplication()).getBookmarkDao()
                  .deleteRecord((Bookmark) entityObject);
        return true;
    }

    public boolean isCacheValid(final Object... cacheParams) {
        return isCacheEmpty();
    }

    public LiveData<List<Bookmark>> doesRecordExist(final String bookmarkReference) {
        return SbDatabase.getInstance(getApplication()).getBookmarkDao()
                         .readRecord(bookmarkReference);

    }
}
