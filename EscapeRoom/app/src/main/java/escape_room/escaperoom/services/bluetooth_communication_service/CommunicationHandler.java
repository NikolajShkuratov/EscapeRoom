package escape_room.escaperoom.services.bluetooth_communication_service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;

import escape_room.escaperoom.EscapeRoomActivity;
import escape_room.escaperoom.rooms.RoomHandler;
import escape_room.escaperoom.services.bluetooth_communication_service.tasks.ConnectionStatusMonitoringTask;
import escape_room.escaperoom.services.bluetooth_communication_service.tasks.ConnectionTask;
import escape_room.escaperoom.services.bluetooth_communication_service.tasks.ReceptionTask;
import escape_room.escaperoom.services.bluetooth_communication_service.tasks.TransmissionTask;
import escape_room.escaperoom.services.data_service.DataService;

/**
 * Created by Dell on 27.12.2014.
 */
public class CommunicationHandler {

    public static final int ENABLE_REQUEST_CODE = 1;

//////////////TASKS//////////////
    ConnectionTask connectionTask;
    public ConnectionTask getConnectionTask() {
        return connectionTask;
    }

    public void connectionHaveBeenDone(){
        receptionTask.startReception();
        connectionStatusMonitoringTask.setTimeoutFlag();
        communicationStatusUpdate(true);
    }

    ReceptionTask receptionTask;

    TransmissionTask transmissionTask;
    public TransmissionTask getTransmissionTask() {
        return transmissionTask;
    }

    ConnectionStatusMonitoringTask connectionStatusMonitoringTask;
//////////////SERVICES & CONSTRUCTOR//////////////
    private DataService dataService;

    public DataService getDataService() {
        return dataService;
    }

    private EscapeRoomActivity escapeRoomActivity;
    public EscapeRoomActivity getEscapeRoomActivity() {
        return escapeRoomActivity;
    }

    public CommunicationHandler(DataService dataService,EscapeRoomActivity escapeRoomActivity){
        this.dataService = dataService;
        this.escapeRoomActivity = escapeRoomActivity;
        connectionTask = new ConnectionTask(this);
        receptionTask = new ReceptionTask(this);
        transmissionTask = new TransmissionTask(this);
        connectionStatusMonitoringTask = new ConnectionStatusMonitoringTask(this);
        connectionStatus=false;
    }
//////////////BLUETOOTH ADAPTER//////////////
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
//////////////BLUETOOTH DEVICE//////////////
    private BluetoothDevice bluetoothDevice;
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
//////////////SOCKET//////////////
    private BluetoothSocket bluetoothSocket;

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public void socketClose(){
        try {
            bluetoothSocket.close();
        }
        catch (IOException e) { }
    }

//////////////INITIAL MAC ADDRES//////////////
    private String initialMacAddress;

    public String getInitialMacAddress() {
        return initialMacAddress;
    }

    public void setInitialMacAddress(String initialMacAddress) {
        this.initialMacAddress = initialMacAddress;
    }

    public static final String COMMUNICATION_FILE_NAME = "communicationInfoFile.txt";
    public void saveInitialMacAddress(){
        escapeRoomActivity.getServices().getArchivationService()
                .stringWriteInFile(COMMUNICATION_FILE_NAME, initialMacAddress);
    }

//////////////
    public void communicationServiceInit(){
        if (bluetoothAdapter!=null) { //the device supports bluetooth
            if (!bluetoothAdapter.isEnabled()) { //bluetooth isnt enabled
                Intent bluetoothEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                escapeRoomActivity.startActivityForResult(bluetoothEnableIntent,ENABLE_REQUEST_CODE);
            }
            else {
                startConnection();
            }
        }
    }

    private boolean connectionStatus;
    public boolean getConnectionStatus(){
        return connectionStatus;
    }

    public void startConnection(){
        connectionTask.startConnection();
    }

    public void startReconnection(){
        if (!connectionTask.isConnectionStarted()) {
            communicationStatusUpdate(ConnectionTask.SOCKET_IS_NOT_CONNECTED);
            receptionTask.stopReception();
            socketClose();
            connectionTask.startConnection();
        }
    }


    public void onDataReceive(String data){
        if (data.equals(ConnectionStatusMonitoringTask.CONNECTION_STATUS_CHECK_MESSAGE)){
            connectionStatusMonitoringTask.connectionFlagSet();
        }
        else {
            if (data.equals(ConnectionStatusMonitoringTask.START_CONNECTION_STATUS_CHECK_MESSAGE)) {
                connectionStatusMonitoringTask.startConnectionStatusMonitoring();
            }
            else {
                dataService.getRoomHandler().roomNotification(RoomHandler.BLUETOOTH_TEST, data);
            }
        }
    }

    public void communicationStatusUpdate(boolean connectionStatus){
        if (this.connectionStatus!=connectionStatus){
            onCommunicationStatusChange(connectionStatus);
        }
        this.connectionStatus=connectionStatus;
        if (connectionStatus==ConnectionTask.SOCKET_IS_NOT_CONNECTED){
            connectionTask.startConnection();
        }
    }

    private void onCommunicationStatusChange(boolean connectionStatus){
        dataService.getRoomHandler().roomNotification(dataService.getRoomHandler()
                .BLUETOOTH_STATUS_CHANGE, connectionStatus);
    }

}
