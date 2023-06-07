package com.seal.simplebible.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.seal.simplebible.R;
import com.seal.simplebible.model.Book;
import com.seal.simplebible.model.Verse;
import com.seal.simplebible.model.view.HomeViewModel;
import com.seal.simplebible.ui.ops.HomeScreenOps;
import com.seal.simplebible.ui.ops.SimpleBibleOps;

import org.w3c.dom.Text;

import java.util.Calendar;

public class HomeScreen
  extends Fragment
  implements HomeScreenOps {

  private static final String TAG = "HomeScreen";

  private HomeViewModel model;

  private SimpleBibleOps ops;

  private View rootView;

  private static String DEFAULT_REFERENCE = null;
  private InterstitialAd  interstitialAd ;


  @Override
  public void onAttach(@NonNull final Context context) {
    Log.d(TAG, "onAttach:");
    super.onAttach(context);

    if (context instanceof SimpleBibleOps) {
      ops = (SimpleBibleOps) context;
    } else {
      throw new ClassCastException(TAG + " onAttach: [Context] must implement [SimpleBibleOps]");
    }

    model = ViewModelProvider.AndroidViewModelFactory.getInstance(
      requireActivity().getApplication())
                                                     .create(HomeViewModel.class);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedState) {
    Log.d(TAG, "onCreateView:");
    rootView = inflater.inflate(R.layout.home_screen, container, false);
    ops.showNavigationView();

    interstitialAd = new InterstitialAd(rootView.getContext(), "YOUR_PLACEMENT_ID");
    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
      @Override
      public void onInterstitialDisplayed(Ad ad) {
        handleActionSettings();
      }

      @Override
      public void onInterstitialDismissed(Ad ad) {

      }

      @Override
      public void onError(Ad ad, AdError adError) {

      }

      @Override
      public void onAdLoaded(Ad ad) {

      }

      @Override
      public void onAdClicked(Ad ad) {

      }

      @Override
      public void onLoggingImpression(Ad ad) {

      }
    };
    // load the ad
    interstitialAd.loadAd(
            interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build());

    rootView.findViewById(R.id.scr_home_action_settings)
//            .setOnClickListener(v -> handleActionSettings());
            .setOnClickListener(v -> showAdWithDelay());
    rootView.findViewById(R.id.scr_home_action_bookmark)
            .setOnClickListener(v -> handleActionBookmark());
    rootView.findViewById(R.id.scr_home_action_chapter)
            .setOnClickListener(v -> handleActionChapter());
    rootView.findViewById(R.id.scr_home_action_share)
            .setOnClickListener(v -> handleActionShare());

    if (null == DEFAULT_REFERENCE) {
      final Resources resources = getResources();
      DEFAULT_REFERENCE = Verse.createReference(resources.getInteger(R.integer.default_book_number),
                                                resources.getInteger(
                                                  R.integer.default_chapter_number),
                                                resources.getInteger(
                                                  R.integer.default_verse_number));
      Log.d(TAG, "onCreateView: DEFAULT_VERSE_REFERENCE[" + DEFAULT_REFERENCE + "]");
    }

    updateContent("");

    return rootView;
  }

  private void updateContent(@NonNull final String verseReference) {
    final String[] reference = {verseReference};
    final int dayNo = Calendar.getInstance()
                              .get(Calendar.DAY_OF_YEAR);
    Log.d(TAG, "updateContent: reference = [" + verseReference + "], dayNo[" + dayNo + "]");

    if (verseReference.isEmpty()) {
      final Verse cachedVerse = model.getCachedVerse();

      if (cachedVerse != null && dayNo == model.getCachedVerseDay()) {
        Log.d(TAG, "updateContent: already cached verse for day[" + dayNo + "]");
        displayVerse(cachedVerse);
        return;
      }

      final String[] array = getResources().getStringArray(R.array.daily_verse_references);
      reference[0] = (array.length < dayNo) ? DEFAULT_REFERENCE : array[dayNo];
    }

    final boolean validated = Verse.validateReference(reference[0]);

    if (!validated) {
      Log.e(TAG, "updateContent:",
            new IllegalArgumentException("reference[" + reference[0] + "] not valid"));
      updateContent(DEFAULT_REFERENCE);
      return;
    }

    final int[] parts = Verse.splitReference(reference[0]);
    if (parts == null) {
      Log.e(TAG, "updateContent:",
            new IllegalArgumentException("invalid parts of reference[" + reference[0] + "]"));
      updateContent(DEFAULT_REFERENCE);
      return;
    }

    model.getVerse(parts[0], parts[1], parts[2])
         .observe(getViewLifecycleOwner(), verse -> {
           if (verse == null) {
             Log.e(TAG, "updateContent:", new IllegalArgumentException(
               "no verse found for reference[" + reference[0] + "]"));
             updateContent(DEFAULT_REFERENCE);
             return;
           }

           final Book book = Book.getCachedBook(verse.getBook());
           if (book == null) {
             Log.e(TAG, "updateContent:", new IllegalArgumentException(
               "no book found for reference[" + reference[0] + "]"));
             updateContent(DEFAULT_REFERENCE);
             return;
           }

           final Verse dailyVerse = new Verse(verse, book);

           model.setCachedVerse(dailyVerse);
           model.setCachedVerseDay(dayNo);
           displayVerse(dailyVerse);

         });
  }

  private void displayVerse(@NonNull final Verse verse) {
    Book book = verse.getBook();

//    if (verse.getReference()
//             .equalsIgnoreCase(DEFAULT_REFERENCE)) {
//      Log.d(TAG, "displayVerse: displaying defaultReference[" + DEFAULT_REFERENCE + "]");
//      displayDefaultVerse();
//      return;
//    }

//    Log.d(TAG, "displayVerse: displaying reference[" + verse.getReference() + "]");
    TextView textView1 = rootView.findViewById(R.id.scr_home_verse);
    TextView textView2 = rootView.findViewById(R.id.scr_home_verse_chapter);
    TextView textView3 = rootView.findViewById(R.id.scr_home_verse_book);
    textView1.setText(verse.getVerseText());
    textView2.setText("Chapter " + verse.getChapterNumber() + " Verse " +verse.getVerseNumber());
    textView3.setText("Inside the book: " +book.getName());
//    final String formattedText = String.format(getString(R.string.scr_home_verse_template),
//                                               book.getName(), verse.getChapterNumber(),
//                                               verse.getVerseNumber(), verse.getVerseText());
//    final Spanned htmlText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_COMPACT);
//    final TextView textView = rootView.findViewById(R.id.scr_home_verse);
//    textView.setText(htmlText);
  }

  private void displayDefaultVerse() {
    Log.d(TAG, "displayDefaultVerse:");
    final Spanned htmlText = HtmlCompat.fromHtml(getString(R.string.scr_home_verse_default),
                                                 HtmlCompat.FROM_HTML_MODE_COMPACT);
    final TextView textView = rootView.findViewById(R.id.scr_home_verse);
    textView.setText(htmlText);
  }

  private void handleActionSettings() {
    Log.d(TAG, "handleActionSettings:");
    NavHostFragment.findNavController(getParentFragment())
                   .navigate(R.id.nav_from_scr_home_to_scr_settings);
  }

  private void handleActionBookmark() {
    final Verse verse = model.getCachedVerse();
    if (null == verse) {
      Log.e(TAG, "handleActionBookmark: null cached verse");
      return;
    }

    final Bundle bundle = new Bundle();
    bundle.putString(BookmarkScreen.ARG_STR_REFERENCE, verse.getReference());
    Log.d(TAG, "handleActionBookmark: ARG_STR_REFERENCE [" + verse.getReference() + "]");
    NavHostFragment.findNavController(this)
                   .navigate(R.id.nav_from_scr_home_to_scr_bookmark, bundle);
  }

  private void handleActionChapter() {
    Log.d(TAG, "handleActionChapter:");
    final Bundle bundle = new Bundle();
    final Verse verse = model.getCachedVerse();

    if (verse == null) {
      Log.e(TAG, "handleActionChapter: null cached verse, will show default chapter");
      final Resources resources = getResources();
      bundle.putInt(ChapterScreen.ARG_INT_BOOK,
                    resources.getInteger(R.integer.default_book_number));
      bundle.putInt(ChapterScreen.ARG_INT_CHAPTER,
                    resources.getInteger(R.integer.default_chapter_number));
    } else {
      bundle.putInt(ChapterScreen.ARG_INT_BOOK, verse.getBookNumber());
      bundle.putInt(ChapterScreen.ARG_INT_CHAPTER, verse.getChapterNumber());
    }

    NavHostFragment.findNavController(this)
                   .navigate(R.id.nav_from_scr_home_to_scr_chapter, bundle);

  }

  private void handleActionShare() {
    Log.d(TAG, "handleActionShare:");
    final CharSequence text = ((TextView) rootView.findViewById(R.id.scr_home_verse)).getText();
    ops.shareText(text.toString());
  }

  private void showAdWithDelay() {
    /**
     * Here is an example for displaying the ad with delay;
     * Please do not copy the Handler into your project
     */
     Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        // Check if interstitialAd has been loaded successfully
        if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
          return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if(interstitialAd.isAdInvalidated()) {
          return;
        }
        // Show the ad
        interstitialAd.show();
      }
    }, 500); // Show the ad after 15 minutes
  }

}
