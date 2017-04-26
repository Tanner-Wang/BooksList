package com.example.android.bookslist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 0;
    static ViewHolder holder = new ViewHolder();
    private static String BOOK_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    // Adapter for the list of books
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        holder.booksListView = (ListView) findViewById(R.id.list);

        holder.mEmptyStateView = (TextView) findViewById(R.id.empty_view);
        holder.booksListView.setEmptyView(holder.mEmptyStateView);


        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        holder.booksListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        holder.booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentBook.getmUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        holder.firstShowBackground = (TextView) findViewById(R.id.firstShowBackground);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            holder.firstShowBackground.setVisibility(View.VISIBLE);
            holder.searchButton = (TextView) findViewById(R.id.search_button);
            holder.searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUi();
                }
            });
        } else {
            holder.firstShowBackground.setVisibility(View.GONE);
            View mLoading = findViewById(R.id.in_loading);
            mLoading.setVisibility(View.GONE);
            holder.mEmptyStateView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUi() {
        holder.firstShowBackground.setVisibility(View.GONE);
        holder.enterTitle = (EditText) findViewById(R.id.enter_title);
        String newBookTitle = holder.enterTitle.getText().toString().trim();

        //定义变量，存储转义后的书名参数，因为输入的书名中有空格请求会因为URL不正确
        //而发生错误，如the book,此处对其进行编码转义，用%20代替空格
        String param = null;

        try {
            // 转码为 UTF-8
            param = URLEncoder.encode(newBookTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        BOOK_REQUEST_URL += param;

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        View mLoading = findViewById(R.id.in_loading);
        mLoading.setVisibility(View.GONE);
        // Clear the adapter of previous book data
        mAdapter.clear();
        holder.mEmptyStateView.setText(R.string.empty_view_text);
        // If there is a valid list of {@link book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            BOOK_REQUEST_URL =
                    "https://www.googleapis.com/books/v1/volumes?q=";
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    static class ViewHolder {
        ListView booksListView;
        TextView mEmptyStateView;
        EditText enterTitle;
        TextView searchButton;
        TextView firstShowBackground;
    }
}
