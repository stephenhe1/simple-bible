package com.seal.simplebible.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.seal.simplebible.R;
import com.seal.simplebible.db.entities.EntityBookmark;
import com.seal.simplebible.model.Book;
import com.seal.simplebible.model.Bookmark;
import com.seal.simplebible.model.view.BookmarksViewModel;
import com.seal.simplebible.ui.adapter.BookmarksAdapter;
import com.seal.simplebible.ui.ops.BookmarksScreenOps;
import com.seal.simplebible.ui.ops.SimpleBibleOps;

import java.util.ArrayList;
import java.util.List;

public class BookmarksScreen
    extends Fragment
    implements BookmarksScreenOps {

  private static final String TAG = "BookmarksScreen";

  private NativeAdLayout nativeAdLayout;
  private LinearLayout adView;
  private NativeAd nativeAd;

  private SimpleBibleOps ops;

  private BookmarksViewModel model;

  private BookmarksAdapter adapter;

  private View rootView;

  @NonNull
  private String verseTemplateSingle = "";

  @NonNull
  private String verseTemplateMultiple = "";

  @NonNull
  private String noteTemplate = "";

  @NonNull
  private String noteEmptyTemplate = "";

  @Override
  public void onAttach(@NonNull final Context context) {
    Log.d(TAG, "onAttach:");
    super.onAttach(context);

    if (context instanceof SimpleBibleOps) {
      //noinspection unused
      ops = (SimpleBibleOps) context;
    } else {
      throw new ClassCastException(TAG + " onAttach: [Context] must implement [SimpleBibleOps]");
    }

    model = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookmarksViewModel.class);
    adapter = new BookmarksAdapter(this);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView:");
    ops.showNavigationView();
    rootView = inflater.inflate(R.layout.bookmarks_screen, container, false);

    loadNativeAd();

    verseTemplateSingle =
        getResources().getQuantityString(R.plurals.item_bookmark_template_verse, 1);
    verseTemplateMultiple =
        getResources().getQuantityString(R.plurals.item_bookmark_template_verse, 2);
    noteTemplate = getString(R.string.item_bookmark_template_note);
    noteEmptyTemplate = getString(R.string.item_bookmark_template_note_empty);

    ((RecyclerView) rootView.findViewById(R.id.scr_bookmarks_list)).setAdapter(adapter);

    updateContent();

    return rootView;
  }

  private void updateContent() {
    Log.d(TAG, "updateContent:");
    model.getBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
      if (bookmarks == null || bookmarks.isEmpty()) {
        showHelpInfo();
        return;
      }
      model.cacheBookmarks(bookmarks);
      showBookmarks();
    });
  }

  private void showHelpInfo() {

    ((TextView) rootView.findViewById(R.id.scr_bookmarks_help))
        .setText(HtmlCompat.fromHtml(getString(R.string.scr_bookmarks_help),
                                     HtmlCompat.FROM_HTML_MODE_COMPACT));

    rootView.findViewById(R.id.scr_bookmarks_contain_help).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.scr_bookmarks_list).setVisibility(View.GONE);
  }

  private void showBookmarks() {
    rootView.findViewById(R.id.scr_bookmarks_list).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.scr_bookmarks_contain_help).setVisibility(View.GONE);
    adapter.notifyDataSetChanged();
  }

  @Override
  public int getBookmarkListSize() {
    return model.getBookmarkListSize();
  }

  @NonNull
  @Override
  public EntityBookmark getBookmarkAtPosition(final int position) {
    return model.getBookmarkAtPosition(position);
  }

  @Override
  public void handleActionSelect(@NonNull final EntityBookmark bookmark) {
    Log.d(TAG, "handleActionSelect: bookmark = [" + bookmark + "]");

    final Bundle bundle = new Bundle();
    bundle.putString(BookmarkScreen.ARG_STR_REFERENCE, bookmark.getReference());

    NavHostFragment.findNavController(this)
                   .navigate(R.id.nav_from_scr_bookmark_list_to_scr_bookmark, bundle);
  }

  @Override
  public void getFirstVerseOfBookmark(@NonNull final EntityBookmark bookmark,
                                      @NonNull final TextView verseView,
                                      @NonNull final TextView noteView) {
    model.getFirstVerseOfBookmark(bookmark).observe(getViewLifecycleOwner(), verse -> {
      final String bookmarkReference = bookmark.getReference();
      final String bookmarkNote = bookmark.getNote();
      final int verseCount = Bookmark.splitBookmarkReference(bookmarkReference).length;

      if (verse == null) {
        Log.e(TAG, "getFirstVerseOfBookmark: ",
              new IllegalArgumentException("null bookmark found for reference["
                                           + bookmarkReference + "]"));
        return;
      }

      final Book book = Book.getCachedBook(verse.getBook());
      if (book == null) {
        Log.e(TAG, "getFirstVerseOfBookmark: ",
              new IllegalArgumentException("null book for verse reference[" + verse + "]"));
        return;
      }

      final int htmlMode = HtmlCompat.FROM_HTML_MODE_COMPACT;
      final Spanned verseContent =
          HtmlCompat.fromHtml(String.format((verseCount == 1)
                                            ? verseTemplateSingle
                                            : verseTemplateMultiple,
                                            verseCount,
                                            book.getName(),
                                            verse.getChapter(),
                                            verse.getVerse(),
                                            verse.getText()), htmlMode);

      final Spanned noteContent =
          (bookmarkNote.isEmpty())
          ? HtmlCompat.fromHtml(noteEmptyTemplate, htmlMode)
          : HtmlCompat.fromHtml(String.format(noteTemplate, bookmarkNote), htmlMode);

      noteView.setText(noteContent);
      verseView.setText(verseContent);

    });
  }

  private void loadNativeAd() {
    // Instantiate a NativeAd object.
    // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
    // now, while you are testing and replace it later when you have signed up.
    // While you are using this temporary code you will only get test ads and if you release
    // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
    nativeAd = new NativeAd(rootView.getContext(), "YOUR_PLACEMENT_ID");

    NativeAdListener nativeAdListener = new NativeAdListener() {
      @Override
      public void onMediaDownloaded(Ad ad) {
        // Native ad finished downloading all assets
        Log.e(TAG, "Native ad finished downloading all assets.");
      }

      @Override
      public void onError(Ad ad, AdError adError) {
        // Native ad failed to load
        Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
      }

      @Override
      public void onAdLoaded(Ad ad) {
        // Native ad is loaded and ready to be displayed
        Log.d(TAG, "Native ad is loaded and ready to be displayed!");
        showNativeAdWithDelay();
      }

      @Override
      public void onAdClicked(Ad ad) {
        // Native ad clicked
        Log.d(TAG, "Native ad clicked!");
      }

      @Override
      public void onLoggingImpression(Ad ad) {
        // Native ad impression
        Log.d(TAG, "Native ad impression logged!");
      }
    };

    // Request an ad
    nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build());
  }

  private void inflateAd(NativeAd nativeAd) {

    nativeAd.unregisterView();

    // Add the Ad view into the ad container.
    nativeAdLayout = rootView.findViewById(R.id.native_ad_container);
    // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
    adView = (LinearLayout) LayoutInflater.from(rootView.getContext()).inflate(R.layout.native_ad_layout, nativeAdLayout, false);
    nativeAdLayout.addView(adView);

    // Add the AdOptionsView
    LinearLayout adChoicesContainer = rootView.findViewById(R.id.ad_choices_container);
    AdOptionsView adOptionsView = new AdOptionsView(rootView.getContext(), nativeAd, nativeAdLayout);
    adChoicesContainer.removeAllViews();
    adChoicesContainer.addView(adOptionsView, 0);

    // Create native UI using the ad metadata.
    MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
    MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

    // Set the Text.
    nativeAdTitle.setText(nativeAd.getAdvertiserName());
    nativeAdBody.setText(nativeAd.getAdBodyText());
    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

    // Create a list of clickable views
    List<View> clickableViews = new ArrayList<>();
    clickableViews.add(nativeAdTitle);
    clickableViews.add(nativeAdCallToAction);

    // Register the Title and CTA button to listen for clicks.
    nativeAd.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews);
  }

  private void showNativeAdWithDelay() {
    /**
     * Here is an example for displaying the ad with delay;
     * Please do not copy the Handler into your project
     */
        // Check if nativeAd has been loaded successfully
        if(nativeAd == null || !nativeAd.isAdLoaded()) {
          return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if(nativeAd.isAdInvalidated()) {
          return;
        }
        inflateAd(nativeAd); // Inflate NativeAd into a container, same as in previous code examples


  }
}
