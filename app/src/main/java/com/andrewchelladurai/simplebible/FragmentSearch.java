/*
 *
 * This file 'FragmentSearch.java' is part of SimpleBible :
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

package com.andrewchelladurai.simplebible;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentSearch
        extends Fragment
        implements View.OnClickListener, TextWatcher {

    private static final String TAG = "SB_FragmentSearch";
    private TextInputEditText mInput;
    private AdapterSearchList mListAdapter;
    private AppCompatButton   mButton;
    private TextInputLayout   mLabel;

    public FragmentSearch() {
    }

    public static FragmentSearch newInstance() {
        return new FragmentSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
//        super.onCreateView(inflater, container, savedState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mInput = (TextInputEditText) view.findViewById(R.id.frag_search_input);
        mInput.addTextChangedListener(this);
        mLabel = (TextInputLayout) view.findViewById(R.id.frag_search_label);

        mButton = (AppCompatButton) view.findViewById(R.id.frag_search_button);
        mButton.setOnClickListener(this);

        RecyclerView listResults = (RecyclerView) view.findViewById(R.id.frag_search_results);
        mListAdapter = new AdapterSearchList(ListSearch.getEntries(), this);
        listResults.setAdapter(mListAdapter);

        AppCompatButton button =
                (AppCompatButton) view.findViewById(R.id.frag_search_but_save);
        button.setOnClickListener(this);

        button = (AppCompatButton) view.findViewById(R.id.frag_search_but_share);
        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof AppCompatButton) {
            switch (v.getId()) {
                case R.id.frag_search_button:
                    String buttonText = mButton.getText().toString();
                    if (buttonText.equalsIgnoreCase(
                            getString(R.string.button_search_text))) {
                        searchButtonClicked();
                    } else if (buttonText.equalsIgnoreCase(
                            getString(R.string.button_search_reset))) {
                        resetButtonClicked();
                    }
                    break;
                case R.id.frag_search_but_save:
                    buttonSaveClicked();
                    break;
                case R.id.frag_search_but_share:
                    buttonShareClicked();
                    break;
                default:
                    Utilities.throwError(TAG + " onClick() unknown Button ID" + v.getId());
            }
        }
    }

    private void searchButtonClicked() {
        String input = mInput.getText().toString().trim();
        if (input.isEmpty()) {
            resetButtonClicked();
            mLabel.setError(getString(R.string.search_text_empty));
            return;
        }
        if (input.length() < 3) {
            resetButtonClicked();
            mLabel.setError(getString(R.string.search_text_length));
            return;
        }
        DatabaseUtility dbu = DatabaseUtility.getInstance(getActivity().getApplicationContext());
        ArrayList<String> list = dbu.searchText(input);
        if (list.size() == 0) {
            mLabel.setError(getString(R.string.search_no_results));
            mInput.requestFocus();
            return;
        }
        mLabel.setError(list.size() + " " + getString(+R.string.search_text_results_found));

        ListSearch.truncate();
        ListSearch.populate(list);
        showActionBar();
        mListAdapter.notifyDataSetChanged();
        mButton.setText(getString(R.string.button_search_reset));
    }

    private void resetButtonClicked() {
        Log.d(TAG, "resetButtonClicked() called");
        ListSearch.truncate();
        mInput.setText("");
        mInput.setError(null);
        mLabel.setError(null);
        mButton.setText(getString(R.string.button_search_text));
        mListAdapter.notifyDataSetChanged();
        mInput.requestFocus();
        showActionBar();
    }

    public void buttonSaveClicked() {
        Log.d(TAG, "buttonSaveClicked() called");
        if (ListSearch.isSelectedEntriesEmpty()) {
            Log.d(TAG, "buttonShareClicked: No Selected entries exist");
            return;
        }
        ArrayList<ListSearch.Entry> entries = ListSearch.getSelectedEntries();
        ArrayList<String> references = new ArrayList<>();
        for (ListSearch.Entry entry : entries) {
            references.add(entry.getReference());
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Utilities.REFERENCES, references);
        bundle.putString(Utilities.BOOKMARK_MODE, Utilities.BOOKMARK_VIEW);

        Intent intent = new Intent(getContext(), ActivityBookmark.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void buttonShareClicked() {
        Log.d(TAG, "buttonShareClicked() called");
        if (ListSearch.isSelectedEntriesEmpty()) {
            Log.d(TAG, "buttonShareClicked: No Selected entries exist");
            return;
        }
        ArrayList<ListSearch.Entry> entries = ListSearch.getSelectedEntries();
        String text;
        StringBuilder shareText = new StringBuilder();
        ListBooks.Entry mBook;
        for (ListSearch.Entry entry : entries) {
            mBook = ListBooks.getItem(entry.getBookNumber());
            text = mBook.getName() + " (" +
                   entry.getChapterNumber() + ":" +
                   entry.getVerseNumber() + ") " +
                   entry.getVerse() + "\n";
            shareText.append(text);
        }
        shareText.append(getString(R.string.share_append_text));
        startActivity(Utilities.shareVerse(shareText.toString()));
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        mInput.setError(null);
        mLabel.setError(null);
        mButton.setText(getString(R.string.button_search_text));
        showActionBar();
    }

    public void showActionBar() {
        LinearLayoutCompat view = (LinearLayoutCompat) getActivity().findViewById(
                R.id.frag_search_verse_actions);
        view.setVisibility((ListSearch.isSelectedEntriesEmpty()) ? View.GONE : View.VISIBLE);
    }
}
