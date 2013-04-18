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

import java.io.IOException;
import java.net.ConnectException;

import javax.net.ssl.SSLException;

import android.os.AsyncTask;
import android.util.Log;

import com.geekhub.exam.R;
import com.geekhub.exam.activities.MainActivity;
import com.geekhub.exam.utils.Utils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

/**
 * Asynchronous task that also takes care of common needs, such as displaying
 * progress, authorization, exception handling, and notifying UI when operation
 * succeeded.
 * 
 * @author Yaniv Inbar
 */
public abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {

	final MainActivity activity;
	final com.google.api.services.tasks.Tasks client;
	protected String TAG = CommonAsyncTask.class.getSimpleName();
	private ProgressBar progress;

	CommonAsyncTask(MainActivity activity, ProgressBar progress) {
		this.activity = activity;
		client = activity.service;
		this.progress = progress;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d(TAG, getClass().getSimpleName() + "task started");
		activity.numAsyncTasks++;

		if (progress != null)
			progress.showProgressDialog(true);
	}

	@Override
	protected final Boolean doInBackground(Void... ignored) {
		Log.d(TAG, "doInBackground");

		if (activity.credential.getSelectedAccountName() != null)
			try {
				doInBackground();
				return true;
			} catch (GooglePlayServicesAvailabilityIOException availabilityException) {
				Log.d(TAG, "error "
						+ "GooglePlayServicesAvailabilityIOException e ");
				Log.d(TAG, "error "
						+ "GooglePlayServicesAvailabilityIOException e "
						+ availabilityException.getMessage());
				activity.showGooglePlayServicesAvailabilityErrorDialog(availabilityException
						.getConnectionStatusCode());
			} catch (UserRecoverableAuthIOException userRecoverableException) {
				Log.d(TAG, "error " + "UserRecoverableAuthIOException e ");
				Log.d(TAG, "error " + "userRecoverableException e "
						+ userRecoverableException.getMessage());
				activity.startActivityForResult(
						userRecoverableException.getIntent(),
						MainActivity.REQUEST_AUTHORIZATION);
			}catch (SSLException e) {
				Log.d(TAG, "error " + "SSLException e ");
				Log.d(TAG, "error " + "SSLException e " + e.getMessage());

				Utils.logAndShowError(activity, MainActivity.TAG,
						activity.getString(R.string.server_unavaliable));
				
			
			}
			/*}catch (java.net. e) {
		
			}*/ 
			catch (ConnectException e) {

				Log.d(TAG, "error " + "ConnectException e ");
				Log.d(TAG, "error " + "ConnectException e " + e.getMessage());

				Utils.logAndShowError(activity, MainActivity.TAG,
						activity.getString(R.string.server_unavaliable));
			} catch (IOException e) {

				Log.d(TAG, "error " + "IOException e ");
				Log.d(TAG, "error " + "IOException e " + e.getMessage());
				Log.d(TAG,
						"error " + "IOException e.toString() " + e.toString());

				Utils.logAndShow(activity, MainActivity.TAG, e);

			}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (0 == --activity.numAsyncTasks) {
			if (progress != null)
				progress.showProgressDialog(false);
		}
		if (success)
			onSuccess();
		else
			onFail();
	}

	@Override
	protected void finalize() throws Throwable {
		Log.d(TAG, "Task finalized");
		super.finalize();
	}

	@Override
	protected void onCancelled() {
		Log.d(TAG, "Task cancelled");
		super.onCancelled();
	}

	abstract protected void doInBackground() throws IOException;

	abstract protected void onSuccess();
	abstract protected void onFail();

	public interface ProgressBar {
		void showProgressDialog(boolean show);

	}

}