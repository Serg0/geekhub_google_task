package com.geekhub.exam.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

public class AccountsDialog extends DialogFragment {

	public Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			AccountManager accountManager = AccountManager.get(getActivity());
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select a Google account");
			final Account[] accounts = accountManager.getAccountsByType("com.google");
			final int size = accounts.length;
			String[] names = new String[size];
			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Stuff to do when the account is selected by the user
					//	          gotAccount(accounts[[]which]);
				}
			});
			return builder.create();
		}
		return null;
	}

}
