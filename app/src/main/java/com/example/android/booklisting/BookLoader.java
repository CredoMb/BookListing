package com.example.android.booklisting;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Query Url
     **/
    private String mUrl;

    /** The constructor  */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    /** This will help load the data */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**This will cause the "QueryUtils.fetchBooksData" to be executed on a background
      thread  */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchBooksData(mUrl);
    }

}
