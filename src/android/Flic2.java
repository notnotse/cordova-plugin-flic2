package io.notnot.cordova.flic2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flic.flic2libandroid.BatteryLevel;
import io.flic.flic2libandroid.Flic2Button;
import io.flic.flic2libandroid.Flic2ButtonListener;
import io.flic.flic2libandroid.Flic2Manager;
import io.flic.flic2libandroid.Flic2ScanCallback;

public class Flic2 extends CordovaPlugin {


    private static final int BUTTON_ON_SINGLE_OR_DOUBLE_CLICK_OR_HOLD = 0;
    private static final int BUTTON_ON_ALL_QUEUED_BUTTON_EVENTS_PROCESSED = 1;
    private static final int BUTTON_ON_BATTERY_LEVEL_UPDATED = 2;
    private static final int BUTTON_ON_CLICK_OR_HOLD = 3;
    private static final int BUTTON_ON_SINGLE_OR_DOUBLE_CLICK = 4;
    private static final int BUTTON_ON_UP_OR_DOWN = 5;
    private static final int BUTTON_ON_CONNECT = 6;
    private static final int BUTTON_ON_DISCONNECT = 7;
    private static final int BUTTON_ON_FAILURE = 8;
    private static final int BUTTON_ON_READY = 9;
    private static final int BUTTON_ON_FIRMWARE_VERSION_UPDATED = 10;
    private static final int BUTTON_ON_NAME_UPDATED = 11;
    private static final int BUTTON_ON_UNPAIRED = 12;


    private static int SCAN_RESULT_ALREADY_PAIRED = 0;
    private static int SCAN_RESULT_DISCOVERED = 1;
    private static int SCAN_RESULT_CONNECTED = 2;
    private static int SCAN_RESULT_COMPLETE = 3;


