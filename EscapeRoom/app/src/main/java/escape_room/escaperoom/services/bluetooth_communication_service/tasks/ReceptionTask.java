package escape_room.escaperoom.services.bluetooth_communication_service.tasks;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import escape_room.escaperoom.services.bluetooth_communication_service.CommunicationHandler;

/**
 * Created by Dell on 27.12.2014.
 */
public class ReceptionTask {
    private static final int BUFFER_SIZE = 128;
    private static final byte MESSAGE_END_VALUE = 126;

    private CommunicationHandler communicationHandler;
    private ReceivingTask receivingTask;

    public ReceptionTask(CommunicationHandler communicationHandler){
        this.communicationHandler = communicationHandler;
    }

    public void startReception(){
        receivingTask = new ReceivingTask();
        receivingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopReception(){
        receivingTask.cancel(true);
    }

    private class ReceivingTask extends AsyncTask<Void,String,Void>{

        private InputStream inputStream;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try{
                inputStream = communicationHandler.getBluetoothSocket().getInputStream();
            }
            catch (IOException e){ }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean socketIsOpen = true;

            byte receiver[] = new byte[BUFFER_SIZE];
            int receivedBytes;

            byte buffer[] = new byte[BUFFER_SIZE];
            int bufferByteSize=0;

            while (socketIsOpen & !isCancelled()){
                try {
                    receivedBytes = inputStream.read(receiver);
                    for (int i=0;i<receivedBytes;i++){
                        buffer[bufferByteSize] = receiver[i];
                        bufferByteSize++;
                    }
                    if (buffer[bufferByteSize-1]==MESSAGE_END_VALUE) {
                        byte message[] = new byte[bufferByteSize-1];
                        for (int i=0;i<bufferByteSize-1;i++){
                            message[i]=buffer[i];
                        }
                        bufferByteSize=0;
                        String data = new String (message);
                        publishProgress(data);
                    }
                }
                catch (IOException e){
                    socketIsOpen = false;
                    closeInputStream();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            communicationHandler.onDataReceive(values[0]);
        }

        private void closeInputStream(){
            try {
                inputStream.close();
            }
            catch (IOException ee){

            }
        }
    }

}
