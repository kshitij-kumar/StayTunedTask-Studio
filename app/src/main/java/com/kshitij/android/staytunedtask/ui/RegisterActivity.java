package com.kshitij.android.staytunedtask.ui;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kshitij.android.staytunedtask.R;
import com.kshitij.android.staytunedtask.db.DatabaseHandler;
import com.kshitij.android.staytunedtask.model.User;
import com.kshitij.android.staytunedtask.util.Crypto;
import com.kshitij.android.staytunedtask.util.Utility;

public class RegisterActivity extends Activity {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private RegistrationTask mRegistrationTask;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set View to register.xml
		setContentView(R.layout.register);

		final EditText edtTxtRegEmail = (EditText) findViewById(R.id.edtTxtRegEmail);

		edtTxtRegEmail.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					if (!Utility.isValidEmail(v.getText().toString())) {
						v.setError(getString(R.string.error_invalid_email));
					}
				}
				return false;
			}
		});
		edtTxtRegEmail
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							((EditText) v).setError(null);
						}
					}
				});

		final EditText edtTxtRegPass = (EditText) findViewById(R.id.edtTxtRegPass);

		edtTxtRegPass.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (Utility.isNullOrEmpty(v.getText().toString())) {
						v.setError(getString(R.string.error_blank_pass));
					}
				}
				return false;
			}
		});
		edtTxtRegPass
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							((EditText) v).setError(null);
						}
					}
				});

		final EditText edtTxtRegFullName = (EditText) findViewById(R.id.edtTxtRegFullName);

		Button btnRegister = (Button) findViewById(R.id.btnRegister);

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = edtTxtRegEmail.getText().toString();
				String pass = edtTxtRegPass.getText().toString();
				String userName = edtTxtRegFullName.getText().toString();
				if (Utility.isValidEmail(email) // valid email
						&& !Utility.isNullOrEmpty(pass)) { // valid password
					mRegistrationTask = new RegistrationTask(email, pass, userName);
					mRegistrationTask.execute();
				}
				if (!Utility.isValidEmail(email)) {
					edtTxtRegEmail
							.setError(getString(R.string.error_invalid_email));
				}
				if (Utility.isNullOrEmpty(pass)) {
					edtTxtRegPass
							.setError(getString(R.string.error_blank_pass));
				}

			}
		});

		TextView loginScreen = (TextView) findViewById(R.id.txtLinkToLogin);

		loginScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// Switching to Login Screen/closing register screen
				finish();
			}
		});
	}

	private void dismissRegisterProgress() {
		Log.d(TAG, "dismissRegisterProgress()");
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	private void showRegisterProgress() {
		Log.d(TAG, "showRegisterProgress()");
		dismissRegisterProgress();
		mProgressDialog = ProgressDialog.show(this, "", "Registering...", true,
				false);
	}

	private void showMessage(String message) {
		Log.d(TAG, "showMessage()");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private class RegistrationTask extends AsyncTask<String, Void, String> {
		private String mEmail;
		private String mPass;
		private String mUserName;

		RegistrationTask(String email, String pass, String userName) {
			this.mEmail = email;
			this.mPass = pass;
			this.mUserName = userName;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "LoginTask, onPreExecute()");
			// Show progress indicator
			showRegisterProgress();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			User registeredUser = DatabaseHandler.getInstance(
					getApplicationContext()).getUserFromDb(mEmail);
			if (registeredUser != null) {
				return getString(R.string.try_login);
			} else {
				User user = new User();
				user.setName(mUserName);
				user.setEmail(mEmail);
				try {
					user.setPassword(Crypto.generateStorngPasswordHash(mPass));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					return getString(R.string.error);
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
					return getString(R.string.error);
				}
				long regResult = DatabaseHandler.getInstance(
						getApplicationContext()).addUser(user);
				if (regResult != -1) {
					return getString(R.string.registration_successful);
				} else {
					return getString(R.string.error);
				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "LoginTask, onPostExecute()");
			// Hide progress indicator
			dismissRegisterProgress();
			showMessage(result);
		}
	}
}