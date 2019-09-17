package edu.bluejack182.defilm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager sliderpager;
    private List<Movie> movieList;
    private Button btnAddToMovieList;
    private Button btnShowReview;

    private ImageView posterView;
    private TextView titleView;
    private TextView sinopsisView;
    private TextView genreView;
    private TextView releasedView;
    private TextView runtimeView;
    private TextView ratingView;
    private VideoView videoView;
    private EditText txtReview;
    private RatingBar ratingBar;

    private DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    private List<Review> reviewList;

    Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        movie = (Movie) getIntent().getSerializableExtra("movie");
        final String genres[] = movie.getGenre().split(", ");

        Uri uri = Uri.parse("");

        if(movie.getVideo() != null){
            uri = Uri.parse(movie.getVideo());
        }

//        videoView.requestFocus();

//        videoView.start();

        sliderpager = findViewById(R.id.slider_recommendation);
        movieList = new ArrayList<>();
//        slideList.add(new Slide(R.drawable.dumbo, "Dumbo"));
//        slideList.add(new Slide(R.drawable.salt, "Salt"));
//        slideList.add(new Slide(R.drawable.toystory, "Toy Story"));

        final SliderPagerAdapter adapter = new SliderPagerAdapter(this, movieList);
        sliderpager.setAdapter(adapter);

        Query query = FirebaseDatabase.getInstance().getReference("movies").orderByChild("rating");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    Movie movies = sp.getValue(Movie.class);


                    for(int i = 0; i<genres.length; i++){
                        if(movies.getGenre().contains(genres[i])){
//                            Log.d("genre", movies.getGenre());
//                            Log.d("genre asal", genres[i]);
                            movieList.add(movies);
                            break;
                        }
                    }
                }
                adapter.setMovieList(movieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        btnAddToMovieList = findViewById(R.id.btn_add_to_movielist);
        btnAddToMovieList.setOnClickListener(this);

        btnShowReview = findViewById(R.id.btn_show_review);
        btnShowReview.setOnClickListener(this);

        videoView = findViewById(R.id.video_trailer);
        posterView = findViewById(R.id.detail_poster_img_view);
        titleView = findViewById(R.id.txt_title);
        sinopsisView = findViewById(R.id.txt_synopsis);
        genreView = findViewById(R.id.txt_genre);
        releasedView = findViewById(R.id.txt_released);
        runtimeView = findViewById(R.id.txt_runtimme);
        ratingView = findViewById(R.id.txt_rating);

        videoView.setVideoURI(uri);
        new DownloadImageTask(posterView).execute(movie.getPoster());
        titleView.setText(movie.getTitle());
        sinopsisView.setText(movie.getPlot());
        genreView.setText(movie.getGenre());
        releasedView.setText(movie.getReleased());
        runtimeView.setText(movie.getRuntime());
        ratingView.setText(movie.getImdbRating());


//        videoView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(videoView.isPlaying()){
//                    Toast.makeText(MovieDetailActivity.this, "pause", Toast.LENGTH_SHORT).show();
//                    videoView.pause();
//                    mHandler.removeCallbacks(hideControlBar);
//                }
//                else{
//                    Toast.makeText(MovieDetailActivity.this, "play", Toast.LENGTH_SHORT).show();
//                    videoView.start();
//                }
//
//                return false;
//            }
//        });

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        Query query1 = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
                    Query query2 = databaseReference.child("Users").child(sp.getKey()).child("movieList").orderByChild("imdbID").equalTo(movie.getImdbID());

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                btnAddToMovieList.setText("Add to movie list");
                            }
                            else{
                                btnAddToMovieList.setText("Remove from movie list");
                            }
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


        Query query2 = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
                    Query query2 = databaseReference.child("Users").child(sp.getKey()).child("historyView").orderByChild("imdbID").equalTo(movie.getImdbID());

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                databaseReference.child("Users").child(sp.getKey()).child("historyView").child(movie.getImdbID()).setValue(movie);
                            }
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

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setNumStars(5);
        ratingBar.setOnClickListener(this);

        final Button btn = findViewById(R.id.btn_add_review);
        btn.setOnClickListener(this);

        txtReview = findViewById(R.id.txt_review);

        reviewList = new ArrayList<>();
        reviewList.add(new Review(5, "test", "test 2"));
        final ReviewAdapter reviewAdapter = new ReviewAdapter(MovieDetailActivity.this, R.layout.review_detail, reviewList);
        final ListView listView = findViewById(R.id.list_review);
        listView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

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
                                Toast.makeText(MovieDetailActivity.this, review.getText(), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(MovieDetailActivity.this, sp.getKey(), Toast.LENGTH_SHORT).show();
//                                Log.d("tes", sp.getKey());
                                reviewList.add(review);
                                Log.d("x", review.getText());
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
            case R.id.btn_add_to_movielist:
                Query query = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot sp : dataSnapshot.getChildren()){
                            Query query1 = databaseReference.child("Users").child(sp.getKey()).child("movieList").orderByChild("imdbID").equalTo(movie.getImdbID());

                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){
                                        databaseReference.child("Users").child(sp.getKey()).child("movieList").child(movie.getImdbID()).setValue(movie);
                                        btnAddToMovieList.setText("Remove from movie list");
                                        Toast.makeText(MovieDetailActivity.this, "Success Add to movie list", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        databaseReference.child("Users").child(sp.getKey()).child("movieList").child(movie.getImdbID()).removeValue();
                                        btnAddToMovieList.setText("Add to movie list");
                                        Toast.makeText(MovieDetailActivity.this, "Success delete from movie list", Toast.LENGTH_SHORT).show();
                                    }
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
                break;
            case R.id.btn_add_review:
                Query query1 = databaseReference.child("movies").orderByChild("imdbID").equalTo(movie.getImdbID()).limitToFirst(1);

                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot sp : dataSnapshot.getChildren()){
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(sharedPreferences.getString("user", "")).child("text").setValue(txtReview.getText().toString());
                            databaseReference.child("movies").child(sp.getKey()).child("review").child(sharedPreferences.getString("user", "")).child("rating").setValue(ratingBar.getRating());
                            Toast.makeText(MovieDetailActivity.this,sp.getKey() , Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MovieDetailActivity.this,txtReview.getText().toString() , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.btn_show_review:
                View v = findViewById(R.id.layout_review);
                if(v.getVisibility() == View.GONE){
                    v.setVisibility(View.VISIBLE);
                }else{
                    v.setVisibility(View.GONE);
                }


                break;
        }
    }
}
