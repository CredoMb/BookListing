package com.example.android.booklisting;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuItemCompat;

import java.util.ArrayList;
import java.util.List;


public class SearchResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Constant value for the book loader ID. We can choose any integer.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * The class name will be used as the LOG TAG
     */
    private static final String LOG_TAG = SearchResultActivity.class.getName();

    /**
     * Variable to store the Google book Request Url
     */
    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?";

    /**
     * This variable will hold an Array Adapter with a list of Books
     */
    private BookAdapter mAdapter;

    /**
     * Holds the empty state TextView
     */
    private View mEmptyStateView;

    /**
     * The progress Spinner
     */
    private ProgressBar mProgressSpinner;

    /**
     * The searched term will entered here
     */
    EditText mSearchField;

    /**
     * To store the genre entered by the user in the mainActivity
     */
    String mBookGenre;

    /**
     * The maximum of item to display on the UI
     */
    String MAX_RESULT = "10";

    /**
     * Will contain all the books queryed from the API
     */
    ListView BookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        // Change the font of the appbar Text (which is the name of the app)
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            // Get the TextView that holds the name of the app
        TextView actionBarTitleView = (TextView) findViewById(actionBarTitleId);
        setCustomTypeFace(actionBarTitleView, actionBarTitleId,R.font.avenir_nextltpro_regular);*/


        // Draw the layout by using the XML resource "search_results_activity.xml"
        setContentView(R.layout.search_results_activity);

        // Inflate the progress bar (spinner form this time) -  It has an other name like instatiate or something
        mProgressSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        // Remove the progress spinner to display the empty state view properly
        mProgressSpinner.setVisibility(View.INVISIBLE);

        // Inflate the empty state View
        mEmptyStateView = findViewById(R.id.empty_view);

        // Find a reference to the {@link ListView} in the layout
        BookListView = (ListView) findViewById(R.id.list);
        BookListView.setEmptyView(mEmptyStateView);

        // Create a new {@link ArrayAdapter} of Books
        mAdapter = new BookAdapter(
                this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        BookListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }

        // Set click listener on the list's items
        BookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Create an Intent to open a browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getItem(i).getExploreLink()));

                // Verify if there's any application available to handle the intent
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    // Start an Activity with the intent
                    startActivity(webIntent);
                }
            }
        });
    }

    /**
     * This will inflate the menu on the app bar of the "SearchResultActivity"
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_search_activity.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);

        // Find the menuItem that corresponds to the search View
        // and store it inside the searchItem variable
        MenuItem searchItem = menu.findItem(R.id.menu_search_view);

        // From the searchItem, get the searchView and store it into a variable
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Set a custom typeface to the searchView Text.
        // This will match the font of the text typed by the user
        // with the font of the texts in the app

        // Get the id of the SearchView's textView
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        // Set a custom Type Face on the searchView's TextView
        setCustomTypeFace(searchView, id,R.font.avenir_nextltpro_regular);

        // Prevent the searchView to take a full screen size when the device is on landscape
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                // Make the Empty View Invisible
                mEmptyStateView.setVisibility(View.INVISIBLE);

                // Display the progress spinner while the user is waiting for results
                mProgressSpinner.setVisibility(View.VISIBLE);

                // Destroy the previous loader so the system can create a new link
                getLoaderManager().destroyLoader(BOOK_LOADER_ID);

                // After the user has submitted his search word,
                // insert the word inside the bookGenre variable
                mBookGenre = s;

                // Based on the network connection status, start the loader
                // or display the empty state view
                startLoaderOrEmptyState(R.drawable.empty_state_no_internet,
                        R.string.no_internet_title,
                        R.string.no_internet_subtitle);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // How to wait until the person presses enter ?
                return true;
            }
        });
        return true;
    }

    /** Set a custom Typeface on the searchView Text
     * @param view is the main view that contains the TextView to modify
     * @param fontId is the Id of the font we want to set
     * @param viewTextViewId is the Id of the textView contained by the view*/

    private void setCustomTypeFace(View view, int viewTextViewId ,int fontId) {

        // Get the textView of the SearchView to set a custom font on it
        TextView viewText = (TextView) view.findViewById(viewTextViewId);

        // Create a custom typeFace from the font
        Typeface myCustomFont = ResourcesCompat.getFont(this,fontId);
        viewText.setTypeface(myCustomFont);
    }
    /**
     * Method to Check the Network connection and return true or false
     * based on the network connection state
     */

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Execute certain task based on the internet connection status.
     * If the connection is on, initiate the loader
     * other wise, display the empty state view
     */

    private void startLoaderOrEmptyState(int emptyStateImageId, int emptyStateTitleId, int emptyStateSubtitleId) {

        // Check the status of the network, then either launch the Loader or
        // display the Empty State

        if (isNetworkConnected()) {
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, SearchResultActivity.this).forceLoad();
        } else {
            // Remove the progress spinner
            mProgressSpinner.setVisibility(View.GONE);

            // Fill the empty state view with its resources,
            // one image and 2 strings
            fillEmptyStateView(emptyStateImageId,
                    emptyStateTitleId,
                    emptyStateSubtitleId);
        }
    }

    /**
     * This method will help to fill the Image View and
     * the two textViews of the emptyState Group View
     */
    private void fillEmptyStateView(int emptyStateImageId, int emptyStateTitleId, int emptyStateSubtitleId) {

        // Set the correct image into the empty state image view
        ImageView emptyStateImage = (ImageView) mEmptyStateView.findViewById(R.id.empty_state_image);
        emptyStateImage.setImageResource(emptyStateImageId);

        // Set the correct text into the empty state title text
        TextView emptyStateTitleText = (TextView) mEmptyStateView.findViewById(R.id.empty_state_title);
        emptyStateTitleText.setText(emptyStateTitleId);

        // Set the correct text into the empty state subtitle text
        TextView emptyStateSubTitleText = (TextView) mEmptyStateView.findViewById(R.id.empty_state_subtitle);
        emptyStateSubTitleText.setText(emptyStateSubtitleId);
    }

    /** Get executed when the loader is initiated */
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Make an Uri Builder with the Google Request Url as the base Uri
        Uri baseUri = Uri.parse(GOOGLE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Add 2 query parameter to the base Uri.
        // maxResults : determine the maximum number of elements
        // that should be downloaded from the API.
        // q: represent the book genre for the books that will be
        // downloaded from the API.

        uriBuilder.appendQueryParameter("maxResults", MAX_RESULT);
        uriBuilder.appendQueryParameter("q", mBookGenre.trim());

        // This will execute the network request needed to get the data
        // from the API and return the data to onLoadFinished
        return new BookLoader(this, uriBuilder.toString());
    }

    /** Get executed when the background thread finishes the work */
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        // Set empty state view with the content of the "no result" state
        // This will explain to the user that no result where found for the
        // key word he entered
        fillEmptyStateView(R.drawable.empty_state_search,
                R.string.no_results_title,
                R.string.no_results_subtitle);

        // Hide the loading spinner
        mProgressSpinner.setVisibility(View.GONE);

        // Clear the adapter of the data of previous books
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    /** This will reset the previous created loader */
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        // Create a new empty book list for the Adapter
        mAdapter.addAll(new ArrayList<Book>());
    }

}
