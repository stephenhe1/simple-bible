package com.seal.simplebible.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.seal.simplebible.R;
import com.seal.simplebible.db.entities.EntityBook;
import com.seal.simplebible.model.Book;
import com.seal.simplebible.model.view.BooksViewModel;
import com.seal.simplebible.ui.adapter.BooksAdapter;
import com.seal.simplebible.ui.ops.BookListScreenOps;
import com.seal.simplebible.ui.ops.SimpleBibleOps;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BooksScreen
    extends Fragment
    implements BookListScreenOps {

  private static final String TAG = "BooksScreen";

  private NativeAdLayout nativeAdLayout;
  private LinearLayout adView;
  private NativeBannerAd nativeBannerAd;

  private SimpleBibleOps ops;

  private View rootView;

  private BooksViewModel model;

  private BooksAdapter booksAdapter;

  @Override
  public void onAttach(@NonNull final Context context) {
    Log.d(TAG, "onAttach:");
    super.onAttach(context);

    if (context instanceof SimpleBibleOps) {
      ops = (SimpleBibleOps) context;
    } else {
      throw new ClassCastException(TAG + " onAttach: [Context] must implement [SimpleBibleOps]");
    }

    model = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BooksViewModel.class);

    booksAdapter = new BooksAdapter(this);
  }

  @Override
  public void onDestroyView() {
    final SearchView searchView = rootView.findViewById(R.id.scr_books_search);
    searchView.setQuery("", true);
    super.onDestroyView();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView:");




    ops.hideKeyboard();
    ops.showNavigationView();

    rootView = inflater.inflate(R.layout.books_screen, container, false);

    final RecyclerView recyclerView = rootView.findViewById(R.id.scr_books_list);
    recyclerView.setAdapter(booksAdapter);

    final SearchView searchView = rootView.findViewById(R.id.scr_books_search);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(final String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(final String newText) {
        booksAdapter.filterList((newText == null) ? "" : newText);
        booksAdapter.notifyDataSetChanged();
        return true;
      }
    });

    if (savedInstanceState == null) {
      updateContent();
    }

    nativeBannerAd = new NativeBannerAd(rootView.getContext(), "YOUR_PLACEMENT_ID");
    NativeAdListener nativeAdListener = new NativeAdListener() {
      @Override
      public void onMediaDownloaded(Ad ad) {

      }

      @Override
      public void onError(Ad ad, AdError adError) {

      }

      @Override
      public void onAdLoaded(Ad ad) {
        if (nativeBannerAd == null || nativeBannerAd != ad) {
          return;
        }
        // Inflate Native Banner Ad into Container
        showAdWithDelay();
      }

      @Override
      public void onAdClicked(Ad ad) {

      }

      @Override
      public void onLoggingImpression(Ad ad) {

      }
    };
    // load the ad
    nativeBannerAd.loadAd(
            nativeBannerAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build());

    return rootView;
  }

  private void updateContent() {
    Log.d(TAG, "updateContent:");

    final Set<Integer> books = Book.getCachedBookList();

    if (books.size() != Book.MAX_BOOKS) {
      refreshContent();
      return;
    }

    model.getAllBooks().observe(getViewLifecycleOwner(), list -> {

      if (list == null || list.isEmpty() || list.size() != Book.MAX_BOOKS) {
        final String msg = getString(R.string.scr_books_msg_invalid_list,
                                     (list == null) ? 0 : list.size(),
                                     Book.MAX_BOOKS);
        Log.e(TAG, "updateContent: " + msg);
        ops.showErrorScreen(msg, true, true);
        return;
      }

      final ArrayList<Book> bookList = new ArrayList<>(list.size());
      for (final EntityBook book : list) {
        bookList.add(new Book(book));
      }

      Book.updateCacheBooks(bookList);
      refreshContent();

    });
  }

  private void refreshContent() {
    Log.d(TAG, "refreshContent:");
    booksAdapter.filterList("");
    booksAdapter.notifyDataSetChanged();
  }

  @Override
  public void handleBookSelection(@Nullable final Book book) {
    Log.d(TAG, "handleBookSelection:");

    if (book == null) {
      Log.e(TAG, "handleBookSelection: cannot show a null book");
      return;
    }

    final Bundle bundle = new Bundle();
    bundle.putInt(ChapterScreen.ARG_INT_BOOK, book.getNumber());
    bundle.putInt(ChapterScreen.ARG_INT_CHAPTER, 1);

    NavHostFragment.findNavController(this)
                   .navigate(R.id.nav_from_scr_book_list_to_scr_chapter, bundle);
  }



  private void inflateAd(NativeBannerAd nativeBannerAd) {
    // Unregister last ad
    nativeBannerAd.unregisterView();

    // Add the Ad view into the ad container.
    nativeAdLayout = rootView.findViewById(R.id.native_banner_ad_container);
    // Inflate the Ad view.  The layout referenced is the one you created in the last step.
    adView = (LinearLayout) LayoutInflater.from(rootView.getContext()).inflate(R.layout.native_banner_ad_unit, nativeAdLayout, false);
    nativeAdLayout.addView(adView);

    // Add the AdChoices icon
    RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
    AdOptionsView adOptionsView = new AdOptionsView(rootView.getContext(), nativeBannerAd, nativeAdLayout);
    adChoicesContainer.removeAllViews();
    adChoicesContainer.addView(adOptionsView, 0);

    // Create native UI using the ad metadata.
    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
    MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

    // Set the Text.
    nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
    nativeAdCallToAction.setVisibility(
            nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
    nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
    nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
    sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

    // Register the Title and CTA button to listen for clicks.
    List<View> clickableViews = new ArrayList<>();
    clickableViews.add(nativeAdTitle);
    clickableViews.add(nativeAdCallToAction);
    nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
  }

  private void showAdWithDelay() {
    /**
     * Here is an example for displaying the ad with delay;
     * Please do not copy the Handler into your project
     */
     Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        // Check if nativeBannerAd has been loaded successfully
        if(nativeBannerAd == null || !nativeBannerAd.isAdLoaded()) {
          return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if(nativeBannerAd.isAdInvalidated()) {
          return;
        }
        inflateAd(nativeBannerAd); // Inflate Native Banner Ad into Container same as in previous code example
      }
    }, 4000); // Show the ad after 15 minutes
  }

}
