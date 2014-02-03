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

import java.io.IOException;
import java.util.List;

import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.constants.Constants;
import com.google.api.services.tasks.model.Task;

/**
 * Asynchronously load the tasks.
 * 
 * @author Yaniv Inbar
 */
public class AsyncLoadTasks extends CommonAsyncTask {

	private List<Task> tasks;
	private LoadTasksCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;

	AsyncLoadTasks(MainActivity activity, ProgressBar progress,
			LoadTasksCallBack callBack, String taskListID) {
		super(activity, progress);
		if (taskListID != null)
			this.taskListID = taskListID;
		this.callBack = callBack;
	}

	protected void doInBackground() throws IOException {
		tasks = client.tasks().list(taskListID).setFields("items").execute()
				.getItems();

	}

	public static void run(MainActivity tasksSample, ProgressBar progress,
			LoadTasksCallBack callBack, String taskListID) {
		new AsyncLoadTasks(tasksSample, progress, callBack, taskListID)
				.execute();
	}

	@Override
	protected void onSuccess() {
		if (callBack != null)
			callBack.getTasks(tasks);
	}

	public interface LoadTasksCallBack {
		void getTasks(List<Task> tasks);

	}

	@Override
	protected void onFail() {
		if (callBack != null)
			callBack.getTasks(null);

	}
}