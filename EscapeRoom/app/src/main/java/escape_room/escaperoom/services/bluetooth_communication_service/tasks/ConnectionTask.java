package escape_room.escaperoom.services.bluetooth_communication_service.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import escape_room.escaperoom.services.bluetooth_communication_service.CommunicationHandler;

/**
 * Created by Dell on 27.12.2014.
 */
public class ConnectionTask {

    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String HC_05_DEVICE_NAME = "HC-05";

    public static final String COMMUNICATION_FILE_NAME = "communicationInfoFile.txt";

    public static final boolean SOCKET_IS_CONNECTED = true;
    public static final boolean SOCKET_IS_NOT_CONNECTED = false;

    private boolean deviceHaveBeenFound;
    private boolean broadcastSearchingIsStarted;

    public void setDeviceHaveBeenFound(boolean deviceHaveBeenFound) {
        this.deviceHaveBeenFound = deviceHaveBeenFound;
    }

    public void setBroadcastSearchingIsStarted(boolean broadcastSearchingIsStarted) {
        this.broadcastSearchingIsStarted = broadcastSearchingIsStarted;
    }

    private boolean connectionIsStarted;

    public boolean isConnectionStarted(){
        return connectionIsStarted;
    }

    CommunicationHandler communicationHandler;
    ConnectingTask connectingTask;

    public ConnectionTask(CommunicationHandler communicationHandler){
        this.communicationHandler = communicationHandler;
        connectionIsStarted = false;
    }

    public void startConnection(){
        if (!connectionIsStarted) {
            deviceHaveBeenFound = false;
            broadcastSearchingIsStarted = false;
            connectingTask = new ConnectingTask();
            connectionIsStarted = true;
            connectingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class ConnectingTask extends AsyncTask<Void,Void,Void> {
        private static final int MAC_CONNECTION = 1;
        private static final int BROADCASTING_CONNECTION = 2;
        private int connectionStage;

        private void deviceSearching(){
            if (connectionStage == MAC_CONNECTION) {
                macConnection();
                connectionStage = BROADCASTING_CONNECTION;
            }
            else{
                broadcastConnection();
                connectionStage = MAC_CONNECTION;
            }
        }

        private void macConnection(){
            communicationHandler.setInitialMacAddress(communicationHandler.getEscapeRoomActivity().getServices()
                    .getArchivationService().stringReadFromFile(COMMUNICATION_FILE_NAME));
            if ((communicationHandler.getInitialMacAddress() != null)&
                    (communicationHandler.getBluetoothAdapter().checkBluetoothAddress(
                            communicationHandler.getInitialMacAddress()))) {
                communicationHandler.setBluetoothDevice(communicationHandler.getBluetoothAdapter()
                        .getRemoteDevice(communicationHandler.getInitialMacAddress()));
                deviceHaveBeenFound=true;
            }
        }

        private boolean pairConnection(){
            boolean operationSucceed = false;
            Set<BluetoothDevice> pairedDevices = communicationHandler.getBluetoothAdapter().getBondedDevices();
            if (pairedDevices.size()>0) {
                for (BluetoothDevice sDevice : pairedDevices) {
                    if (sDevice.getName().equals(HC_05_DEVICE_NAME)) {
                        deviceHaveBeenFound = true;
                        communicationHandler.setInitialMacAddress(sDevice.getAddress());
                        communicationHandler.setBluetoothDevice(sDevice);
                        operationSucceed = true;
                        break;
                    }
                }
            }
            return operationSucceed;
        }

        private void broadcastConnection(){
            if (!communicationHandler.getBluetoothAdapter().isDiscovering()) {
                if (communicationHandler.getBluetoothAdapter().startDiscovery()) {
                    broadcastSearchingIsStarted = true;
                }
            }
            while (broadcastSearchingIsStarted) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e) { }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceHaveBeenFound = false;
            broadcastSearchingIsStarted = false;
            connectionStage = MAC_CONNECTION;
            boolean connectionStatus=SOCKET_IS_NOT_CONNECTED;
            while(connectionStatus == SOCKET_IS_NOT_CONNECTED) {
                deviceSearching();
                if (deviceHaveBeenFound) {
                    if (communicationHandler.getBluetoothAdapter().isDiscovering()) {
                        communicationHandler.getBluetoothAdapter().cancelDiscovery();
                    }
                    try {
                        communicationHandler.setBluetoothSocket(communicationHandler.getBluetoothDevice()
                                .createRfcommSocketToServiceRecord(SPP_UUID));
                        communicationHandler.getBluetoothSocket().connect();
                        connectionStatus = SOCKET_IS_CONNECTED;
                    }
                    catch (IOException e) {
                        try {
                            communicationHandler.getBluetoothSocket().close();
                        }
                        catch (IOException ec) { }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*if (receiverIsRegistered){
                communicationHandler.getActivity().unregisterReceiver(broadcastReceiver);
            }*/
            connectionIsStarted=false;
            communicationHandler.connectionHaveBeenDone();
        }
    }

}
