package edu.bluejack182.defilm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    Context context;
    int layoutResourceId;
    List<Review> reviews;

    public ReviewAdapter(@NonNull Context context, int resource, List<Review> reviews) {
        super(context, resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.reviews = reviews;
    }

    static class DataHolder{
        TextView review;
        TextView rating;
        TextView user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TEs", "called");

        DataHolder dataHolder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new DataHolder();
            dataHolder.review = convertView.findViewById(R.id.txt_review);
            dataHolder.user = convertView.findViewById(R.id.txt_user);
            dataHolder.rating = convertView.findViewById(R.id.txt_rating);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (DataHolder) convertView.getTag();
        }

        Review review = reviews.get(position);

        Log.d("TEs", review.getText());
        Log.d("TEs", reviews.size() + "");
        Toast.makeText(context, "Halo" + review.getText(), Toast.LENGTH_SHORT).show();

        dataHolder.review.setText(review.getText());
        dataHolder.rating.setText(Double.toString(review.getRating()));
        dataHolder.user.setText(review.getUser());

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("TEs", reviews.size() + " notified");
    }
}
