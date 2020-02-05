package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View simpleView = convertView;

        if (simpleView == null) {
            simpleView = LayoutInflater.from(getContext()).inflate(R.layout.item_of_list, parent, false);
        }

        // Get the current Book
        Book currentBook = getItem(position);

        // Extract the average rating from the current Book and place it inside the rating bar.
        RatingBar ratingBar = (RatingBar) simpleView.findViewById(R.id.rating_bar);
        ratingBar.setRating(currentBook.getRating());

        // Get the textView of the book's title
        TextView bookTitleTextView = (TextView) simpleView.findViewById(R.id.book_title_text_view);
        bookTitleTextView.setText(currentBook.getTitle());

        // Get the textView of the authors
        TextView authorsTextView = (TextView) simpleView.findViewById(R.id.authors_text_view);
        authorsTextView.setText(currentBook.getAuthors());

        // Extract the TextView that holds the number of reviews
        TextView ratingCountTextView = (TextView) simpleView.findViewById(R.id.reviewer_count_text_view);
        // Convert the number of reviewers from a integer to a string
        String ratingCount = String.format("%d", currentBook.getRatingCount());

        // Add the text that represent the number of reviewers
        // inside the ratingCount TextView
        ratingCountTextView.setText(ratingCount + " " + getContext().getString(R.string.reviewers));

        return simpleView;
    }
}
