

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

import com.geekhub.exam.R.string;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.constants.Constants;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously load the tasks.
 * 
 * @author Yaniv Inbar
 */
public class AsyncLoadTasks extends CommonAsyncTask {

	private List<Task> tasks;
	private LoadTasksCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;
	
	AsyncLoadTasks(MainActivity activity, LoadTasksCallBack callBack, String taskListID) {
		this(activity, callBack);
		this.taskListID = taskListID;
	}
	AsyncLoadTasks(MainActivity activity, LoadTasksCallBack callBack) {
		super(activity);
		this.callBack = callBack;
	}

	@Override
	protected void doInBackground() throws IOException {
		tasks = client.tasks().list(taskListID)
				.setFields("items/title").execute().getItems();
	}

	public static void run(MainActivity tasksSample,LoadTasksCallBack callBack) {
		new AsyncLoadTasks(tasksSample, callBack).execute();
	}

	@Override
	protected void onSuccess() throws IOException {
		if(callBack != null)
			callBack.getTasks(tasks);
	}
	public interface LoadTasksCallBack{
		void getTasks(List<Task> tasks);
		
	}
}