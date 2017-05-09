package com.example.android.bookslist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<Book> {

    static ViewHolder holder = new ViewHolder();

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate
                    (R.layout.book_item, parent, false);
        }

        final Book currentBook = getItem(position);

        holder.image = (ImageView) convertView.findViewById(R.id.book_image);
        holder.image.setImageBitmap(currentBook.getmImage());

        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.title.setText(currentBook.getmTitle());

        holder.author = (TextView) convertView.findViewById(R.id.author);
        holder.author.setText(currentBook.getmAuthor());

        holder.publishedDate = (TextView) convertView.findViewById(R.id.published_date);
        holder.publishedDate.setText(currentBook.getmPublishedDate());

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView publishedDate;
        TextView title;
        TextView author;
    }
}
