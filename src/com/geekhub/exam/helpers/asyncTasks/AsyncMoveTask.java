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

import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.constants.Constants;
import com.google.api.services.tasks.Tasks.TasksOperations.Move;
import com.google.api.services.tasks.model.Task;

public class AsyncMoveTask extends CommonAsyncTask {

	private Task task, taskPrevious;
	private MoveTaskCallBack callBack;
	private String taskListID = Constants.DEFAULT_KEY;

	AsyncMoveTask(MainActivity activity, ProgressBar progress,
			MoveTaskCallBack callBack, String taskListID, Task task,
			Task taskPrevious) {
		super(activity, progress);
		if (taskListID != null)
			this.taskListID = taskListID;

		this.task = task;
		this.callBack = callBack;
		this.taskPrevious = taskPrevious;
	}

	protected void doInBackground() throws IOException {

		Move move = client.tasks().move(taskListID, task.getId());
		if (taskPrevious != null)
			move.setPrevious(taskPrevious.getId());

		task = move.execute();

	}

	public static void run(MainActivity activity, ProgressBar progress,
			MoveTaskCallBack callBack, String taskListID, Task task,
			Task taskPrevious) {
		new AsyncMoveTask(activity, progress, callBack, taskListID, task,
				taskPrevious).execute();
	}

	@Override
	protected void onSuccess() {
		if (callBack != null)
			callBack.getTask(task);
	}

	public interface MoveTaskCallBack {
		void getTask(Task task);

	}

	@Override
	protected void onFail() {
		if (callBack != null)
			callBack.getTask(null);

	}
}