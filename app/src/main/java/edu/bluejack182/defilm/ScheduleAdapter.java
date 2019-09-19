package edu.bluejack182.defilm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.bluejack182.defilm.ui.main.Schedule;

public class ScheduleAdapter extends ArrayAdapter<Schedule> implements View.OnClickListener {
    ScheduleFragment context;
    int layoutResourceId;
    List<Schedule> data;
    Schedule schedule;

    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    DataHolder dataHolder;

    public ScheduleAdapter(ScheduleFragment context, int layoutResourceId, List<Schedule> data) {
        super(context.getContext(), layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    static class DataHolder{
        EditText description;
        Button delBtn;
        Button updateBtn;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context.getContext()).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new DataHolder();
            dataHolder.description = convertView.findViewById(R.id.txt_description);
            dataHolder.delBtn = convertView.findViewById(R.id.btn_del_desc);
            dataHolder.updateBtn = convertView.findViewById(R.id.btn_update_desc);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (ScheduleAdapter.DataHolder) convertView.getTag();
        }
        final int pos = position;
        schedule = data.get(pos);

        dataHolder.description.setHint(schedule.getDescription());
        dataHolder.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                data.get(pos).setDescription(editable.toString());
            }
        });
        dataHolder.delBtn.setOnClickListener(this);
        dataHolder.updateBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Query query = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot sp : dataSnapshot.getChildren()){
                            Toast.makeText(getContext(), data.get(pos).getDescription(), Toast.LENGTH_SHORT).show();

                            databaseReference.child("Users").child(sp.getKey()).child("schedule").child(schedule.getId()).child("description").setValue(data.get(pos).getDescription());
                        }

                        context.getEventFromFirebase(context.getView());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                Toast.makeText(getContext(), data.get(pos).getDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = context.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_del_desc:
                delSchedule();
                break;
            case R.id.btn_update_desc:
                updateSchedule();
                break;
        }
    }



    public void delSchedule(){
        Query query = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
                    databaseReference.child("Users").child(sp.getKey()).child("schedule").child(schedule.getId()).removeValue();
                }

                context.getEventFromFirebase(context.getView());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateSchedule(){
        Query query = databaseReference.child("Users").orderByChild("email").equalTo(sharedPreferences.getString("email", "")).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot sp : dataSnapshot.getChildren()){
                    Toast.makeText(getContext(), dataHolder.description.getText().toString(), Toast.LENGTH_SHORT).show();

//                    databaseReference.child("Users").child(sp.getKey()).child("schedule").child(schedule.getId()).setValue(dataHolder.description.getText());
                }

                context.getEventFromFirebase(context.getView());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
