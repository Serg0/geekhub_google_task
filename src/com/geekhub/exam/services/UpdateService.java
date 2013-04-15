package com.geekhub.exam.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geekhub.exam.constants.Constants;

public class UpdateService  extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		new Thread() {
			@Override
			public void run() {
				Intent i = new Intent(Constants.BROADCAST_ACTION).putExtra(Constants.BROADCAST_TYPE, Constants.BROADCAST_MESSAGE);
				while(true) {
					try {
						Thread.sleep(Constants.SERVICE_TIME_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendBroadcast(i);
				}
			}
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}