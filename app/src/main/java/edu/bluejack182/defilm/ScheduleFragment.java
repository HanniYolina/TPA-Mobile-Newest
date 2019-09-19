package edu.bluejack182.defilm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.bluejack182.defilm.ui.main.Schedule;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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

    CompactCalendarView compactCalendarView;
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    TextView txtDate;
    TextView month;
    EditText edtDesc;
    Button btnAddEvent;
    ListView listView;

    ArrayList<Schedule> scheduleList = new ArrayList<>();
    ArrayList<Schedule> clickedScheduleList = new ArrayList<>();
    ScheduleAdapter scheduleAdapter;

    private void setClickedScheduleList(){
        clickedScheduleList.clear();

        for (Schedule s : scheduleList) {
//                    Toast.makeText(getContext(), "Msk for", Toast.LENGTH_SHORT).show();
            if(s.getDate().equals(txtDate.getText())){
                clickedScheduleList.add(s);
            }
        }

        scheduleAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragments

//        Calendar calendarEvent = Calendar.getInstance();
//        Intent intent = new Intent(Intent.ACTION_EDIT);
//        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra("beginTime", calendarEvent.getTimeInMillis());
//        intent.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
//        intent.putExtra("title", "Sample Event");
//        intent.putExtra("allDay", true);
//        intent.putExtra("rule", "FREQ=YEARLY");
//        startActivity(intent);

        scheduleAdapter = new ScheduleAdapter(this, R.layout.schedule_layout, clickedScheduleList);

        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);

        //set month
        month = view.findViewById(R.id.txt_month);
        String date = monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth());
        month.setText(date);

        txtDate = view.findViewById(R.id.txt_date);
        edtDesc = view.findViewById(R.id.edt_desc);
        btnAddEvent = view.findViewById(R.id.btn_add_desc);
        btnAddEvent.setOnClickListener(this);
        listView = view.findViewById(R.id.list_description);


        txtDate.setText(new Date().toString());

//        Date clickedDate = null;
//        try {
//            clickedDate = dateFormat.parse("20/09/2019");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//        final Event event = new Event(Color.GRAY, clickedDate.getTime(), "Testing day");
//        compactCalendarView.addEvent(event);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        getEventFromFirebase(view);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                txtDate.setText(dateClicked.toString());

                setClickedScheduleList();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String date = monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth());
                month.setText(date);
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



    public void getEventFromFirebase(View view){
//        compactCalendarView.removeAllEvents();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Query query1 = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scheduleList.clear();
                compactCalendarView.removeAllEvents();
                for (DataSnapshot sp : dataSnapshot.getChildren()){
                    Query query2 = databaseReference.child("Users").child(sp.getKey()).child("schedule");

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot sp : dataSnapshot.getChildren()){
                                if(dataSnapshot.exists()){
                                    Schedule schedule = sp.getValue(Schedule.class);

//                                    if(schedule.getDate().equals(txtDate.getText())){
                                        scheduleList.add(schedule);

                                        Event event = new Event(Color.GRAY, new Date(schedule.getDate()).getTime(), edtDesc.getText());
                                        compactCalendarView.addEvent(event);
//
//                                    }

                                    Log.d("Tes", schedule.getDate());

                                }
                            }
                            setClickedScheduleList();
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

        final ListView listView = view.findViewById(R.id.list_description);


        listView.setAdapter(scheduleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
//                intent.putExtra("movie", movieList.get(position));
//                startActivity(intent);
            }
        });
    }

    public void addEventToFirebase(){
        final Date clickedDate = new Date(txtDate.getText().toString());

        Event event = new Event(Color.GRAY, clickedDate.getTime(), edtDesc.getText());
        compactCalendarView.addEvent(event);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Query query1 = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
                    String id = UUID.randomUUID().toString();
                    databaseReference.child("Users").child(sp.getKey()).child("schedule").child(id).child("date").setValue(clickedDate.toString());
                    databaseReference.child("Users").child(sp.getKey()).child("schedule").child(id).child("description").setValue(edtDesc.getText().toString());
                    databaseReference.child("Users").child(sp.getKey()).child("schedule").child(id).child("id").setValue(id);
                }
                getEventFromFirebase(getView());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_desc:
                addEventToFirebase();
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
