package com.example.android.booklisting;

import java.util.ArrayList;
import java.util.List;


public class Book {

    /**
     * Minimum lenght of the author's array
     */
    private final int ONE_AUTHOR_ONLY = 1;

    /**
     * The Index of the first author
     */
    private final int FIRST_AUTHOR_INDEX = 0;

    /**
     * Will be used for books with no authors
     */
    private final String UNKNOWN_AUTHOR = "UNKNOWN AUTHOR";

    /**
     * Attributes
     */
    private String mTitle;
    private List<String> mAuthors;
    private float mRating;
    private int mRatingsCount;
    private String mExploreLink;

    /**
     * Constructor
     */
    public Book(String title, List<String> authors, float rating, int ratingsCount, String exploreLink) {
        mTitle = title;
        mAuthors = new ArrayList<String>();

        // Check if there's at least one author
        if (authors.size() > 0) {
            // Add the first author inside the Author's list
            mAuthors.add(authors.get(FIRST_AUTHOR_INDEX));
        }

        // Check if there is more than one author
        if (authors.size() > ONE_AUTHOR_ONLY) {

            // Add the "cie" mark as a sign that there are other authors
            mAuthors.add(" & cie");
        }

        mRating = rating;
        mRatingsCount = ratingsCount;
        mExploreLink = exploreLink;
    }

    /**
     * Getter methods for the attributes
     */
    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        if (mAuthors.size() > 0) {

            // Return the name of the auhor
            return mAuthors.get(FIRST_AUTHOR_INDEX);
        }
        return UNKNOWN_AUTHOR;
    }

    public float getRating() {

        // If there is no ratings, return "0.0" as the rating value
        if (Float.isNaN(mRating)) {
            mRating = 0.0f;
        }
        return mRating;
    }

    public int getRatingCount() {
        return mRatingsCount;
    }

    public String getExploreLink() {
        return mExploreLink;
    }
}
