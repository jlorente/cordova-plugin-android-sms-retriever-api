Cordova Plugin Android SMS Retriever
====================================
Cordova plugin to enable single SMS reception in Android using the [SMS Retriever API](https://developers.google.com/identity/sms-retriever/overview).

## Installation

The installation required cordova 6.0+

Use the cordova client in order to install the package.

```bash
$ cordova plugin add cordova-plugin-android-sms-retriever
```

## Configuration

The plugin requires the Google libraries com.google.android.gms:play-services-auth
and com.google.android.gms:play-services-auth-api-phone.

The default versions of this two libraries has been set to 15.+, but you can configure
the version you want by defining the PLAY_SERVICES_AUTH_VERSION and
PLAY_SERVICES_AUTH_API_PHONE_VERSION preferences in config.xml or package.json.

### Example

```xml
<platform name="android">
  <preference name="PLAY_SERVICES_AUTH_VERSION" value="17.+" />
</platform>
```

## SMS message preparation

To use the [SMS Retriever API](https://developers.google.com/identity/sms-retriever/overview)
you need to include a text signature in your SMS content. You can find more about
this topic in the [Google Developers Guide](https://developers.google.com/identity/sms-retriever/verify)

## Basic Usage

To start listening for a single incoming SMS with a verification code during 5 minutes use the following method.

```javascript
cordova.plugins.smsRetriever.start(function() {
	// Listener registered successfully
}, function(err) {
	// Failed to register listener
	console.error(err);
});
```

Calling the above method will only start listening for incoming SMS messages with
the application signature. In order to attach an event handler to the SMS received event
you will have to call the "on" method.

```javascript
cordova.plugins.smsRetriever.on('smsReceived', function(smsMessage) {
  // Sms received
  console.error(smsMessage);
});
```

## API reference

### cordova.plugins.smsRetriever.start(successHandler, errorHandler)

Starts listening for one single SMS message during 5 minutes.

#### Parameters

| Parameter        | Type       | Default | Description                                                                                                                                                                                      |
| ---------------- | ---------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `successHandler` | `Function` |         | Is called when the plugin successfully starts listening number.                                                                                                                                  |
| `errorHandler`   | `Function` |         | Is called when the plugin encounters an error while trying to start listening number.                                                                                                            |

#### Example

```javascript
cordova.plugins.smsRetriever.start(function() {
	// Listener registered successfully
}, function(err) {
	// Failed to register listener
	console.error(err);
});
```

### cordova.plugins.smsRetriever.stop(successHandler, errorHandler)

Stops listening for SMS messages

#### Parameters

| Parameter        | Type       | Default | Description                                                                                                                                                                                      |
| ---------------- | ---------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `successHandler` | `Function` |         | Is called when the plugin successfully stops listening number.                                                                                                                                  |
| `errorHandler`   | `Function` |         | Is called when the plugin encounters an error while trying to stop listening number.                                                                                                            |

#### Example

```javascript
cordova.plugins.smsRetriever.stop(function() {
	// Listener registered successfully
}, function(err) {
	// Failed to register listener
	console.error(err);
});
```

### cordova.plugins.smsRetriever.on(eventName, eventHandler)

Registers an event handler for the event name.

#### Parameters

| Parameter  | Type       | Default | Description                                                        |
| ---------- | ---------- | ------- | ------------------------------------------------------------------ |
| `eventName`    | `string`   |         | Name of the event to listen to. See below for all the event names. |
| `eventHandler` | `Function` |         | Is called when the event is triggered.

### cordova.plugins.smsRetriever.on('smsReceived', eventHandler)

Fired when the listener receives an SMS message.

#### Callback parameters

| Parameter               | Type     | Description                                                                     |
| ----------------------- | -------- | ------------------------------------------------------------------------------- |
| `smsMessage`   | `string` | The complete received SMS message             |

#### Example

```javascript
cordova.plugins.smsRetriever.on('smsReceived', (smsMessage) => {
  // Sms received
  console.error('This is the content of the SMS message: ' + smsMessage);
});
```

### cordova.plugins.smsRetriever.on('timeout', eventHandler)

Fired when the listener stops listening after 5 minutes from start.

### cordova.plugins.smsRetriever.off(eventName, eventHandler)

Unregisters an event handler for the event name. If eventHandler is not defined, unregisters all events of the eventName.

#### Parameters

| Parameter  | Type       | Default | Description                                                        |
| ---------- | ---------- | ------- | ------------------------------------------------------------------ |
| `eventName`    | `string`   |         | Name of the event to unregister. |
| `eventHandler` | `Function` |         | The eventHandler to unregister. If not provided, all the events of the eventName will be unregistered |

#### Example

```javascript
cordova.plugins.smsRetriever.off('smsReceived');
```

## Further Considerations

If you want to debug the SMS reception with Android Studio, remember to include
the same signing configuration as the one you have use to create the SMS text
signature in the app Module by right clicking in your project, chosing the
Open Module Settings options and selecting the app Module.

## License
Copyright &copy; 2020 José Lorente Martín <jose.lorente.martin@gmail.com>.

Licensed under the BSD 3-Clause License. See LICENSE.txt for details.
