package com.geekhub.exam.activities;

/*
 * Copyright (c) 2011 Google Inc.
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.geekhub.exam.R;
import com.geekhub.exam.fragments.TasksFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
//import com.google.android.gms.common.GooglePlayServicesUtil;

public final class MainActivity extends SherlockFragmentActivity implements OnNavigationListener {

	private static final Level LOGGING_LEVEL = Level.ALL;

	private static final String PREF_ACCOUNT_NAME = "accountName";

	public static final String TAG = "TasksSample";

	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

	public static final int REQUEST_AUTHORIZATION = 1;

	public static final int REQUEST_ACCOUNT_PICKER = 2;

	final HttpTransport transport = AndroidHttp.newCompatibleTransport();

	final JsonFactory jsonFactory = new GsonFactory();

	GoogleAccountCredential credential;

	public List<String> tasksList;

	ArrayAdapter<String> adapter;

	public static com.google.api.services.tasks.Tasks service;

	public int numAsyncTasks;

	private static MainActivity instance;

	public static final String TASKLIST_DEFAULT_NAME = "@default";

	public TaskLists taskLists;

	public static Integer currentTaskListNumber = 0;
	
	public List<String> taskListsTitles = new ArrayList<String>();

	//	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_LIST);
		instance = this;
		// Google Accounts
		credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), TasksScopes.TASKS);
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

		// Tasks client
		service = new com.google.api.services.tasks.Tasks.Builder(transport, jsonFactory, credential)
		.setApplicationName("com.geekhub.exam").build();
		Log.d(TAG, "Task client init");

	}

	public static MainActivity getInstance(){
		return instance;
	}

	public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "showGooglePlayServicesAvailabilityErrorDialog" + connectionStatusCode);
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, getInstance(),
						REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "if (checkGooglePlayServicesAvailable())");
		if (checkGooglePlayServicesAvailable()) {
			Log.d(TAG, "checkGooglePlayServicesAvailable It is!");
			haveGooglePlayServices();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult" + requestCode + " "+resultCode);
		switch (requestCode) {
		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode == Activity.RESULT_OK) {
				haveGooglePlayServices();
			} else {
				checkGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				startFragment();
			} else {
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					startFragment();
				}
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			startFragment();
			break;
		case R.id.menu_accounts:
			chooseAccount();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Check that Google Play services APK is installed and up to date. */
	private boolean checkGooglePlayServicesAvailable() {
		int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void haveGooglePlayServices() {
		// check if there is already an account selected
		if (credential.getSelectedAccountName() == null) {
			// ask user to choose account
			chooseAccount();
		} else {
			startFragment();
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	private void startFragment() {
		actionBar();
		//		getSupportFragmentManager().beginTransaction().replace(R.id.list, new TasksFragment()).commit();

	}
	private void actionBar() {
		new Thread(){
			public void run(){
				try {
					taskLists = service.tasklists().list().execute();
					for (TaskList taskList : taskLists.getItems()) {
						taskListsTitles.add(taskList.getTitle().toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (taskListsTitles.size() != 0)
					setActionBar();
			}
		}.start();
	}

	public void setActionBar() {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(instance,	android.R.layout.simple_list_item_1, taskListsTitles);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {	
				String taskListID = TASKLIST_DEFAULT_NAME;
				getSupportActionBar().setListNavigationCallbacks(adapter, instance);
				getSupportActionBar().setSelectedNavigationItem(currentTaskListNumber);	
				for (TaskList taskList : taskLists.getItems()) {
					if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle())
						taskListID = taskList.getId();
				}
				TasksFragment fr = new TasksFragment();
				fr.getTasksFragment(taskListID);
			}
		});
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		currentTaskListNumber = itemPosition;
		String taskListID = TASKLIST_DEFAULT_NAME;
		TasksFragment fr = new TasksFragment();
		for (TaskList taskList : taskLists.getItems()) {
			if(taskListsTitles.get(currentTaskListNumber) == taskList.getTitle())
				taskListID = taskList.getId();
		}
		fr.getTasksFragment(taskListID);
		getSupportFragmentManager().beginTransaction().replace(R.id.list, fr).commit();
		return false;
	}

}
