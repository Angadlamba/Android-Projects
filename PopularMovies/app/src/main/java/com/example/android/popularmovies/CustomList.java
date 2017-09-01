package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Angadlamba21 on 28/12/15.
 */
public class CustomList extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;

    //Store urls of movie posters in the grid.
    String[] urls;

    /**
     * DESCRIPTION: Constructor
     *
     * @param urls
     * @param ctxt
     */
    public CustomList(String[] urls, Context ctxt) {
        this.context = ctxt;
        this.urls = urls;
        layoutInflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * DESCRIPTION: returns number of movie posters.
     *
     * @return int
     */
    @Override
    public int getCount() {
        return urls.length;
    }

    /**
     * DESCRIPTION: return url of movie poster.
     *
     * @param position
     * @return String
     */
    @Override
    public String getItem(int position) {
        return urls[position];
    }

    /**
     * DESCRIPTION: return url position in the gridArray.
     *
     * @param position
     * @return long
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * DESCRIPTION: custom getView. It loads imageview with images(movie posters) using Picasso.
     *
     * @param position
     * @param parent
     * @param convertView
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_item_image, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);

        Picasso.with(context).load(urls[position]).into(imageView);

        return convertView;
    }
}


