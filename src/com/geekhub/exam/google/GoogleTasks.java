package com.geekhub.exam.google;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;



public class GoogleTasks {

	private Tasks service = null;
	private ArrayList<GoogleTask> tasks = null;
	private String taskListID = "@default";

	public GoogleTasks(Tasks service, String taskListID) throws IOException {
		this.service = service;
		this.setTaskListID(taskListID);
		getGoogleTasks(taskListID);
	}

	public GoogleTasks(Tasks service) throws IOException {
		this.service = service;
		getGoogleTasks("@default");
	}

	private void getGoogleTasks(String taskListID) throws IOException {
		com.google.api.services.tasks.model.Tasks tasks = this.service.tasks().list(taskListID).execute();
		this.tasks = new ArrayList<GoogleTask>();
		for (Task task : tasks.getItems()) {
			this.tasks.add(new GoogleTask(service, task));
		}
	}

	//get current service from instance of GoogleTasks class	
	public Tasks getCurrentService() {
		return this.service;
	}

	//set current service to instance of GoogleTasks class	
	public void setCurrentService(Tasks service) {
		this.service = service;
	}

	//The affected tasks will be marked as 'hidden' and no longer be returned by default
	//when retrieving all tasks for a task list
	public void clearTasks() throws IOException {
		this.service.tasks().clear("taskListID").execute();
	}	

	public GoogleTask getGoogleTasks(int index) {
		return this.tasks.get(index);
	}

	public  ArrayList<GoogleTask> getAllGoogleTasks() {
		return this.tasks;
	}

	public String getTaskListID() {
		return taskListID;
	}

	public void setTaskListID(String taskListID) {
		this.taskListID = taskListID;
	}

}

