package edu.bluejack182.defilm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.bluejack182.defilm.ui.main.Schedule;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    Context context;
    int layoutResourceId;
    List<Schedule> data;

    public ScheduleAdapter(Context context, int layoutResourceId, List<Schedule> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    static class DataHolder{
        TextView description;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataHolder dataHolder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            dataHolder = new DataHolder();
            dataHolder.description = convertView.findViewById(R.id.txt_description);

            convertView.setTag(dataHolder);
        }
        else{
            dataHolder = (ScheduleAdapter.DataHolder) convertView.getTag();
        }

        Schedule schedule = data.get(position);
        dataHolder.description.setText(schedule.getDescription());

        return convertView;
    }

}
