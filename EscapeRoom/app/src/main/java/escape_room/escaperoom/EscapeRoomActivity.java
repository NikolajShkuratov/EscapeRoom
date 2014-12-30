package escape_room.escaperoom;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import escape_room.escaperoom.rooms.RoomHandler;
import escape_room.escaperoom.rooms.adjusting_room.FormAdjustingRoom;
import escape_room.escaperoom.rooms.lazer_rooms.childs.LazerFormEditRoom;
import escape_room.escaperoom.rooms.lazer_rooms.childs.LazerInitializationRoom;
import escape_room.escaperoom.services.Services;
import escape_room.escaperoom.services.bluetooth_communication_service.tasks.ConnectionTask;

/**
 * Created by Dell on 19.12.2014.
 */
public class EscapeRoomActivity extends Activity{

    private RoomHandler roomHandler;
    private Services services;

    public Services getServices() {
        return services;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        roomHandler = new RoomHandler(this,getFragmentManager(),R.id.main_frame);
        services = new Services(this,getExternalCacheDir().getParent());

        roomHandler.roomsInit();
        services.getDataService().setRoomHandler(roomHandler);
        services.getDataService().formsInit();
        services.getCommunicationHandler().communicationServiceInit();
        registerBluetoothBroadcastReceiver();
        roomHandler.goToDirectRoom(roomHandler.LAZER_ROOM);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice sDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (sDevice.getName().equals(ConnectionTask.HC_05_DEVICE_NAME)){
                    services.getCommunicationHandler().getBluetoothAdapter().cancelDiscovery();
                    services.getCommunicationHandler().setInitialMacAddress(sDevice.getAddress());
                    services.getCommunicationHandler().setBluetoothDevice(sDevice);
                    services.getCommunicationHandler().getConnectionTask().setDeviceHaveBeenFound(true);
                    services.getCommunicationHandler().getConnectionTask().setBroadcastSearchingIsStarted(false);
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                services.getCommunicationHandler().getConnectionTask().setDeviceHaveBeenFound(false);
                services.getCommunicationHandler().getConnectionTask().setBroadcastSearchingIsStarted(false);
            }
            /*if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                if (intent.getStringExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE)
                        .equals(BluetoothAdapter.STATE_DISCONNECTED)){
                    services.getCommunicationHandler().communicationStatusUpdate(ConnectionTask.SOCKET_IS_NOT_CONNECTED);
                }
            }*/
        }
    };


    public void registerBluetoothBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        if (roomHandler.isAllowedToPopBackStack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        services.getDataService().formsSave();
        services.getCommunicationHandler().saveInitialMacAddress();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == services.getCommunicationHandler().ENABLE_REQUEST_CODE){
                services.getCommunicationHandler().startConnection();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        services.getCommunicationHandler().socketClose();
        unregisterReceiver(broadcastReceiver);
    }
}
