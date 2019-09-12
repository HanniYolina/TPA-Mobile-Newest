package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    CustomAdapter customAdapter;
    final List<Movie> movieList = new ArrayList<>();
    TextView txtTitle;
    TextView txtRating;
    TextView txtGenre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btn = view.findViewById(R.id.btn_filter);
        btn.setOnClickListener(this);

        txtTitle = view.findViewById(R.id.txt_title);
        txtGenre = view.findViewById(R.id.txt_genre);
        txtRating = view.findViewById(R.id.txt_rating);

        final ListView listView = view.findViewById(R.id.list_search_result);

        customAdapter = new CustomAdapter(container.getContext(), R.layout.movie_list_layout, movieList);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Object o = listView.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra("movie", movieList.get(position));
                startActivity(intent);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_filter:
                filter();
                break;
        }
    }

    public void filter(){
        movieList.removeAll(movieList);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String title = txtTitle.getText().toString().toLowerCase();
        final String genre = txtGenre.getText().toString().toLowerCase();
//        final double rating;

        Log.d("genre", genre);

        Query query1 = databaseReference.child("movies").orderByChild("Title");
//        Toast.makeText(this.getActivity(), "filter", Toast.LENGTH_SHORT).show();

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    Movie mv1 = sp.getValue(Movie.class);

                    if(!title.equals("")){
                        if(mv1.getTitle().toLowerCase().contains(title)){
                            Query query2 = databaseReference.child("movies").child(sp.getKey());

                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        Movie mv2 = dataSnapshot.getValue(Movie.class);

                                        if(!genre.equals("")){
                                            if(mv2.getGenre().toLowerCase().contains(genre)){
                                                double rating = 0;
                                                double mvRating = -1;

                                                if(!txtRating.getText().toString().equals("")){
                                                    try {
                                                        rating = Double.parseDouble(txtRating.getText().toString());
                                                    }catch (NumberFormatException e) {
                                                        Toast.makeText(getContext(), "Rating must be number", Toast.LENGTH_SHORT).show();
                                                        rating = 0;
                                                    }

                                                    try {
                                                        mvRating = Double.parseDouble(mv2.getImdbRating());
                                                    }catch (NumberFormatException e) {
                                                        mvRating = -1;
                                                    }

                                                    if(mvRating != -1){
                                                        if(mvRating >= rating){
                                                            movieList.add(mv2);
                                                        }
                                                    }

                                                }
                                                else{
                                                    movieList.add(mv2);
                                                }

                                            }
                                        }
                                        else{
                                            double rating = 0;
                                            double mvRating = -1;

                                            if(!txtRating.getText().toString().equals("")){
                                                try {
                                                    rating = Double.parseDouble(txtRating.getText().toString());
                                                }catch (NumberFormatException e) {
                                                    Toast.makeText(getContext(), "Rating must be number", Toast.LENGTH_SHORT).show();
                                                    rating = 0;
                                                }

                                                try {
                                                    mvRating = Double.parseDouble(mv2.getImdbRating());
                                                }catch (NumberFormatException e) {
                                                    mvRating = -1;
                                                }

                                                if(mvRating != -1){
                                                    if(mvRating >= rating){
                                                        movieList.add(mv2);
                                                    }
                                                }

                                            }
                                            else{
                                                movieList.add(mv2);
                                            }
                                        }
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    else{
                        Query query2 = databaseReference.child("movies").child(sp.getKey());

                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Movie mv2 = dataSnapshot.getValue(Movie.class);

                                if(!genre.equals("")){
                                    if(mv2.getGenre().toLowerCase().contains(genre)){
                                        double rating = 0;
                                        double mvRating = -1;

                                        if(!txtRating.getText().toString().equals("")){
                                            try {
                                                rating = Double.parseDouble(txtRating.getText().toString());
                                            }catch (NumberFormatException e) {
                                                Toast.makeText(getContext(), "Rating must be number", Toast.LENGTH_SHORT).show();
                                                rating = 0;
                                            }

                                            try {
                                                mvRating = Double.parseDouble(mv2.getImdbRating());
                                            }catch (NumberFormatException e) {
                                                mvRating = -1;
                                            }

                                            if(mvRating != -1){
                                                if(mvRating >= rating){
                                                    movieList.add(mv2);
                                                }
                                            }

                                        }
                                        else{
                                            movieList.add(mv2);
                                        }

                                    }
                                }
                                else{
                                    double rating = 0;
                                    double mvRating = -1;

                                    if(!txtRating.getText().toString().equals("")){
                                        try {
                                            rating = Double.parseDouble(txtRating.getText().toString());
                                        }catch (NumberFormatException e) {
                                            Toast.makeText(getContext(), "Rating must be number", Toast.LENGTH_SHORT).show();
                                            rating = 0;
                                        }

                                        try {
                                            mvRating = Double.parseDouble(mv2.getImdbRating());
                                        }catch (NumberFormatException e) {
                                            mvRating = -1;
                                        }

                                        if(mvRating != -1){
                                            if(mvRating >= rating){
                                                movieList.add(mv2);
                                            }
                                        }

                                    }
                                    else{
                                        movieList.add(mv2);
                                    }
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
