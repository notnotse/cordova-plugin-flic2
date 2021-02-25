const BUTTON_EVENT_TYPES = {
  BUTTON_ON_SINGLE_OR_DOUBLE_CLICK_OR_HOLD: 0,
  BUTTON_ON_ALL_QUEUED_BUTTON_EVENTS_PROCESSED: 1,
  BUTTON_ON_BATTERY_LEVEL_UPDATED: 2,
  BUTTON_ON_CLICK_OR_HOLD: 3,
  BUTTON_ON_SINGLE_OR_DOUBLE_CLICK: 4,
  BUTTON_ON_UP_OR_DOWN: 5,
  BUTTON_ON_CONNECT: 6,
  BUTTON_ON_DISCONNECT: 7,
  BUTTON_ON_FAILURE: 8,
  BUTTON_ON_READY: 9,
  BUTTON_ON_FIRMWARE_VERSION_UPDATED: 10,
  BUTTON_ON_NAME_UPDATED: 11,
  BUTTON_ON_UNPAIRED: 12,
};

const SCAN_RESULT = {
  ALREADY_PAIRED: 0,
  DISCOVERED: 1,
  CONNECTED: 2,
  COMPLETE: 3,
  FAILED_ALREADY_RUNNING: 1,
  FAILED_BLUETOOTH_OFF: 2,
  FAILED_SCAN_ERROR: 3,
  FAILED_NO_NEW_BUTTONS_FOUND: 4,
  FAILED_BUTTON_ALREADY_CONNECTED_TO_OTHER_DEVICE: 5,
  FAILED_CONNECT_TIMED_OUT: 6,
  FAILED_VERIFY_TIMED_OUT: 7,
};

const voidCallback = (_) => {};
const buttonListeners = {};
let globalButtonListeners = [];

function buttonCallback(event) {
  if (buttonListeners[event.uuid] && buttonListeners[event.uuid][event.type]) {
    buttonListeners[event.uuid][event.type].forEach((callback) =>
      callback(event.payload)
    );
  }
  globalButtonListeners
    .filter((globalButtonListener) => {
      return globalButtonListener.eventType === event.type;
    })
    .forEach((globalButtonListener) => {
      globalButtonListener.callback(event);
    });
}

function addButtonCallback(uuid, callback, eventType) {
  if (!buttonListeners[uuid]) {
    buttonListeners[uuid] = {};
  }
  if (!buttonListeners[uuid][eventType]) {
    buttonListeners[uuid][eventType] = [];
  }
  buttonListeners[uuid][eventType].push(callback);
}

function removeButtonCallback(uuid, callback, eventType) {
  if (!buttonListeners[uuid]) {
    return;
  }
  if (!buttonListeners[uuid][eventType]) {
    return;
  }
  buttonListeners[uuid][eventType] = buttonListeners[uuid][eventType].filter(
    (c) => c != callback
  );
}

let pluginInit = false;

function init() {
  if (pluginInit) {
    return;
  }
  cordova.exec(buttonCallback, console.error, "Flic2", "init", []);
  pluginInit = true;
}

function startScan(callback, error) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(
    (result) => {
      if (result.statusCode == SCAN_RESULT.SCAN_RESULT_COMPLETE) {
        result.button = enrichButton(result.button);
      }
      callback(result);
    },
    error,
    "Flic2",
    "startScan",
    []
  );
}

function stopScan() {
  if (!pluginInit) {
    return;
  }
  cordova.exec(voidCallback, console.error, "Flic2", "startScan", []);
}

function connect(uuid) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(voidCallback, console.error, "Flic2", "button.connect", [uuid]);
}

function disconnectOrAbortPendingConnection(uuid) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(
    voidCallback,
    console.error,
    "Flic2",
    "button.disconnectOrAbortPendingConnection",
    [uuid]
  );
}

function on(eventType, callback) {
  if (!pluginInit) {
    return;
  }
  globalButtonListeners.push({ eventType, callback });
}

function un(callback) {
  if (!pluginInit) {
    return;
  }
  globalButtonListeners = globalButtonListeners.filter(
    (globalButtonListener) => {
      return globalButtonListener.callback !== callback;
    }
  );
}

function setName(uuid, name) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(voidCallback, console.error, "Flic2", "button.setName", [
    uuid,
    name,
  ]);
}

function setAutoDisconnectTime(time) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(
    voidCallback,
    console.error,
    "Flic2",
    "button.setAutoDisconnectTime",
    [uuid, time]
  );
}

function enrichButton(button) {
  return {
    ...button,
    connect: () => connect(button.uuid),
    disconnectOrAbortPendingConnection: () =>
      disconnectOrAbortPendingConnection(button.uuid),
    setName: (name) => setName(button.uuid, name),
    setAutoDisconnectTime: (time) => setAutoDisconnectTime(button.uuid, time),
    onButtonSingleOrDoubleClickOrHold: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_SINGLE_OR_DOUBLE_CLICK_OR_HOLD
      ),
    onAllQueuedButtonEventsProcessed: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_ALL_QUEUED_BUTTON_EVENTS_PROCESSED
      ),
    onBatteryLevelUpdated: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_BATTERY_LEVEL_UPDATED
      ),
    onButtonClickOrHold: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_CLICK_OR_HOLD
      ),
    onButtonSingleOrDoubleClick: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_SINGLE_OR_DOUBLE_CLICK
      ),
    onButtonUpOrDown: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_UP_OR_DOWN
      ),
    onConnect: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_CONNECT
      ),
    onDisconnect: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_DISCONNECT
      ),
    onFailure: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_FAILURE
      ),
    onReady: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_READY
      ),
    onFirmwareVersionUpdated: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_FIRMWARE_VERSION_UPDATED
      ),
    onNameUpdated: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_NAME_UPDATED
      ),
    onUnpaired: (callback) =>
      addButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_UNPAIRED
      ),
    unButtonSingleOrDoubleClickOrHold: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_SINGLE_OR_DOUBLE_CLICK_OR_HOLD
      ),
    unAllQueuedButtonEventsProcessed: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_ALL_QUEUED_BUTTON_EVENTS_PROCESSED
      ),
    unBatteryLevelUpdated: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_BATTERY_LEVEL_UPDATED
      ),
    unButtonClickOrHold: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_CLICK_OR_HOLD
      ),
    unButtonSingleOrDoubleClick: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_SINGLE_OR_DOUBLE_CLICK
      ),
    unButtonUpOrDown: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_UP_OR_DOWN
      ),
    unConnect: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_CONNECT
      ),
    unDisconnect: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_DISCONNECT
      ),
    unFailure: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_FAILURE
      ),
    unReady: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_READY
      ),
    unFirmwareVersionUpdated: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_FIRMWARE_VERSION_UPDATED
      ),
    unNameUpdated: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_NAME_UPDATED
      ),
    unUnpaired: (callback) =>
      removeButtonCallback(
        button.uuid,
        callback,
        BUTTON_EVENT_TYPES.BUTTON_ON_UNPAIRED
      ),
  };
}

function getButtons(callback, error) {
  if (!pluginInit) {
    return;
  }
  cordova.exec(
    (buttons) => {
      callback(buttons.map(enrichButton));
    },
    error,
    "Flic2",
    "getButtons",
    []
  );
}

const flic2 = {
  init,
  startScan,
  stopScan,
  getButtons,
  connect,
  disconnectOrAbortPendingConnection,
  on,
  un,
  setName,
  setAutoDisconnectTime,
  SCAN_RESULT,
  BUTTON_EVENT_TYPES,
};

module.exports = flic2;
