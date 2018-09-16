/*
 *
 * This file 'Verse.java' is part of SimpleBible :
 *
 * Copyright (c) 2018.
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
 *
 */

package com.andrewchelladurai.simplebible.data;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * Created by : Andrew Chelladurai
 * Email : TheUnknownAndrew[at]GMail[dot]com
 * on : 04-Aug-2018 @ 8:17 PM.
 */

@Entity(tableName = "BIBLEVERSES",
        primaryKeys = {"BOOKNUMBER", "CHAPTERNUMBER", "VERSENUMBER"})
public class Verse {

    private static final String TAG = "Verse";

    public static String SEPARATOR = ":";

    @NonNull
    @ColumnInfo(name = "TRANSLATION")
    private final String translation;

    @ColumnInfo(name = "BOOKNUMBER")
    @IntRange(from = 1,
              to = 66)
    private final int book;

    @ColumnInfo(name = "CHAPTERNUMBER")
    @IntRange(from = 1)
    private final int chapter;

    @ColumnInfo(name = "VERSENUMBER")
    @IntRange(from = 1)
    private final int verse;

    @NonNull
    @ColumnInfo(name = "VERSETEXT")
    private final String text;

    private boolean selected = false;

    public Verse(@NonNull final String translation, @IntRange(from = 1,
                                                              to = 66) final int book,
                 @IntRange(from = 1) final int chapter, @IntRange(from = 1) final int verse,
                 @NonNull final String text) {
        this.translation = translation;
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
        this.text = text;
        this.selected = false;
    }

    @NonNull
    public static String createReference(@IntRange(from = 1, to = 66) int book,
                                         @IntRange(from = 1) int chapter,
                                         @IntRange(from = 1) int verse)
    throws UnsupportedOperationException {
        if (book < 1 || book > 66 || chapter < 1 || verse < 1) {
            Log.e(TAG, "createReference: invalid params");
            throw new UnsupportedOperationException();
        }
        return book + SEPARATOR + chapter + SEPARATOR + verse;
    }

    public static boolean validateReference(@NonNull final String reference)
    throws NullPointerException, NumberFormatException, UnsupportedOperationException {
        if (reference.isEmpty()) {
            Log.e(TAG, "validateReference: Empty reference passed");
            throw new NullPointerException();
        }

        if (!reference.contains(SEPARATOR)) {
            Log.e(TAG, "validateReference: no " + SEPARATOR + " in reference[" + reference + "]");
            throw new UnsupportedOperationException();
        }

        final String[] parts = reference.split(SEPARATOR);
        if (parts.length != 3) {
            Log.e(TAG, "validateReference: reference[" + reference + "] does not have 3 parts");
            throw new UnsupportedOperationException();
        }

        int value;
        for (final String part : parts) {
            try {
                value = Integer.parseInt(part);
                if (value < 1) {
                    Log.e(TAG, "validateReference: part of reference[" + reference + "] < 1");
                    throw new UnsupportedOperationException();
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "validateReference: part of reference[" + reference + "] is NAN");
                throw e;
            }
        }

        return true;
    }

    public static int[] splitReference(@NonNull final String reference)
    throws NumberFormatException {
        final String[] parts = reference.split(SEPARATOR);
        final int[] sections = new int[parts.length];
        for (int i = 0; i < sections.length; i++) {
            try {
                sections[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "splitReference: parsing failure", e);
                throw e;
            }
        }
        return sections;
    }

    @NonNull
    public String getTranslation() {
        return translation;
    }

    public int getBook() {
        return book;
    }

    public int getChapter() {
        return chapter;
    }

    public int getVerse() {
        return verse;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public String getReference() {
        return createReference(book, chapter, verse);
    }

    @Override
    public String toString() {
        return getReference();
    }

}