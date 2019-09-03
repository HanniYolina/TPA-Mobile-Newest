package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SliderPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Slide> slideList;

    public SliderPagerAdapter(Context context, List<Slide> slideList) {
        this.context = context;
        this.slideList = slideList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slider_item, null);

        ImageView slideImg = slideLayout.findViewById(R.id.slider_comingsoon);
        TextView slideText = slideLayout.findViewById(R.id.slider_title);

//        URL url = null;
//        try {
//            url = new URL("https://m.media-amazon.com/images/M/MV5BZjE2MGVkMTAtMWIwYy00YzQ5LWE2YTAtMTU2NGJmNGNjY2IyXkEyXkFqcGdeQXVyNjMxMzM3NDI@._V1_SX300.jpg");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        Bitmap bmp = null;
//        try {
//            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            slideImg.setImageBitmap(bmp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        slideImg.setImageURI(Uri.parse("https://m.media-amazon.com/images/M/MV5BZjE2MGVkMTAtMWIwYy00YzQ5LWE2YTAtMTU2NGJmNGNjY2IyXkEyXkFqcGdeQXVyNjMxMzM3NDI@._V1_SX300.jpg"));
//        slideImg.setImageResource(slideList.get(position).getImage());

        new DownloadImageTask(slideImg)
                .execute("https://m.media-amazon.com/images/M/MV5BZjE2MGVkMTAtMWIwYy00YzQ5LWE2YTAtMTU2NGJmNGNjY2IyXkEyXkFqcGdeQXVyNjMxMzM3NDI@._V1_SX300.jpg");


//        slideText.setText(slideList.get(position).getTitle());

        slideLayout.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                context.startActivity(intent);
            }
        });
        container.addView(slideLayout);
        return slideLayout;
    }

    @Override
    public int getCount() {
        return slideList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
