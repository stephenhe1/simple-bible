package com.andrewchelladurai.simplebible.data.repository.ops;

import com.andrewchelladurai.simplebible.data.entities.Verse;

import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * Created by : Andrew Chelladurai
 * Email : TheUnknownAndrew[at]GMail[dot]com
 * on : 02-Sep-2018 @ 9:28 PM.
 */
public interface VerseRepositoryOps {

    void clearCache();

    boolean isCacheValid(@NonNull final Object... cacheParams);

    boolean populateCache(@NonNull final List<Verse> list,
                          @NonNull final Object... cacheParams);

    boolean isCacheEmpty();

    int getCacheSize();

    List<Verse> getCachedList();

    LiveData<List<Verse>> queryDatabase(@NonNull final Object... cacheParams);

    boolean createRecord(@NonNull final Verse verse);

    List<Verse> queryVerse(@IntRange(from = 1, to = 66) final int book,
                           @IntRange(from = 1) final int chapter,
                           @IntRange(from = 1) final int verse);

/*
    Object getCachedRecordUsingKey(@NonNull final Object key);

    Object getCachedRecordUsingValue(@NonNull final Object value);

    boolean deleteRecord(@NonNull final Verse verse);
*/

}