package com.geekhub.exam.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.geekhub.exam.R;
import com.geekhub.exam.constants.Constants;


public class MainActivity extends SherlockActivity {

	private static Account googleAccount = null;
	AccountManager accountManager = null;
	public String AUTH_TOKEN = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		accountManager = AccountManager.get(this);
		if(googleAccount == null) {
			Dialog dialogAccounts = onCreateDialog(Constants.DIALOG_ACCOUNTS);
			dialogAccounts.show();
		} else {
			getAuthToken();
		}
	}

	public Dialog onCreateDialog(int id) {
		switch (id) {
		case 1000:
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
					Log.e("token", AUTH_TOKEN);
				} catch (Exception e) {}
			}
		}, null);
	}
}
