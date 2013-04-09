package com.geekhub.exam.helpers.asyncTasks;



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


import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.utils.Utils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author Yaniv Inbar
 */
abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {

  MainActivity activity;
  com.google.api.services.tasks.Tasks client;
  private ProgressDialog progressBar;
private String TAG = CommonAsyncTask.class.getSimpleName();

  CommonAsyncTask(MainActivity activity) {
    this.activity = activity;
    client = activity.service;
   
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    activity.numAsyncTasks++;
    progressBar = ProgressDialog.show(activity, null,activity.getString(R.string.progress_dialog_processing),true, false);
  }

  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
      doInBackground();
      return true;
    } catch (GooglePlayServicesAvailabilityIOException availabilityException) {
      activity.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
      activity.startActivityForResult(
          userRecoverableException.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
    } catch (IOException e) {
      Utils.logAndShow(activity, MainActivity.TAG, e);
    }
    return false;
  }

  @Override
  protected void onPostExecute(Boolean success) {
    super.onPostExecute(success);
    if (0 == --activity.numAsyncTasks) {
      progressBar.dismiss();
    }
    if (success) 
			onSuccess();
			/*String message = e.getMessage();
			if(message != null)
				Utils.showError(activity, message);
			else
				Utils.showError(activity, activity.getString(R.string.error_unknown_io_error));
    }*/
  }

  @Override
	protected void onCancelled() {
	  if(progressBar != null)
		  progressBar.dismiss();
	  Log.d(TAG, "Task canseled");
		super.onCancelled();
	}
  abstract protected void doInBackground() throws IOException;
  abstract protected void onSuccess();
}