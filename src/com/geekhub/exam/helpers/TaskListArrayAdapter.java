package com.geekhub.exam.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.geekhub.exam.R;
import com.geekhub.exam.constants.Constants;
import com.google.api.services.tasks.model.Task;

public class TaskListArrayAdapter extends ArrayAdapter<Task> {

	private List<Task> tasks;
	private Context context;
	private ListViewCheckedListener checkedListener;
	private boolean dragModeEnabled;
	
	public TaskListArrayAdapter(Context context, List<Task> tasks, ListViewCheckedListener checkedListener, boolean dragModeEnabled) {
		super(context, R.layout.item, tasks);
		if(tasks == null)
			tasks = new ArrayList<Task>();
		this.tasks = tasks;
		this.context = context;
		this.checkedListener = checkedListener;
		this.dragModeEnabled = dragModeEnabled;
	}
	
	@Override
	public int getCount() {
		int count;
		if(tasks == null)
			count = 0;
		else
			count = tasks.size();
		return count;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listview_row_custom, parent,
					false);
		}
		
		CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
		TextView mainRow  = (TextView) row.findViewById(R.id.mainRow);
		
		
		final Task task  = tasks.get(position);
		String taskName =	task.getTitle();
		Log.d("TaskListArrayAdapter", taskName);
		
		if(task.getStatus().equals(Constants.TASK_COMPLETED_KEY)){
			mainRow.setPaintFlags(checkbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			checkbox.setChecked(true);
		}else
		{
			mainRow.setPaintFlags(checkbox.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			checkbox.setChecked(false);
		}
		
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				checkedListener.checkStateChanged(task, position, isChecked);
			}
		});
		mainRow.setText(taskName);
		if(!dragModeEnabled)
			mainRow.setCompoundDrawables(null, null, null, null);
				
		return row;
	}

	public interface ListViewCheckedListener{
			void checkStateChanged(Task task, int pos, boolean isChecked);
	}

}
