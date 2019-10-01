package edu.bluejack182.defilm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewDetail extends AppCompatActivity implements View.OnClickListener{
    private RatingBar ratingBar;
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Movie movie;
    TextView txtReview;
    SharedPreferences sharedPreferences;
    final List<Review> reviewList = new ArrayList<>();
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setNumStars(5);

        Button btnAddReview = findViewById(R.id.btn_add_review);
        btnAddReview.setOnClickListener(this);

        txtReview = findViewById(R.id.txt_review);

        reviewAdapter = new ReviewAdapter(ReviewDetail.this, R.layout.review_detail, reviewList);

        movie = (Movie) getIntent().getSerializableExtra("movie");


        final ListView listView = findViewById(R.id.list_review);
        listView.setAdapter(reviewAdapter);
        getReviewFromDatabase();
    }

    public void getReviewFromDatabase(){
        reviewList.clear();
        Query query3 = databaseReference.child("movies").orderByChild("imdbID").equalTo(movie.getImdbID()).limitToFirst(1);

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
//                    Movie movie = sp.getValue(Movie.class);
//                    Toast.makeText(MovieDetailActivity.this, movie.getTitle(), Toast.LENGTH_SHORT).show();

                    Query query4 = databaseReference.child("movies").child(sp.getKey()).child("review");

                    query4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot sp : dataSnapshot.getChildren()){
                                Review review = sp.getValue(Review.class);
                                Toast.makeText(ReviewDetail.this, review.getText(), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(MovieDetailActivity.this, sp.getKey(), Toast.LENGTH_SHORT).show();
                                reviewList.add(review);
                            }
                            reviewAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_review:
                Query query1 = databaseReference.child("movies").orderByChild("imdbID").equalTo(movie.getImdbID()).limitToFirst(1);
                final String user = sharedPreferences.getString("user", "");
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot sp : dataSnapshot.getChildren()){
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(user).child("id").setValue(user);
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(user).child("text").setValue(txtReview.getText().toString());
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(user).child("rating").setValue(ratingBar.getRating());
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(user).child("user").setValue(sharedPreferences.getString("username", ""));
                            getReviewFromDatabase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
