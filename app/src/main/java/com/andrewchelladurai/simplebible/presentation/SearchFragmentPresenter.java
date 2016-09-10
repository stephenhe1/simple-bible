/*
 *
 * This file 'SearchFragmentPresenter.java' is part of SimpleBible :
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
 *
 */

package com.andrewchelladurai.simplebible.presentation;

import android.util.Log;

import com.andrewchelladurai.simplebible.interaction.SearchFragmentInterface;

/**
 * Created by andrew on 10/9/16. Created by Andrew Chelladurai - TheUnknownAndrew[at]GMail[dot]com
 * on 10-Sep-2016 @ 10:45 PM
 */
public class SearchFragmentPresenter {

    private static final String TAG = "SB_SF_Presenter";
    private SearchFragmentInterface mInterface;

    public SearchFragmentPresenter(SearchFragmentInterface fragmentInterface) {
        mInterface = fragmentInterface;
    }

    public void init() {
        Log.d(TAG, "init() called:");
    }

    public String searchButtonClicked(String inputString) {
        Log.d(TAG, "searchButtonClicked() called");
        mInterface.showResetButton();
        return inputString;
    }

    public void resetButtonClicked() {
        Log.d(TAG, "resetButtonClicked() called");
        mInterface.showSearchButton();
    }
}
