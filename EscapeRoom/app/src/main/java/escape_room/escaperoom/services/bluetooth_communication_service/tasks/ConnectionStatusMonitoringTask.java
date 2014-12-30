package escape_room.escaperoom.services.bluetooth_communication_service.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

import escape_room.escaperoom.services.bluetooth_communication_service.CommunicationHandler;

/**
 * Created by Dell on 27.12.2014.
 */
public class ConnectionStatusMonitoringTask {

    public final static int CHECK_PERIOD = 5000; // ms
    public final static int TIMEOUT_PERIOD = 5000; // ms

    public final static boolean FLAG_SET = true;
    public final static boolean FLAG_CLEAR = false;

    public final static String CONNECTION_STATUS_CHECK_MESSAGE = "imHere";
    public final static String START_CONNECTION_STATUS_CHECK_MESSAGE = "followMe";

    private MonitoringTask monitoringTask;
    private CommunicationHandler communicationHandler;

    public ConnectionStatusMonitoringTask (CommunicationHandler communicationHandler){
        this.communicationHandler=communicationHandler;
    }

    public void startConnectionStatusMonitoring(){
        monitoringTask = new MonitoringTask();
        monitoringTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean connectionFlag;

    public void connectionFlagSet(){
        connectionFlag = FLAG_SET;
    }

    private void connectionFlagClear(){
        connectionFlag = FLAG_CLEAR;
    }

    private boolean timeoutFlag;

    public void setTimeoutFlag(){
        timeoutFlag = FLAG_SET;
    }

    private void clearTimeoutFlag(){
        timeoutFlag = FLAG_CLEAR;
    }

    private class MonitoringTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()){
                try {
                    if (timeoutFlag == FLAG_SET){
                        TimeUnit.MILLISECONDS.sleep(TIMEOUT_PERIOD);
                        clearTimeoutFlag();
                    }
                    publishProgress();
                    TimeUnit.MILLISECONDS.sleep(CHECK_PERIOD);
                }
                catch (InterruptedException e){ }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (connectionFlag==FLAG_SET){
                connectionFlagClear();
            }
            else{
                communicationHandler.startReconnection();
            }
        }
    }
}
