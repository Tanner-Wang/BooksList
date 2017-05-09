package com.example.android.bookslist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

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

    static final String NON_IMAGE_URL =
            "https://b-ssl.duitang.com/uploads/item/201501/15/20150115230315_fVrU3.jpeg";

    private QueryUtils() {

    }

    public static List<Book> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Book> currentBooks = extractFeatureFromJson(jsonResponse);

        return currentBooks;
    }

    private static URL createUrl(String url) {
        URL mUrl = null;
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return mUrl;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
            if (inputStream != null) {
                inputStream.close();
            }

        }
        return jsonResponse;
    }

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

    private static List<Book> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<Book> books = new ArrayList<Book>();
        String currentImageUrl;
        Bitmap bookImage;
        String title;
        String author;
        String publishedDate;
        String webReaderLink;
        try {

            JSONObject root = new JSONObject(jsonResponse);
            JSONArray resultsArray = root.getJSONArray("items");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject book = resultsArray.getJSONObject(i);
                JSONObject bookResult = book.getJSONObject("volumeInfo");

                if (bookResult.has("title")) {
                    title = bookResult.getString("title");
                    if (title.trim().equals("")) {
                        title = "Unknown";
                    }
                } else {
                    title = "Unknown";
                }
                if (bookResult.has("authors")) {
                    JSONArray authorArray = bookResult.getJSONArray("authors");
                    author = authorArray.getString(0);
                    for (int j = 1; j < authorArray.length(); j++) {
                        author += ", " + authorArray.getString(j);
                    }
                    if (author.trim().equals("")) {
                        author = "Unknown";
                    }
                } else {
                    author = "Unknown";
                }
                if (bookResult.has("publishedDate")) {
                    publishedDate = bookResult.getString("publishedDate");
                    if (publishedDate.trim().equals("")) {
                        publishedDate = "Unknown";
                    }
                } else {
                    publishedDate = "Unknown";
                }
                JSONObject accessInfo = book.getJSONObject("accessInfo");
                if (accessInfo.has("webReaderLink")) {
                    webReaderLink = accessInfo.getString("webReaderLink");
                } else {
                    webReaderLink = null;
                }
                if (bookResult.has("imageLinks")) {
                    JSONObject imageUrl = bookResult.getJSONObject("imageLinks");
                    if (imageUrl.has("thumbnail")) {
                        currentImageUrl = imageUrl.getString("thumbnail");
                    } else {
                        currentImageUrl = NON_IMAGE_URL;
                    }
                } else {
                    currentImageUrl = NON_IMAGE_URL;
                }
                bookImage = returnBitMap(currentImageUrl);

                Book mBook = new Book(bookImage, title, author, publishedDate, webReaderLink);

                books.add(mBook);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }

    private static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}