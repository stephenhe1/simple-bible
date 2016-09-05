package com.andrewchelladurai.simplebible;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class SimpleBibleActivity
        extends AppCompatActivity
        implements SimpleBibleActivityInterface {

    private static final String TAG = "SB_SimpleBibleActivity";
    private SimpleBibleActivityPresenter mPresenter;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init the presenter so it can start the necessary DB and Alarm services.
        mPresenter = new SimpleBibleActivityPresenter(this);

        setContentView(R.layout.activity_simple_bible);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_simple_bible_toolbar);
        setSupportActionBar(toolbar);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.activity_simple_bible_container);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_simple_bible_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.activity_simple_bible_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Log.d(TAG, "onCreate() returned");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_bible, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override public Context getThisApplicationContext() {
        return getApplicationContext();
    }

    public static class PlaceholderFragment
            extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_simple_bible, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(
                    getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    private class PagerAdapter
            extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return mPresenter.getTabsCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String titles[] = mPresenter.getTabTitles();
            int titleCount = titles.length;
            Log.d(TAG, "getPageTitle() called : position = [" + position + "] " +
                       "titleCount = [" + titleCount + "]");
            if (position < 0 | position > titleCount) {
                return getString(R.string.application_name);
            }
            return titles[position];
        }
    }
}