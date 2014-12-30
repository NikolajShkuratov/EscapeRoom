package escape_room.escaperoom.rooms;

import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import escape_room.escaperoom.EscapeRoomActivity;
import escape_room.escaperoom.R;
import escape_room.escaperoom.rooms.adjusting_room.FormAdjustingRoom;
import escape_room.escaperoom.rooms.lazer_rooms.childs.LazerFormEditRoom;
import escape_room.escaperoom.rooms.lazer_rooms.childs.LazerInitializationRoom;
import escape_room.escaperoom.rooms.parent_room.Room;

/**
 * Created by Dell on 20.12.2014.
 */
public class RoomHandler {

    public static final int LAZER_ROOM = 0;
    public static final int ADJUSTING_ROOM = 1;
    public static final int FORM_MODIFICATION_ROOM = 2;
    public static final int BLUETOOTH_STATUS_CHANGE = 1;
    public static final int BLUETOOTH_TEST = 2;

    private EscapeRoomActivity escapeRoomActivity;

    private FragmentManager fragmentManager;

    private int mainFrameId;

    public RoomHandler(EscapeRoomActivity escapeRoomActivity,FragmentManager fragmentManager,int mainFrameId){
        this.fragmentManager = fragmentManager;
        this.escapeRoomActivity = escapeRoomActivity;
        this.mainFrameId=mainFrameId;
    }

    List<Room> roomStack = new ArrayList<Room>();

    public void addRoom(Room room){
        roomStack.add(room);
    }
    public void deleteRoom(int indexOfRoom){
        if ((indexOfRoom>=0)&(indexOfRoom<roomStack.size())){
            roomStack.remove(indexOfRoom);
        }
    }

    public void goToDirectRoom(int indexOfRoom){

        if (indexOfRoom<roomStack.size()){
            fragmentManager.executePendingTransactions();
            if (fragmentManager.getBackStackEntryCount()==0){
                fragmentManager.beginTransaction()
                        .add(mainFrameId, roomStack.get(0))
                        .addToBackStack(null)
                        .commit();
            }
            else {
                fragmentManager.beginTransaction()
                        .replace(mainFrameId, roomStack.get(indexOfRoom))
                        .addToBackStack(null)
                        .commit();
            }
        }
        else{
            throw new NullPointerException();
        }

    }

    public void goToPreviousRoom(){

        fragmentManager.executePendingTransactions();
        if (fragmentManager.getBackStackEntryCount()>1){
            fragmentManager.popBackStack();
        }
        else{
            throw new NullPointerException();
        }

    }

    public boolean isAllowedToPopBackStack(){
        if (fragmentManager.getBackStackEntryCount()>1){
            return true;
        }
        else{
            return false;
        }
    }

    public void roomNotification(int reason, Object data){
        for (Room room:roomStack){
            room.notifyStatusChange(reason,data);
        }
    }


    public void roomsInit(){

        LazerInitializationRoom lazerInitializationRoom = new LazerInitializationRoom();
        lazerInitializationRoom.setLayoutId(R.layout.laser_room);
        lazerInitializationRoom.setDataService(escapeRoomActivity.getServices().getDataService());
        lazerInitializationRoom.setCommunicationHandler(escapeRoomActivity.getServices().getCommunicationHandler());
        addRoom(lazerInitializationRoom);

        FormAdjustingRoom formAdjustingRoom = new FormAdjustingRoom();
        formAdjustingRoom.setLayoutId(R.layout.adjusting_room);
        formAdjustingRoom.setDataService(escapeRoomActivity.getServices().getDataService());
        formAdjustingRoom.setCommunicationHandler(escapeRoomActivity.getServices().getCommunicationHandler());
        addRoom(formAdjustingRoom);

        LazerFormEditRoom lazerFormEditRoom = new LazerFormEditRoom();
        lazerFormEditRoom.setLayoutId(R.layout.creating_new_form_room);
        lazerFormEditRoom.setDataService(escapeRoomActivity.getServices().getDataService());
        lazerFormEditRoom.setCommunicationHandler(escapeRoomActivity.getServices().getCommunicationHandler());
        addRoom(lazerFormEditRoom);

        lazerInitializationRoom.setRoomHandler(this);

    }

}
