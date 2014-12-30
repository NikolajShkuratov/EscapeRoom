package escape_room.escaperoom.rooms.lazer_rooms.parent;

import android.view.View;
import android.widget.Button;

import escape_room.escaperoom.R;
import escape_room.escaperoom.rooms.parent_room.Room;
import escape_room.escaperoom.collections.MyBitSet;

/**
 * Created by Dell on 21.12.2014.
 */
public abstract class LazerParentRoom extends Room {

    protected MyBitSet lazerBitSet = new MyBitSet(MyBitSet.LAZER_SET_BYTE_SIZE);

    protected boolean isReturned = false;

    private class OnLazerButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();

            if (lazerBitSet.getBit(position)){

                view.setBackgroundColor(getResources().getColor(R.color.silver));
                lazerBitSet.clearBit(position);

            }
            else{

                view.setBackgroundColor(getResources().getColor(R.color.red));
                lazerBitSet.setBit(position);

            }

            lazerButtonAdditionalAction();

        }
    }

    OnLazerButtonOnClickListener onLazerButtonOnClickListener = new OnLazerButtonOnClickListener();

    @Override
    protected void roomInit() {

        lazerButtonsInit();

        specificAction();

    }

    protected void lazerButtonsInit(){
        Button currentButt;
        for (int i=0;i<24;i++){
            currentButt = (Button) thisRoom.findViewById(R.id.b1+i);
            currentButt.setOnClickListener(onLazerButtonOnClickListener);
            currentButt.setTag(i);
        }

    }

    /*@Override
    protected void onReturn() {
        if (isReturned){
            additionalRecovery();

            activedButtonsSetRed();

        }
    }*/

    protected void activedButtonsSetRed(){
        for (int i=0;i<3;i++){
            for (int j=0;j<8;j++){
                if (lazerBitSet.getBit(i*8+j)){
                    thisRoom.findViewById(R.id.b1+i*8+j).setBackgroundColor(
                            getResources().getColor(R.color.red));
                }
                else{
                    thisRoom.findViewById(R.id.b1+i*8+j).setBackgroundColor(
                            getResources().getColor(R.color.silver));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (thisRoom!=null) {
            additionalRecovery();

            activedButtonsSetRed();
        }
    }

    protected abstract void lazerButtonAdditionalAction();

    protected abstract void additionalRecovery();

    protected abstract void specificAction();
}
