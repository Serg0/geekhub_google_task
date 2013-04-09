

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
public class AsyncAddTask extends CommonAsyncTask {

	private Task task;
	private AddTaskCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;
	
	AsyncAddTask(MainActivity activity, AddTaskCallBack callBack, Task task) {
		this(activity, callBack);
		this.task = task;
	}
	
	AsyncAddTask(MainActivity activity, AddTaskCallBack callBack, String taskListID, Task task) {
		this(activity, callBack);
		this.taskListID = taskListID;
		this.task = task;
	}
	
	AsyncAddTask(MainActivity activity, AddTaskCallBack callBack) {
		super(activity);
		this.callBack = callBack;
		
	}

	protected void doInBackground() throws IOException {
		
//		try {
			task  = client.tasks().insert(taskListID, task).execute();
		/*} catch (IOException e) {
			String message = e.getMessage();
			if(message != null)
				Utils.showError(activity, message);
			else
				Utils.showError(activity, activity.getString(R.string.error_unknown_io_error));
			e.printStackTrace();
		}*/
	}

	public static void run(MainActivity tasksSample,AddTaskCallBack callBack, Task task) {
		new AsyncAddTask(tasksSample, callBack, task).execute();
	}

	@Override
	protected void onSuccess() {
		if(callBack != null)
			callBack.getTask(task);
	}
	public interface AddTaskCallBack{
		void getTask(Task task);
		
	}
	
	//TODO added just in case
	private void generateDefaultTask(){
		this.task = new Task();
		task.setTitle("Default Task Title " + activity.numAsyncTasks);
	}
}