package edu.bluejack182.defilm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager sliderpager;
    private List<Slide> slideList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        VideoView videoView = findViewById(R.id.video_slider);
        Uri uri = Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.start();

        sliderpager = findViewById(R.id.slider_recommendation);
//        slideList = new ArrayList<>();
//        slideList.add(new Slide(R.drawable.dumbo, "Dumbo"));
//        slideList.add(new Slide(R.drawable.salt, "Salt"));
//        slideList.add(new Slide(R.drawable.toystory, "Toy Story"));

        SliderPagerAdapter adapter = new SliderPagerAdapter(this, slideList);
        sliderpager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

    }
}
