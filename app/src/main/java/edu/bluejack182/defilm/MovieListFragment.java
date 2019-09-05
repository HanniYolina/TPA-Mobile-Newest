package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
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

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieListFragment extends Fragment implements View.OnClickListener {

    CustomAdapter customAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    private OnFragmentInteractionListener mListener;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance(String param1, String param2) {
        MovieListFragment fragment = new MovieListFragment();
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

    View view;
    final List<Movie> movieList = new ArrayList<>();;
    ViewGroup container;
    TextView txtTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        txtTitle = view.findViewById(R.id.txt_title);

        this.container = container;
        Button btn = view.findViewById(R.id.btn_filter);
        btn.setOnClickListener(this);

        sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query1 = databaseReference.child("Users").child(sharedPreferences.getString("user", "")).child("movieList");
//        Toast.makeText(this.getActivity(), "tes", Toast.LENGTH_SHORT).show();
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot sp : dataSnapshot.getChildren()){

                    movieList.add(sp.getValue(Movie.class));
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        movieList.add(new Movie(R.drawable.poster_dumbo, "Dumbo", 3.7f));
//        movieList.add(new Movie(R.drawable.poster_toystory, "Toy Story", 3.7f));
//        movieList.add(new Movie(R.drawable.poster_toystory, "Toy Story", 3.7f));

        setItemList();
        return view;
    }

    public void setItemList(){
        final ListView listView = view.findViewById(R.id.list_movie);

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
    }

    public void filter(){
        movieList.removeAll(movieList);
        Query query1 = databaseReference.child("Users").child(sharedPreferences.getString("user", "")).child("movieList");
        Toast.makeText(this.getActivity(), "tes", Toast.LENGTH_SHORT).show();
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    Movie movie = sp.getValue(Movie.class);
                    if(movie.getTitle().toLowerCase().contains(txtTitle.getText().toString().toLowerCase())){
                        movieList.add(movie);
                    }
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        switch (view.getId()){
            case R.id.btn_filter:
                filter();
                break;
        }
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
