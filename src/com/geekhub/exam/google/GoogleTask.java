package com.geekhub.exam.google;

import java.io.IOException;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

public class GoogleTask {

	private Task task = null;
	private Tasks service = null;

	public GoogleTask(Tasks service, Task task) {
		if((service == null) || (task == null)) throw new NullPointerException();
		this.task = task;
		this.service = service;
	}

	public GoogleTask(Tasks service, String taskListID) throws IOException {
		if(service == null) throw new NullPointerException();
		this.service = service;
		this.task = this.service.tasks().insert(taskListID, newTask()).execute();
	}

	//create default instance of GoogleTask in default tasklist 
	public GoogleTask(Tasks service) throws IOException {
		if(service == null) throw new NullPointerException();
		this.service = service;
		this.task = this.service.tasks().insert("@default", newTask()).execute();
	}

	//task checking for null
	public boolean hasTask() {
		return !this.task.isEmpty();
	} 

	//private method for create default task
	private Task newTask() {
		Task task = new Task();
		task.setTitle("New Task");
		task.setNotes("Please complete me");
		task.setDue(new DateTime(System.currentTimeMillis() + 3600000));//magic
		return task;
	}

	//get current task from instance of GoogleTask class
	public Task getCurrentTask() {
		return this.task;
	}

	//get current service from instance of GoogleTask class	
	public Tasks getCurrentService() {
		return this.service;
	}

	//update current task in default tasklist
	public void update(Task task) throws IOException {
		this.service.tasks().update("@default", task.getId(), task).execute();
	}

	//update current task by tasklist ID
	public void update(String taskListID, Task task) throws IOException {
		this.task = this.service.tasks().update(taskListID, task.getId(), task).execute();
	}

	//update current task by tasklist ID
	public void update(String taskListID) throws IOException {
		this.task = this.service.tasks().update(taskListID, this.task.getId(), this.task).execute();
	}

	//delete current task in default tasklist
	public void delete(Task task) throws IOException {
		this.service.tasks().delete("@default", "taskID").execute();
	}

	//delete current task by tasklist ID
	public void delete(String taskListID, Task task) throws IOException {
		this.service.tasks().delete(taskListID, "taskID").execute();
	}

	//set current task to instance of GoogleTask class
	public void setCurrentTask(Task task) {
		this.task = task;
	}

	//set current service to instance of GoogleTask class	
	public void setCurrentService(Tasks service) {
		this.service = service;
	}

	//return task from default tasklist by ID
	public static Task getTaskByID(Tasks service, String taskID) throws IOException {
		return service.tasks().get("@default", taskID).execute();
	}

	//return task by task ID and tasklist ID
	public static Task getTaskByID(Tasks service, String tasklistID, String taskID) throws IOException {
		return service.tasks().get(tasklistID, taskID).execute();		
	}

}
