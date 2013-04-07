package com.geekhub.exam.helpers;

import java.io.IOException;
import java.util.List;

import com.geekhub.exam.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.google.api.client.json.JsonFactory;
import com.google.api.services.tasks.model.Task;

public class TaskListArrayAdapter extends ArrayAdapter<Task> {

	private List<Task> tasks;
	private Context context;

	public TaskListArrayAdapter(Context context, List<Task> tasks) {
		super(context, R.layout.list_menu_item_checkbox, tasks);
		this.tasks = tasks;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_menu_item_checkbox, parent,
					false);
		}
		
		CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
		Task task  = tasks.get(position);
		String taskName =	task.getTitle();
		checkbox.setText(taskName);

		return super.getView(position, convertView, parent);
	}

}
