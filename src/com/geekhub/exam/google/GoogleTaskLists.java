package com.geekhub.exam.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class GoogleTaskLists {

	private Map<String, GoogleTaskList> tastLists = null;

	private Tasks service = null;

	public GoogleTaskLists(Tasks service) {	
		loadData(service);
	}

	private void loadData(Tasks service) {
		this.service = service;
		if (tastLists == null) {
			this.updateData();
		}
	}

	public void updateData() {
		try {
			TaskLists taskLists = service.tasklists().list().execute();
			tastLists = new HashMap<String, GoogleTaskList>();
			for (TaskList taskList : taskLists.getItems()) {
				tastLists.put(taskList.getId(), new GoogleTaskList(this.service, taskList));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GoogleTaskList getGoogleTaskList(String id) {
		return tastLists.get(id);
	}

	public void insert(GoogleTaskList googleTaskList) {
		tastLists.put(googleTaskList.getTaskList().getId(),googleTaskList);
	}

	public void update(GoogleTaskList googleTaskList) throws IOException {
			this.updateData();
	}

	public void delete(String id) throws IOException {
		tastLists.get(id).delete();
		this.updateData();
	}

	public Set<String> getIDs() {
		return this.tastLists.keySet();
	}

}
