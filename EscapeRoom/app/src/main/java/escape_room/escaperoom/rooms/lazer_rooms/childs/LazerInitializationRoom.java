package escape_room.escaperoom.rooms.lazer_rooms.childs;

import android.view.View;
import android.widget.Button;

import escape_room.escaperoom.rooms.lazer_rooms.parent.LazerParentRoom;
import escape_room.escaperoom.R;

/**
 * Created by Dell on 20.12.2014.
 */
public class LazerInitializationRoom extends LazerParentRoom {

    public static final boolean ACTIVED_FORM = true;
    public static final boolean NOT_ACTIVED_FORM = false;


    @Override
    public void notifyStatusChange(int reason,Object data) {
        super.notifyStatusChange(reason, data);
        if (reason==roomHandler.BLUETOOTH_STATUS_CHANGE) {
            boolean bluetoothStatus = (Boolean) data;
            Button activeLazersButton = (Button) thisRoom.findViewById(R.id.bApplyLazetSet);
            if (bluetoothStatus) {
                activeLazersButton.setBackgroundColor(getResources().getColor(R.color.green));
                activeLazersButton.setText("Set Lazers");
            } else {
                activeLazersButton.setBackgroundColor(getResources().getColor(R.color.red));
                activeLazersButton.setText("Error");
            }
        }
        /*if (reason==roomHandler.BLUETOOTH_TEST){
            Button testButton = (Button) thisRoom.findViewById(R.id.b24);
            testButton.setText((String)data);
        }*/
    }

    private class OnFormButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (dataService.getFormSet().get(position).isThisFormActived()==NOT_ACTIVED_FORM){
                dataService.getFormSet().get(position).setFormState(ACTIVED_FORM);
                view.setBackgroundColor(getResources().getColor(R.color.red));
                for (int j=0;j<3;j++){
                    if (j!=position){
                        if (dataService.getFormSet().get(j).isThisFormActived()==ACTIVED_FORM) {
                            dataService.getFormSet().get(j).setFormState(NOT_ACTIVED_FORM);
                            thisRoom.findViewById(R.id.bForm1+j).setBackgroundColor(getResources()
                                    .getColor(R.color.silver));
                        }
                    }
                }
                lazerBitSet.setBitSet(dataService.getFormSet().get(position).getLazerBitSet());
            }
            else{
                dataService.getFormSet().get(position).setFormState(NOT_ACTIVED_FORM);
                lazerBitSet.clearBitSet();
                view.setBackgroundColor(getResources().getColor(R.color.silver));
            }
            activedButtonsSetRed();
        }
    }


    @Override
    protected void additionalRecovery() {
        for (int i=0;i<3;i++){

            if (dataService.getFormSet().get(i).isThisFormActived()==ACTIVED_FORM){
                thisRoom.findViewById(R.id.bForm1+i).setBackgroundColor(getResources().getColor(R.color.red));
                lazerBitSet.setBitSet(dataService.getFormSet().get(i).getLazerBitSet());
            }

        }
        Button applyButton = (Button)thisRoom.findViewById(R.id.bApplyLazetSet);
        if (communicationHandler.getConnectionStatus()) {
            applyButton.setBackgroundColor(getResources().getColor(R.color.green));
            applyButton.setText("Set Lazers");
        }
        else{
            applyButton.setBackgroundColor(getResources().getColor(R.color.red));
            applyButton.setText("Error");
        }

    }

    OnFormButtonOnClickListener onFormButtonOnClickListener = new OnFormButtonOnClickListener();

    @Override
    protected void specificAction() {
        Button button;
        for(int i=0;i<3;i++){
            button = (Button) thisRoom.findViewById(R.id.bForm1+i);
            button.setOnClickListener(onFormButtonOnClickListener);
            button.setTag(i);
        }
        thisRoom.findViewById(R.id.bApplyLazetSet).setBackgroundColor(getResources().getColor(R.color.red));
        thisRoom.findViewById(R.id.bApplyLazetSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (communicationHandler.getTransmissionTask().isTransmissionIsCompleted()){
                    communicationHandler.getTransmissionTask().startTransmission(lazerBitSet.getBytes());
                }

            }
        });

        thisRoom.findViewById(R.id.bFormEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roomHandler.goToDirectRoom(roomHandler.ADJUSTING_ROOM);
                isReturned=true;
            }
        });
    }

    @Override
    protected void lazerButtonAdditionalAction() {
        for(int i=0;i<3;i++) {
            if (dataService.getFormSet().get(i).isThisFormActived()) {
                dataService.getFormSet().get(i).setFormState(NOT_ACTIVED_FORM);
                thisRoom.findViewById(R.id.bForm1+i).setBackgroundColor(
                        getResources().getColor(R.color.silver));
            }
        }
    }


}
