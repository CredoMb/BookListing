package com.example.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Variable to store the JSON response for a USGS query
     */
    private static String JSON_RESPONSE;

    /**
     * Variable to store the Google books Request Url
     */
    private static String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = SearchResultActivity.class.getSimpleName();

    /**
     * This is a sample json response to help us test the last function
     */
    private String SAMPLE_JSON_RESPONSE = theSampleJson();

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private static String makeHttpRequest(java.net.URL url) throws IOException {
        String jsonResponse = "";

        // Check if the url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // The internet connection is needed before executing the next line !
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect(); // Did the connexion lead to a successful transfert ? That's a good question !

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream(); // Check if the inputStream has a bad value, like null, right ?
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
            // TODO: Handle the exception

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;

        // Returns the actual Json from the URL
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} by Fetching data from the USGS server
     */

    public static ArrayList<Book> fetchBooksData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        }
        Log.w(LOG_TAG, "This is the \"fetchBookData\" method");
        return extractBooks(jsonResponse);
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */

    public static ArrayList<Book> extractBooks(String jsonResponse) {

        // If the JSON string is empty or null, then return null.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Convert the string of Json received from the Google API to a JSONObject
            // and JSONArray
            JSONObject JSONbookObject = new JSONObject(jsonResponse);
            JSONArray JSONbookArray = JSONbookObject.optJSONArray("items");

            // Loop through the JSONbookArray to extract informations about each book
            for (int i = 0; i < JSONbookArray.length(); i++) {

                // Extract the VolumeInfo of the Book positionned at the index "i"
                JSONObject JSONbookVolumeInfoObject = JSONbookArray.optJSONObject(i).optJSONObject("volumeInfo");

                // Get the array of authors from the "VolumeInfo" JSON object
                JSONArray JSONAuthorsArray = JSONbookVolumeInfoObject.optJSONArray("authors");
                List<String> authorsList = new ArrayList<String>();
                authorsList = extractAuthors(JSONAuthorsArray);


                // From the VolumeInfo object, get datas (title,averageRating, ratingsCount & infoLink)
                // to create a new Book inside the Book's ArrayList
                books.add(new Book(JSONbookVolumeInfoObject.optString("title"),
                        // the authorsList could be "null", if so, put "no info about the author"
                        authorsList,
                        (float) JSONbookVolumeInfoObject.optDouble("averageRating"),
                        JSONbookVolumeInfoObject.optInt("ratingsCount"),
                        JSONbookVolumeInfoObject.optString("infoLink"))
                );
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        return books;
    }

    /**
     * This function will extract the authors from the JsonArray and return a List of String
     * that contains all the authors
     */
    private static List<String> extractAuthors(JSONArray JSONAuthorsArray) {

        List<String> authors = new ArrayList<String>();

        if (JSONAuthorsArray != null && JSONAuthorsArray.length() > 0) {
            for (int i = 0; i < JSONAuthorsArray.length(); i++) {
                authors.add(JSONAuthorsArray.optString(i)); // What to do now ?
            }
        }
        return authors;
    }

    /**
     * Returns a String that represent a Json Response from the Google API
     */

    public static String theSampleJson() {
        return "{\n" +
                " \"kind\": \"books#volumes\",\n" +
                " \"totalItems\": 2308,\n" +
                " \"items\": [\n" +
                "  {\n" +
                "   \"kind\": \"books#volume\",\n" +
                "   \"id\": \"XR2BcRwiG-sC\",\n" +
                "   \"etag\": \"xDPAuk4FwFY\",\n" +
                "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/XR2BcRwiG-sC\",\n" +
                "   \"volumeInfo\": {\n" +
                "    \"title\": \"Making Sex\",\n" +
                "    \"subtitle\": \"Body and Gender from the Greeks to Freud\",\n" +
                "    \"authors\": [\n" +
                "     \"Thomas Walter Laqueur\"\n" +
                "    ],\n" +
                "    \"publisher\": \"Harvard University Press\",\n" +
                "    \"publishedDate\": \"1992\",\n" +
                "    \"description\": \"History of sex in the West from the ancients to the moderns by describing the developments in reproductive anatomy and physiology.\",\n" +
                "    \"industryIdentifiers\": [\n" +
                "     {\n" +
                "      \"type\": \"ISBN_10\",\n" +
                "      \"identifier\": \"0674543556\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"type\": \"ISBN_13\",\n" +
                "      \"identifier\": \"9780674543553\"\n" +
                "     }\n" +
                "    ],\n" +
                "    \"readingModes\": {\n" +
                "     \"text\": false,\n" +
                "     \"image\": true\n" +
                "    },\n" +
                "    \"pageCount\": 313,\n" +
                "    \"printType\": \"BOOK\",\n" +
                "    \"categories\": [\n" +
                "     \"Psychology\"\n" +
                "    ],\n" +
                "    \"averageRating\": 4.0,\n" +
                "    \"ratingsCount\": 3,\n" +
                "    \"maturityRating\": \"MATURE\",\n" +
                "    \"allowAnonLogging\": false,\n" +
                "    \"contentVersion\": \"1.0.3.0.preview.1\",\n" +
                "    \"panelizationSummary\": {\n" +
                "     \"containsEpubBubbles\": false,\n" +
                "     \"containsImageBubbles\": false\n" +
                "    },\n" +
                "    \"imageLinks\": {\n" +
                "     \"smallThumbnail\": \"http://books.google.com/books/content?id=XR2BcRwiG-sC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",\n" +
                "     \"thumbnail\": \"http://books.google.com/books/content?id=XR2BcRwiG-sC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"\n" +
                "    },\n" +
                "    \"language\": \"en\",\n" +
                "    \"previewLink\": \"http://books.google.ca/books?id=XR2BcRwiG-sC&printsec=frontcover&dq=sex&hl=&cd=1&source=gbs_api\",\n" +
                "    \"infoLink\": \"http://books.google.ca/books?id=XR2BcRwiG-sC&dq=sex&hl=&source=gbs_api\",\n" +
                "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Making_Sex.html?hl=&id=XR2BcRwiG-sC\"\n" +
                "   },\n" +
                "   \"saleInfo\": {\n" +
                "    \"country\": \"CA\",\n" +
                "    \"saleability\": \"NOT_FOR_SALE\",\n" +
                "    \"isEbook\": false\n" +
                "   },\n" +
                "   \"accessInfo\": {\n" +
                "    \"country\": \"CA\",\n" +
                "    \"viewability\": \"PARTIAL\",\n" +
                "    \"embeddable\": true,\n" +
                "    \"publicDomain\": false,\n" +
                "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
                "    \"epub\": {\n" +
                "     \"isAvailable\": false\n" +
                "    },\n" +
                "    \"pdf\": {\n" +
                "     \"isAvailable\": false\n" +
                "    },\n" +
                "    \"webReaderLink\": \"http://play.google.com/books/reader?id=XR2BcRwiG-sC&hl=&printsec=frontcover&source=gbs_api\",\n" +
                "    \"accessViewStatus\": \"SAMPLE\",\n" +
                "    \"quoteSharingAllowed\": false\n" +
                "   },\n" +
                "   \"searchInfo\": {\n" +
                "    \"textSnippet\": \"History of sex in the West from the ancients to the moderns by describing the developments in reproductive anatomy and physiology.\"\n" +
                "   }\n" +
                "  }]}";
    }
}
