package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.bluejack182.defilm.ui.main.SectionsPagerAdapter;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener, ResultFragment.OnFragmentInteractionListener {

    SearchView searchView;
    ArrayList<Movie> movieList = new ArrayList<>();
    final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(SearchActivity.this, getSupportFragmentManager(), movieList);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ViewPager viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
//                Toast.makeText(SearchActivity.this, searchView.getQuery(), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

                FirebaseDatabase.getInstance().getReference().child("Users").child(sharedPreferences.getString("user","")).child("historySearch").setValue(query);


                movieList.removeAll(movieList);
                Query query1 = FirebaseDatabase.getInstance().getReference("movies");

                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot sp : dataSnapshot.getChildren()){
                            Movie movie = sp.getValue(Movie.class);
                            if(movie.getTitle().toLowerCase().contains(query)){
                                movieList.add(movie);
                            }
                        }
                        sectionsPagerAdapter.setMovieList(movieList);
                        sectionsPagerAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
    private static final int REQUEST_CODE = 1234;

    public void speakButtonClicked(View v)
    {
//        Toast.makeText(this, "masuk speak button", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your device not support speech input", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();

                    Query query1 = FirebaseDatabase.getInstance().getReference( "movies");

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                movieList.removeAll(movieList);
                                for (DataSnapshot sp : dataSnapshot.getChildren()){
                                    Movie movie = sp.getValue(Movie.class);
                                    if(movie.getTitle().toLowerCase().contains(result.get(0))){
                                        movieList.add(movie);
                                    }
                                    sectionsPagerAdapter.setMovieList(movieList);
                                    sectionsPagerAdapter.notifyDataSetChanged();
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}