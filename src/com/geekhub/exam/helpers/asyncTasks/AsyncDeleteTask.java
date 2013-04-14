

/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.geekhub.exam.helpers.asyncTasks;

import com.geekhub.exam.R;
import com.geekhub.exam.R.string;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.constants.Constants;
import com.geekhub.exam.helpers.asyncTasks.CommonAsyncTask.ProgressBar;
import com.geekhub.exam.utils.Utils;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously load the tasks.
 * 
 * @author Yaniv Inbar
 */
public class AsyncDeleteTask extends CommonAsyncTask {

	private List<Task> tasks;
	private DeleteTaskCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;
	
	AsyncDeleteTask(MainActivity activity,ProgressBar progress, DeleteTaskCallBack callBack, String taskListID, List<Task> tasks) {
		this(activity, progress);
		this.taskListID = taskListID;
		this.tasks = tasks;
		this.callBack = callBack;
	}
	
	AsyncDeleteTask(MainActivity activity,ProgressBar progress, DeleteTaskCallBack callBack, String taskListID, Task task) {
		this(activity, progress);
		if(taskListID != null)
			this.taskListID = taskListID;
		this.tasks = new ArrayList<Task>();
		tasks.add(task);
		this.callBack = callBack;
	}
	
	AsyncDeleteTask(MainActivity activity,ProgressBar progress) {
		super(activity, progress);
		
	}

	protected void doInBackground() throws IOException{

		for(Task task:tasks)
				client.tasks().delete(taskListID, task.getId()).execute();
	}

	public static void run(MainActivity activity,ProgressBar progress, DeleteTaskCallBack callBack, String taskListID, List<Task> tasks) {
		new AsyncDeleteTask(activity, progress, callBack, taskListID, tasks).execute();
	}

	@Override
	protected void onSuccess() {
		if(callBack != null)
			callBack.getTask(tasks);
	}
	
	public interface DeleteTaskCallBack{
		void getTask(List<Task> tasks);
		
	}
	
	}