    private Flic2Manager manager;
    private boolean isScanning = false;
    private CallbackContext scanningCallbackContext;
    private CallbackContext globalButtonCallbackContext;
    private Map<String, Flic2Button> allButtons;
    private Flic2ButtonListener buttonListener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        allButtons = new HashMap<String, Flic2Button>();
        manager = Flic2Manager.initAndGetInstance(cordova.getActivity().getApplicationContext(), new Handler());
        buttonListener = createButtonListener();
        for (Flic2Button button : manager.getButtons()) {
            button.addListener(buttonListener);
            allButtons.put(button.getUuid(), button);
        }
    }

    public Flic2ButtonListener createButtonListener() {
        return new Flic2ButtonListener() {

            @Override
            public void onButtonSingleOrDoubleClickOrHold(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("wasQueued", wasQueued);
                    payload.put("lastQueued", lastQueued);
                    payload.put("timestamp", timestamp);
                    payload.put("isSingleClick", isSingleClick);
                    payload.put("isDoubleClick", isDoubleClick);
                    payload.put("isHold", isHold);
                    sendButtonEvent(button, BUTTON_ON_SINGLE_OR_DOUBLE_CLICK_OR_HOLD, payload);
                } catch (JSONException e){
                    return;
                }
                
            }

            @Override
            public void onAllQueuedButtonEventsProcessed(Flic2Button button) {
                sendButtonEvent(button, BUTTON_ON_ALL_QUEUED_BUTTON_EVENTS_PROCESSED, null);
            }

            @Override
            public void onBatteryLevelUpdated(Flic2Button button, BatteryLevel level) {
                try {
                    sendButtonEvent(button, BUTTON_ON_BATTERY_LEVEL_UPDATED, lastKnownBatteryLevelToJson(level));
                } catch (JSONException e){
                }
            }

            @Override
            public void onButtonClickOrHold(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isClick, boolean isHold) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("wasQueued", wasQueued);
                    payload.put("lastQueued", lastQueued);
                    payload.put("timestamp", timestamp);
                    payload.put("isClick", isClick);
                    payload.put("isHold", isHold);
                    sendButtonEvent(button, BUTTON_ON_CLICK_OR_HOLD, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onButtonSingleOrDoubleClick(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isSingleClick, boolean isDoubleClick) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("wasQueued", wasQueued);
                    payload.put("lastQueued", lastQueued);
                    payload.put("timestamp", timestamp);
                    payload.put("isSingleClick", isSingleClick);
                    payload.put("isDoubleClick", isDoubleClick);
                    sendButtonEvent(button, BUTTON_ON_SINGLE_OR_DOUBLE_CLICK, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onButtonUpOrDown(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isUp, boolean isDown) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("wasQueued", wasQueued);
                    payload.put("lastQueued", lastQueued);
                    payload.put("timestamp", timestamp);
                    payload.put("isUp", isUp);
                    payload.put("isDown", isDown);
                    sendButtonEvent(button, BUTTON_ON_UP_OR_DOWN, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onConnect(Flic2Button button) {
                sendButtonEvent(button, BUTTON_ON_CONNECT, null);
            }

            @Override
            public void onDisconnect(Flic2Button button) {
                sendButtonEvent(button, BUTTON_ON_DISCONNECT, null);
            }

            @Override
            public void onFailure(Flic2Button button, int errorCode, int subCode) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("errorCode", errorCode);
                    payload.put("subCode", subCode);
                    sendButtonEvent(button, BUTTON_ON_FAILURE, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onReady(Flic2Button button, long timestamp) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("timestamp", timestamp);
                    sendButtonEvent(button, BUTTON_ON_READY, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onFirmwareVersionUpdated(Flic2Button button, int newVersion) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("newVersion", newVersion);
                    sendButtonEvent(button, BUTTON_ON_FIRMWARE_VERSION_UPDATED, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onNameUpdated(Flic2Button button, String newName) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("newName", newName);
                    sendButtonEvent(button, BUTTON_ON_NAME_UPDATED, payload);
                } catch (JSONException e){
                    return;
                }
            }

            @Override
            public void onUnpaired(Flic2Button button) {
                sendButtonEvent(button, BUTTON_ON_UNPAIRED, null);
                allButtons.remove(button.getUuid());
            }
        };
    }

    public void sendButtonEvent(Flic2Button button, int type, JSONObject payload){
        try {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, createButtonEventJson(button, type, payload));
            pluginResult.setKeepCallback(true);

            globalButtonCallbackContext.sendPluginResult(pluginResult);
        } catch(JSONException e){
        }
    }

    public JSONObject createButtonEventJson(Flic2Button button, int type, JSONObject payload) throws JSONException {
        JSONObject event = new JSONObject();
        event.put("type", type);
        event.put("uuid", button.getUuid());
        event.put("payload", payload);
        return event;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            this.globalButtonCallbackContext = callbackContext;
            return true;
        } else if (action.equals("startScan")) {
            this.startScan(callbackContext);
            return true;
        } else if (action.equals("stopScan")) {
            this.stopScan(callbackContext);
            return true;
        } else if (action.equals("getButtons")) {
            this.getButtons(callbackContext);
            return true;
        } else if (action.equals("button.connect")) {
            connectButton(args.getString(0), callbackContext);
        } else if (action.equals("button.setName")) {
            setButtonName(args.getString(0), args.getString(1), callbackContext);
        } else if (action.equals("button.disconnectOrAbortPendingConnection")) {
            disconnectButton(args.getString(0), callbackContext);
        } else if (action.equals("button.setAutoDisconnectTime")) {
            setButtonAutoDisconnectTime(args.getString(0), args.getInt(1), callbackContext);
        }
        return false;
    }

    private void setButtonAutoDisconnectTime(String uid, int time, CallbackContext callbackContext) {
        if(!allButtons.containsKey(uid)){
            callbackContext.error("Unknown button");
            return;
        }
        allButtons.get(uid).setAutoDisconnectTime(time);
        callbackContext.success();
    }

    private void disconnectButton(String uid, CallbackContext callbackContext) {
        if(!allButtons.containsKey(uid)){
            callbackContext.error("Unknown button");
            return;
        }
        allButtons.get(uid).disconnectOrAbortPendingConnection();
        callbackContext.success();
    }

    private void setButtonName(String uid, String name, CallbackContext callbackContext) {
        if(!allButtons.containsKey(uid)){
            callbackContext.error("Unknown button");
            return;
        }
        allButtons.get(uid).setName(name);
        callbackContext.success();
    }

    private void connectButton(String uid, CallbackContext callbackContext) {
        if(!allButtons.containsKey(uid)){
            callbackContext.error("Unknown button");
            return;
        }
        allButtons.get(uid).connect();
        callbackContext.success();
    }

    private void getButtons(CallbackContext callbackContext) {
        try {
            JSONArray result = new JSONArray();
            for (Flic2Button button : allButtons.values()) {
                JSONObject buttonJson = buttonToJson(button);
                result.put(buttonJson);
            }
            callbackContext.success(result);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

    }

    private JSONObject buttonToJson(Flic2Button button) throws JSONException {
        JSONObject buttonJson = new JSONObject();
        buttonJson.put("name", button.getName());
        buttonJson.put("bdAddr", button.getBdAddr());
        buttonJson.put("connectionState", button.getConnectionState());
        buttonJson.put("firmwareVersion", button.getFirmwareVersion());
        buttonJson.put("lastKnownBatteryLevel", lastKnownBatteryLevelToJson(button.getLastKnownBatteryLevel()));
        buttonJson.put("pressCount", button.getPressCount());
        buttonJson.put("readyTimestamp", button.getReadyTimestamp());
        buttonJson.put("serialNumber", button.getSerialNumber());
        buttonJson.put("uuid", button.getUuid());
        return buttonJson;
    }

    private JSONObject lastKnownBatteryLevelToJson(BatteryLevel lastKnownBatteryLevel) throws JSONException{
        JSONObject batteryLevelJson = new JSONObject();
        batteryLevelJson.put("estimatedPercentage", lastKnownBatteryLevel.getEstimatedPercentage());
        batteryLevelJson.put("timestampUtcMs", lastKnownBatteryLevel.getTimestampUtcMs());
        batteryLevelJson.put("voltage", lastKnownBatteryLevel.getVoltage());
        return batteryLevelJson;

    }

    private void stopScan(CallbackContext callbackContext) {
        manager.stopScan();
        isScanning = false;
    }


    private void startScan(CallbackContext callbackContext) {
        if (isScanning) {
            callbackContext.error(Flic2ScanCallback.RESULT_FAILED_ALREADY_RUNNING);
            return;
        }
        this.scanningCallbackContext = callbackContext;
        if (cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            scan();
        } else {
            cordova.requestPermission(this, 0, Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    private void scan() {
        isScanning = true;
        Flic2Manager.getInstance().startScan(new Flic2ScanCallback() {
            @Override
            public void onDiscoveredAlreadyPairedButton(Flic2Button button) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, createScanResultJson(SCAN_RESULT_ALREADY_PAIRED, null));
                pluginResult.setKeepCallback(true);

                scanningCallbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onDiscovered(String bdAddr) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, createScanResultJson(SCAN_RESULT_DISCOVERED, null));
                pluginResult.setKeepCallback(true);

                scanningCallbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onConnected() {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, createScanResultJson(SCAN_RESULT_CONNECTED, null));
                pluginResult.setKeepCallback(true);

                scanningCallbackContext.sendPluginResult(pluginResult);

            }

            @Override
            public void onComplete(int result, int subCode, Flic2Button button) {
                isScanning = false;
                if (result == Flic2ScanCallback.RESULT_SUCCESS) {
                    button.addListener(buttonListener);
                    allButtons.put(button.getUuid(),button);
                    scanningCallbackContext.success(createScanResultJson(SCAN_RESULT_COMPLETE, button));
                } else {
                    scanningCallbackContext.error(result);
                }
            }
        });
    }

    public JSONObject createScanResultJson(int statusCode, Flic2Button button)  {
        JSONObject result = new JSONObject();
        try {
            result.put("statusCode", statusCode);
            if (button != null) {
                result.put("button", buttonToJson(button));
            }
        } catch (JSONException e){

        }
        return result;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                scanningCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, -1));
                return;
            }
        }
        switch (requestCode) {
            case 0:
                scan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Flic2Button button : allButtons.values()) {
            button.removeListener(buttonListener);
        }
    }

    public static class ButtonHolder {

        private final Flic2Button button;
        private final Flic2ButtonListener listener;

        public ButtonHolder(Flic2Button button, Flic2ButtonListener listener) {
            this.button = button;
            this.listener = listener;
        }

        public Flic2Button getButton() {
            return button;
        }

        public Flic2ButtonListener getListener() {
            return listener;
        }
    }

}