package com.kshitij.android.staytunedtask.util;


/**
 * Created by kshitij.kumar on 06-06-2015.
 */
public class Utility {
	private static final String TAG = Utility.class.getSimpleName();

	/**
	 * Checks if a string is null or empty.
	 * 
	 * @param string
	 *            The input string.
	 * @return True if the string is null or empty, false otherwise.
	 */

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	/**
	 * Validates an email address.
	 * 
	 * @param target The email input from user.
	 * @return True if the email is valid, false otherwise.
	 */
	
	public final static boolean isValidEmail(String target) {
		if (isNullOrEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
}
