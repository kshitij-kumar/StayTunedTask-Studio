package com.kshitij.android.staytunedtask;

import android.app.Application;

/**
 * Created by kshitij.kumar on 01-06-2015.
 */

/**
 * Application class.
 */
public class Locator extends Application {

	private static Locator mInstance;

	public Locator() {

	}

	public static Locator getAppInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {

		mInstance = this;
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

}
