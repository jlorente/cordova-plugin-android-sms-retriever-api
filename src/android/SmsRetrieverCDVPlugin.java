package com.lorentech.cordova.smsretriever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class SmsRetrieverCDVPlugin extends CordovaPlugin {

	private HashMap<String, CallbackContext> callbackList;

	private static final String TAG = "SmsRetrieverCDVPlugin";

	private static final String ACTION_START = "start";
	private static final String ACTION_STOP = "stop";
	private static final String ACTION_ON = "on";

	private BroadcastReceiver smsBroadcastReceiver;

	public void onDestroy() {
		finish();
	}

	@Override
	public void initialize(@NonNull CordovaInterface cordova, @NonNull CordovaWebView webView) {
		Log.d(TAG, "Initialize Plugin");

		callbackList = new HashMap<String, CallbackContext>();
		super.initialize(cordova, webView);
	}

	@Override
	public boolean execute(@NonNull String action, @NonNull JSONArray args, @NonNull CallbackContext callbackContext) throws JSONException {
		switch (action) {
			case ACTION_START:
				this.start(callbackContext);
				break;
			case ACTION_STOP:
				this.stop(callbackContext);
				break;
			case ACTION_ON:
				this.on(args, callbackContext);
				break;
			default:
				Log.d(TAG, String.format("Invalid action: %s", action));
				PluginResult result = new PluginResult(PluginResult.Status.INVALID_ACTION);
				callbackContext.sendPluginResult(result);
				break;
		}

		return true;
	}

	public void on(@NonNull JSONArray args, @NonNull CallbackContext callbackContext) throws JSONException {
		String event = args.getString(0);
		Log.d(TAG, "On event registered: " + event);
		callbackList.put(event, callbackContext);
	}


	public void trigger(@NonNull String action) {
		trigger(action, new Object());
	}

	public void trigger(@NonNull String action, @NonNull Object data) {
		JSONObject message = new JSONObject();

		if (!callbackList.containsKey(action)) {
			return;
		}
		
		try {
			message.put("name", action);
			message.put("data", data);
		} catch (JSONException e) {
			Log.d(TAG, "Trigger JSONException");
		}

		PluginResult myResult = new PluginResult(PluginResult.Status.OK, message);
		myResult.setKeepCallback(true);
		callbackList.get(action).sendPluginResult(myResult);
	}

	private void start(final CallbackContext callbackContext) {
		Log.d(TAG, "Action start entered");

		if (smsBroadcastReceiver != null) {
			Log.d(TAG, "SmsRetriever already started");
			PluginResult result = new PluginResult(PluginResult.Status.ERROR, "SmsRetriever already started");
			callbackContext.sendPluginResult(result);
			return;
		}

		SmsRetrieverClient client = SmsRetriever.getClient(cordova.getActivity().getApplicationContext());

		try {
			Task<Void> task = client.startSmsRetriever();

			task.addOnSuccessListener((Void v) -> {
				Log.d(TAG, "SmsRetriever client started");

				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

				try {
					smsBroadcastReceiver = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
								Bundle extras = intent.getExtras();
								Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
								finish();

								switch(status.getStatusCode()) {
									case CommonStatusCodes.SUCCESS:
										String smsMessage = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
										Log.d(TAG, "Sms Retrieved: " + smsMessage);
										trigger("smsReceived", smsMessage);
										break;
									case CommonStatusCodes.TIMEOUT:
										Log.e(TAG, "SmsReceiver timeout");
										trigger("timeout");
										break;
								}
							}
						}
					};

					cordova.getActivity().getApplicationContext().registerReceiver(smsBroadcastReceiver, intentFilter);
					PluginResult result = new PluginResult(PluginResult.Status.OK, "Sms Retriever will last 5 minutes");
					result.setKeepCallback(true);
					callbackContext.sendPluginResult(result);
				} catch (Exception e) {
					PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
					callbackContext.sendPluginResult(result);
				}
			});

			task.addOnFailureListener((Exception e) -> {
          		Log.d(TAG, "SmsRetriever client failed");

				PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
				callbackContext.sendPluginResult(result);
			});
		} catch (Exception e) {
			PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
			callbackContext.sendPluginResult(result);
		}
	}

	private void stop(final CallbackContext callbackContext) {
		finish();

		PluginResult myResult = new PluginResult(PluginResult.Status.OK, "Sms Retriever stopped");
		myResult.setKeepCallback(true);
		callbackContext.sendPluginResult(myResult);
	}

	private void finish() {
		if (smsBroadcastReceiver != null) {
			try {
				cordova.getActivity().getApplicationContext().unregisterReceiver(smsBroadcastReceiver);
				smsBroadcastReceiver = null;
				Log.d(TAG, "Sms Retriever unregistered");
			} catch (Exception e) {
				Log.d(TAG, "Sms Retriever unregister error: " + e.getMessage());
			}
		}
	}
}
