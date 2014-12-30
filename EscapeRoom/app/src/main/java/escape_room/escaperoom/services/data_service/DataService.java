package escape_room.escaperoom.services.data_service;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import escape_room.escaperoom.EscapeRoomActivity;
import escape_room.escaperoom.rooms.RoomHandler;
import escape_room.escaperoom.rooms.lazer_rooms.lazer_form.LazerForm;
import escape_room.escaperoom.support.FormModification;

/**
 * Created by Dell on 21.12.2014.
 */
public class DataService {

    private List<LazerForm> formSet = new ArrayList<LazerForm>();

    public List<LazerForm> getFormSet() {
        return formSet;
    }

    EscapeRoomActivity escapeRoomActivity;

    public DataService(EscapeRoomActivity escapeRoomActivity){
        formSet.add(new LazerForm());
        formSet.add(new LazerForm());
        formSet.add(new LazerForm());
        this.escapeRoomActivity = escapeRoomActivity;
    }

    private RoomHandler roomHandler;

    public RoomHandler getRoomHandler() {
        return roomHandler;
    }

    public void setRoomHandler(RoomHandler roomHandler) {
        this.roomHandler = roomHandler;
    }

    FormModification formModification;

    public FormModification startModification(){
        formModification = new FormModification();
        return formModification;
    }

    public void stopModification(){
        formModification=null;
    }

    public FormModification getModification (){
        return formModification;
    }

    public static final String FORM_FILE_NAME = "formDataFile.txt";

    public byte[] getFormsData(){
        byte formsData[] = new byte[9];
        for (int i=0;i<3;i++) {
            for (int j=0;j<3;j++) {
                formsData[i*3+j] = formSet.get(i).getLazerBitSet().getByte(j);
            }
        }
        return formsData;
    }

    public void formsInit(){
        byte temp[] = escapeRoomActivity.getServices().getArchivationService().byteReadFromFile(FORM_FILE_NAME);
        if (temp!=null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    formSet.get(i).getLazerBitSet().setByte(j, temp[i * 3 + j]);
                }
            }
        }
    }

    public void formsSave(){
        escapeRoomActivity.getServices().getArchivationService()
                .byteWriteInFile(FORM_FILE_NAME,getFormsData());
    }

}
