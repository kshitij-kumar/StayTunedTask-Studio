package com.kshitij.android.staytunedtask.ui;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	private LoginTask mLoginTask;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		final EditText edtTxtEmail = (EditText) findViewById(R.id.edtTxtEmail);

		edtTxtEmail.setOnEditorActionListener(new OnEditorActionListener() {
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
		edtTxtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((EditText) v).setError(null);
				}
			}
		});

		final EditText edtTxtPass = (EditText) findViewById(R.id.edtTxtPass);

		edtTxtPass.setOnEditorActionListener(new OnEditorActionListener() {
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
		edtTxtPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((EditText) v).setError(null);
				}
			}
		});

		Button btnLogin = (Button) findViewById(R.id.btnLogin);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = edtTxtEmail.getText().toString();
				String pass = edtTxtPass.getText().toString();
				if (Utility.isValidEmail(email) // valid email
						&& !Utility.isNullOrEmpty(pass)) { // valid password
					mLoginTask = new LoginTask(email, pass);
					mLoginTask.execute();
				}
				if (!Utility.isValidEmail(email)) {
					edtTxtEmail
							.setError(getString(R.string.error_invalid_email));
				}
				if (Utility.isNullOrEmpty(pass)) {
					edtTxtPass.setError(getString(R.string.error_blank_pass));
				}

			}
		});

		TextView txtRegister = (TextView) findViewById(R.id.txtRegister);

		// Listening to register new account link
		txtRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
			}
		});
	}

	private void dismissLoginProgress() {
		Log.d(TAG, "dissmissLoginProgress()");
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	private void showLoginProgress() {
		Log.d(TAG, "showLoginProgress()");
		dismissLoginProgress();
		mProgressDialog = ProgressDialog.show(this, "", "Logging...", true,
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

	private class LoginTask extends AsyncTask<String, Void, String> {
		private String mEmail;
		private String mPass;

		LoginTask(String email, String pass) {
			this.mEmail = email;
			this.mPass = pass;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "LoginTask, onPreExecute()");
			// Show progress indicator
			showLoginProgress();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			User registeredUser = DatabaseHandler.getInstance(
					getApplicationContext()).getUserFromDb(mEmail);
			if (registeredUser != null) {
				try {
					boolean match = Crypto.validatePassword(mPass,
							registeredUser.getPassword());
					if (match) {
						return getString(R.string.login_successful);
					} else {
						return getString(R.string.incorrect_password);
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					return getString(R.string.error);
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
					return getString(R.string.error);
				}
			} else {
				return getString(R.string.register);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "LoginTask, onPostExecute()");
			// Hide progress indicator
			dismissLoginProgress();

			if (result.equalsIgnoreCase(getString(R.string.login_successful))) {
				Intent intent = new Intent(LoginActivity.this,
						LocationActivity.class);
				startActivity(intent);
				finish();
			} else {
				showMessage(result);
			}
		}
	}
}