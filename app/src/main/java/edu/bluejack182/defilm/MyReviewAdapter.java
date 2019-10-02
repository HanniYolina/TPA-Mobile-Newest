package edu.bluejack182.defilm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

public class MyReviewAdapter extends ArrayAdapter<Review> {
    ReviewRatingFragment context;
    int layoutResourceId;
    List<Review> reviews;
    Button btn_delete;

    public MyReviewAdapter(@NonNull ReviewRatingFragment context, int resource, List<Review> reviews) {
        super(context.getContext(), resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.reviews = reviews;
    }

    static class DataHolder{
        TextView review;
        TextView rating;
        TextView user;
        TextView movie;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d("TEs", "called");

        MyReviewAdapter.DataHolder dataHolder;
        if(convertView == null){
            LayoutInflater inflater = (context.getActivity()).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new MyReviewAdapter.DataHolder();
            dataHolder.review = convertView.findViewById(R.id.txt_review);
            dataHolder.user = convertView.findViewById(R.id.txt_user);
            dataHolder.rating = convertView.findViewById(R.id.txt_rating);
            dataHolder.movie = convertView.findViewById(R.id.txt_movie);
            btn_delete = convertView.findViewById(R.id.btn_delete);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (MyReviewAdapter.DataHolder) convertView.getTag();
        }

        final Review review = reviews.get(position);

        dataHolder.review.setText(review.getText());
        dataHolder.rating.setText(Double.toString(review.getRating()));
        dataHolder.user.setText(review.getUser());
        dataHolder.movie.setText(review.getMovieName());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context.getContext(), "tessss", Toast.LENGTH_SHORT).show();
                databaseReference.child("movies").child(review.getMovieID()).child("review").child(review.getId()).removeValue();
                context.getDataFromFirebase();
            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
//        Log.d("TEs", reviews.size() + " notified");
    }

    @Override
    public int getCount() {
        return reviews.size();
    }
}
