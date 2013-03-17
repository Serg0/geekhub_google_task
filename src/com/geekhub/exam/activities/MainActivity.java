package com.geekhub.exam.activities;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.geekhub.exam.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.model.Task;

public class MainActivity extends SherlockActivity {
	
	//AUTH
	private static Account googleAccount = null;
	AccountManager accountManager = null;
	public String AUTH_TOKEN = null;
	
	//UI
	private ListView listViewTasksList;
	
	//API
	GoogleAccountCredential credential;
	public com.google.api.services.tasks.Tasks service;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		service =
		        new com.google.api.services.tasks.Tasks.Builder(transport, jsonFactory, credential)
		            .setApplicationName("Google-TasksAndroidSample/1.0").build();
		initViews();
		
		/*accountManager = AccountManager.get(this);
		if(googleAccount == null) {
			Dialog dialogAccounts = onCreateDialog(Constants.DIALOG_ACCOUNTS);
			dialogAccounts.show();
		} else {
			getAuthToken();
		}*/
	}

	private void initViews() {
		// TODO Auto-generated method stub
		listViewTasksList = (ListView) findViewById(R.id.listViewTaskLists);
		
	}

	/*public Dialog onCreateDialog(int id) {
		
		switch (id) {	
		case (Constants.DIALOG_ACCOUNTS):
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select a Google account");
			final Account[] accounts = accountManager.getAccountsByType("com.google");
			final int size = accounts.length;
			String[] names = new String[size];
			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					gotAccount(accounts[which]);
					getAuthToken();
				}
			});
			return builder.create();
		}
		return null;
	}

	private void gotAccount(Account acc){
		googleAccount = acc;
	}

	private void getAuthToken(){
		accountManager.getAuthToken(googleAccount, Constants.AUTH_TOKEN_TYPE, null, this, new AccountManagerCallback<Bundle>() {
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
					AUTH_TOKEN = token;
//					getTaskLists();
					Log.e("token", AUTH_TOKEN);
				} catch (Exception e) {
					//TODO add Exception processing
					
				}
			}

		}, null);
	}*/
	
	private void getTaskLists() {	
		// TODO Auto-generated method stub
		
		/* List<Task> tasks =
			        client.tasks().list("@default").setFields("items/title").execute().getItems();*/
		
	}
	

}
