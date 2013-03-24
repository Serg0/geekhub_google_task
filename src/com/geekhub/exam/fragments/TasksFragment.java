package com.geekhub.exam.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.google.api.services.tasks.model.Task;

public class TasksFragment extends SherlockFragment{
	View view;
	ArrayAdapter<String> adapter;
	private ListView listView;
	List<String> result = null;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_list, container, false);
		return  view;
	}

	@Override
	public void onActivityCreated(Bundle savedISnstanceState) {
		super.onActivityCreated(savedISnstanceState);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadData();
					updateUi();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}).start();
	}

	void loadData() throws IOException {
		result = new ArrayList<String>();
		List<Task> tasks = MainActivity.service.tasks().list("@default")
				.setFields("items/title").execute().getItems();
		if (tasks != null) {
			for (Task task : tasks) {
				result.add(task.getTitle());
			}
		} else {
			result.add("No tasks.");
		}
	}

	private void updateUi() {
		adapter = new ArrayAdapter<String>(getSherlockActivity(), R.layout.item, result);
		listView = (ListView) view.findViewById(R.id.list_tasts);
		getSherlockActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listView.setAdapter(adapter);
			}
		});
	}

	public void refreshView() {}

}
