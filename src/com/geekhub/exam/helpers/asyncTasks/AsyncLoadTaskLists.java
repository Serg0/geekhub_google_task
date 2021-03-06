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
import java.util.ArrayList;
import java.util.List;

import com.geekhub.exam.activities.MainActivity;
import com.google.api.services.tasks.model.TaskLists;

public class AsyncLoadTaskLists extends CommonAsyncTask {

	public TaskLists taskLists;
	public List<String> taskListsTitles = new ArrayList<String>();

	private LoadTaskListsCallBack callBack;

	AsyncLoadTaskLists(MainActivity activity, ProgressBar progress,
			LoadTaskListsCallBack callBack) {
		super(activity, progress);
		this.callBack = callBack;
	}

	protected void doInBackground() throws IOException {
		taskLists = activity.service.tasklists().list().execute();

	}

	public static void run(MainActivity tasksSample, ProgressBar progress,
			LoadTaskListsCallBack callBack) {
		new AsyncLoadTaskLists(tasksSample, progress, callBack).execute();
	}

	@Override
	protected void onSuccess() {
		if (callBack != null)
			callBack.loadTaskLists(taskLists);
	}

	public interface LoadTaskListsCallBack {
		void loadTaskLists(TaskLists localTaskLists);

	}

	@Override
	protected void onFail() {
		if (callBack != null)
			callBack.loadTaskLists(null);

	}
}