package escape_room.escaperoom.services.bluetooth_communication_service.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;

import escape_room.escaperoom.services.bluetooth_communication_service.CommunicationHandler;

/**
 * Created by Dell on 27.12.2014.
 */
public class TransmissionTask {

    private CommunicationHandler communicationHandler;
    private TransmittingTask transmittingTask;

    private boolean transmissionIsCompleted=true;

    public boolean isTransmissionIsCompleted() {
        return transmissionIsCompleted;
    }

    public TransmissionTask(CommunicationHandler communicationHandler){
        this.communicationHandler = communicationHandler;
    }

    public boolean startTransmission(byte data[]){
        if (transmissionIsCompleted &
                communicationHandler.getConnectionStatus()==ConnectionTask.SOCKET_IS_CONNECTED) {
            transmissionIsCompleted = false;
            transmittingTask = new TransmittingTask();
            transmittingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data);
            return true;
        }
        else{
            return false;
        }
    }

    private class TransmittingTask extends AsyncTask<byte[],Void,Void>{

        private OutputStream outputStream;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                outputStream = communicationHandler.getBluetoothSocket().getOutputStream();
            }
            catch (IOException e){

            }
        }

        @Override
        protected Void doInBackground(byte[]... bytes) {
            try{
                outputStream.write(bytes[0]);
            }
            catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*try {
                outputStream.close();
            }
            catch (IOException e) {

            }
            */
            transmissionIsCompleted = true;
        }
    }
}
