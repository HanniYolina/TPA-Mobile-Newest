package edu.bluejack182.defilm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Movie> {

    Context context;
    int layoutResourceId;
    List<Movie> data;

    public CustomAdapter(Context context, int resource, List<Movie> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }

    static class DataHolder{
        ImageView img;
        TextView title;
        TextView rating;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataHolder dataHolder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new DataHolder();
            dataHolder.img = convertView.findViewById(R.id.img_view_poster);
            dataHolder.title = convertView.findViewById(R.id.txt_title);
            dataHolder.rating = convertView.findViewById(R.id.txt_rating);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (DataHolder) convertView.getTag();
        }

        Movie movie = data.get(position);
        dataHolder.img.setImageResource(movie.image);
        dataHolder.title.setText(movie.title);
        dataHolder.rating.setText(movie.rating.toString());

        return convertView;
    }
}
