

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
public class AsyncUpdateTask extends CommonAsyncTask {

	private Task task;
	private UpdateTaskCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;
	
	AsyncUpdateTask(MainActivity activity, UpdateTaskCallBack callBack, Task task) {
		this(activity, callBack);
		this.task = task;
	}
	
	AsyncUpdateTask(MainActivity activity, UpdateTaskCallBack callBack, String taskListID, Task task) {
		this(activity, callBack);
		this.taskListID = taskListID;
		this.task = task;
	}
	
	AsyncUpdateTask(MainActivity activity, UpdateTaskCallBack callBack) {
		super(activity);
		this.callBack = callBack;
		
	}

	protected void doInBackground() throws IOException {
		
			task  = client.tasks().update(taskListID, task.getId(), task).execute();
		
	}

	public static void run(MainActivity tasksSample, UpdateTaskCallBack callBack,String taskListID, Task task) {
		new AsyncUpdateTask(tasksSample, callBack,taskListID, task).execute();
	}

	@Override
	protected void onSuccess() {
		if(callBack != null)
			callBack.getTask(task);
	}
	public interface UpdateTaskCallBack{
		void getTask(Task task);
		
	}
	
	//TODO added just in case
	private void generateDefaultTask(){
		this.task = new Task();
		task.setTitle("Default Task Title " + activity.numAsyncTasks);
	}
}