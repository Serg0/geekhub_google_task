package com.geekhub.exam.google;
import java.io.IOException;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;

public class GoogleTaskList {

	private TaskList taskList = null;
	private GoogleTasks tasks = null;
	private Tasks service = null;

	public GoogleTaskList(String title) throws IOException {
		this.taskList = new TaskList();
		this.taskList.setTitle(title);
		this.service.tasklists().insert(this.taskList).execute();
	} 

	public GoogleTaskList(Tasks service, TaskList taskList) throws IOException {
		this.taskList = taskList;
		this.service =service;
		this.setTasks(new GoogleTasks(service, taskList.getId()));
	}

	public GoogleTaskList(Tasks service, String TaskListID) throws IOException {
		this.service =service;
		this.taskList = service.tasklists().get(TaskListID).execute();
		this.setTasks(new GoogleTasks(service, TaskListID));
	}

	public void delete() throws IOException {
		this.service.tasklists().delete(this.taskList.getId()).execute();
	}
	public TaskList getTaskList() {
		return this.taskList;
	}
	public void update() throws IOException {
		this.taskList = this.service.tasklists().update(this.taskList.getId(), this.taskList).execute();
	}

	public GoogleTasks getTasks() {
		return tasks;
	}

	public void setTasks(GoogleTasks tasks) {
		this.tasks = tasks;
	}



}
