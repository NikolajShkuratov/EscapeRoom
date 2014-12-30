package escape_room.escaperoom.rooms.lazer_rooms.lazer_form;

import escape_room.escaperoom.collections.MyBitSet;

/**
 * Created by Dell on 21.12.2014.
 */
public class LazerForm  {
    private MyBitSet lazerBitSet = new MyBitSet(MyBitSet.LAZER_SET_BYTE_SIZE);

    public MyBitSet getLazerBitSet() {
        return lazerBitSet;
    }

    private boolean formState = false;

    public boolean isThisFormActived(){
        return formState;
    }

    public void setFormState (boolean state){
        formState=state;
    }

}
