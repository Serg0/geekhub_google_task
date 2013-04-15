
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

import android.widget.ArrayAdapter;

import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.fragments.TasksFragment;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

/**
 * Asynchronously load the tasks.
 * 
 * @author Yaniv Inbar
 */
public class AsyncLoadTaskLists extends CommonAsyncTask {

	public TaskLists taskLists;
	public List<String> taskListsTitles = new ArrayList<String>();

	private TasksFragment fragment;
	private LoadTaskListsCallBack callBack;
	private ArrayAdapter<String> menuAdapter;

	AsyncLoadTaskLists(MainActivity activity, ProgressBar progress,
			LoadTaskListsCallBack callBack, TasksFragment tasksFfragment) {
		super(activity, progress);
		this.fragment = tasksFfragment;
		this.callBack = callBack;
	}

	protected void doInBackground() throws IOException {
		taskLists = activity.service.tasklists().list().execute();
		for (TaskList taskList : taskLists.getItems()) {
			taskListsTitles.add(taskList.getTitle().toString());
		}
		if (taskListsTitles.size() != 0){
			menuAdapter = new ArrayAdapter<String>(activity,	android.R.layout.simple_list_item_1, taskListsTitles);
			menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}
	}

	public static void run(MainActivity tasksSample, ProgressBar progress,
			LoadTaskListsCallBack callBack, TasksFragment fragment) {
		new AsyncLoadTaskLists(tasksSample, progress, callBack, fragment).execute();
	}

	@Override
	protected void onSuccess() {
		if (callBack != null)
			callBack.getTasks(fragment,taskLists, menuAdapter, taskListsTitles);
	}

	public interface LoadTaskListsCallBack {
		void getTasks(TasksFragment taskFragment,TaskLists taskLists, ArrayAdapter<String> adapter, List<String> taskListsTitles);

	}
}