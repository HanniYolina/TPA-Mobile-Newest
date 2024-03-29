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
import com.google.firebase.database.Query;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    Context context;
    int layoutResourceId;
    List<Review> reviews;
    View layoutBtn;
    Button btn_delete;

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
//        Log.d("TEs", "called");

        DataHolder dataHolder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new DataHolder();
            dataHolder.review = convertView.findViewById(R.id.txt_review);
            dataHolder.user = convertView.findViewById(R.id.txt_user);
            dataHolder.rating = convertView.findViewById(R.id.txt_rating);
            layoutBtn = convertView.findViewById(R.id.layout_btn_delete);
            btn_delete = convertView.findViewById(R.id.btn_delete);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (DataHolder) convertView.getTag();
        }

        final Review review = reviews.get(position);

        dataHolder.review.setText(review.getText());
        dataHolder.rating.setText(Double.toString(review.getRating()));
        dataHolder.user.setText(review.getUser());

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if(review.getId().equals(sharedPreferences.getString("user", ""))){
            layoutBtn.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete
//                    Toast.makeText(context, review.getMovieID(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, review.getId(), Toast.LENGTH_SHORT).show();
                    databaseReference.child("movies").child(review.getMovieID()).child("review").child(review.getId()).removeValue();
                    if(context instanceof  ReviewDetail){
                        ReviewDetail r = (ReviewDetail) context;
                        r.getReviewFromDatabase();
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("TEs", reviews.size() + " notified");
    }

    @Override
    public int getCount() {
        return reviews.size();
    }
}
