# cordova-plugin-flic2


This plugin provides the ability to interact with Flic2 buttons.

Although in the global scope, it is not available until after the `deviceready` event.

```js
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    console.log(flic2);
}
```

## Installation

```bash
cordova plugin add cordova-plugin-flic2
```

## Supported Platforms

- Android


## How to use

First initialize the plugin
```js
flic2.init()
```

### Scan for new buttons
```js
flic2.startScan(success, error)
```

#### Parameters
- __success__: A callback to be called for each step of the scan. Takes an object argument. The properties on the object are `statusCode` and `button`. The `button` will only be set on success. See `Constants` for all status codes.
- __error__: A callback for errors. Will always be an integer. See `Constants` for all failures;

### Stop scan for buttons
```js
flic2.stopScan()
```

### Get buttons
```js
flic2.getButtons(success, error)
```

#### Parameters
- __success__: A callback called when successful. It takes a list of buttons as argument.
- __error__: A callback for errors. 


## Buttons
`getButtons` and `startScan` returns buttons. Each buttons has some properties and methods.

### Properties
- `name`: Name
- `bdAddr`: Bluetooth address
- `connectionState`: Connection state
- `firmwareVersion`: Firmware version
- `getLastKnownBatteryLevel`: Last known battery level
- `pressCount`: Press count
- `readyTimestamp`: Timestamp when the button was ready
- `serialNumber`: Serial number
- `uuid`: Unique button id

### Methods
- `connect`: Connects the button
- `disconnectOrAbortPendingConnection`: Disconnects the button
- `setName`: Set the name of the button
- `setAutoDisconnectTime`: Set auto disconnect time
- `onButtonSingleOrDoubleClickOrHold`: Add event listener for single, double, click or hold events
- `onAllQueuedButtonEventsProcessed`: Add event listener for when all button events are processed
- `onBatteryLevelUpdated`: Add event listener for battery level updated
- `onButtonClickOrHold`: Add event listener for click or hold events
- `onButtonSingleOrDoubleClick`: Add event listener for single or double click events
- `onButtonUpOrDown`:' Add event listener for button up or button down events
- `onConnect`: Add event listener for connect events
- `onDisconnect`: Add event listener for disconnect events
- `onFailure`: Add event listener for failure events
- `onReady`: Add event listener for ready events
- `onFirmwareVersionUpdated`: Add event listener for firmware version updates events
- `onNameUpdated`: Add event listener for name update events
- `onUnpaired`: Add event listener for unpaired events

Every event listener can be removed by calling the `un` instead of `on` method, example `unButtonClickOrHold`



## Constants
The following constants are reported as part of the callback for
`startScan`:
  - `flic2.SCAN_RESULT.ALREADY_PAIRED` = 0;
  - `flic2.SCAN_RESULT.DISCOVERED` = 1;
  - `flic2.SCAN_RESULT.CONNECTED` = 2;
  - `flic2.SCAN_RESULT.COMPLETE` = 3;
  - `flic2.SCAN_RESULT.FAILED_ALREADY_RUNNING` = 1;
  - `flic2.SCAN_RESULT.FAILED_BLUETOOTH_OFF` = 2;
  - `flic2.SCAN_RESULT.FAILED_SCAN_ERROR` = 3;
  - `flic2.SCAN_RESULT.FAILED_NO_NEW_BUTTONS_FOUND` = 4;
  - `flic2.SCAN_RESULT.FAILED_BUTTON_ALREADY_CONNECTED_TO_OTHER_DEVICE` = 5;
  - `flic2.SCAN_RESULT.FAILED_CONNECT_TIMED_OUT` = 6;
  - `flic2.SCAN_RESULT.FAILED_VERIFY_TIMED_OUT` = 7;


