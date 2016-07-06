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

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentSearch
        extends Fragment
        implements View.OnClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mInput = (TextInputEditText) view.findViewById(R.id.frag_search_input);
        mLabel = (TextInputLayout) view.findViewById(R.id.frag_search_label);

        mButton = (AppCompatButton) view.findViewById(R.id.frag_search_button);
        mButton.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.frag_search_results);

        ListSearch.populate(null);
        mListAdapter = new AdapterSearchList(ListSearch.getEntries());
        resetButtonClicked();
        recyclerView.setAdapter(mListAdapter);

        return view;
    }

    private void resetButtonClicked() {
        mInput.setText("");
        mInput.setError(null);
        mLabel.setError(null);
        mListAdapter.notifyDataSetChanged();
        mButton.setText(getString(R.string.button_search_text));
    }

    @Override public void onClick(View v) {
        if (v instanceof AppCompatButton & v.equals(mButton)) {
            String buttonText = mButton.getText().toString();
            if (buttonText.equalsIgnoreCase(getString(R.string.button_search_text))) {
                searchButtonClicked();
            } else if (buttonText.equalsIgnoreCase(getString(R.string.button_search_reset))) {
                resetButtonClicked();
            }
        }
    }

    private void searchButtonClicked() {
        String input = mInput.getText().toString().trim();
        if (input.isEmpty()) {
            mInput.setError(getString(R.string.search_text_empty));
            mInput.requestFocus();
            return;
        }
        if (input.length() < 3) {
            mInput.setError(getString(R.string.search_text_length));
            mInput.requestFocus();
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

        ListSearch.populate(list);
        mListAdapter.notifyDataSetChanged();
        mButton.setText(getString(R.string.button_search_reset));
    }
}
