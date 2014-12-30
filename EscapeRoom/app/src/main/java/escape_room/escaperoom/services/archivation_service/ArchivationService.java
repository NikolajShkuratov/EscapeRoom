package escape_room.escaperoom.services.archivation_service;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Dell on 29.12.2014.
 */
public class ArchivationService {

    public ArchivationService(String externalCacheDirePath,Context context){
        this.externalCacheDirePath = externalCacheDirePath+"/";
        this.context=context;
    }

    private String externalCacheDirePath;
    private Context context;
    public String stringReadFromFile(String fileName){
        String output = null;
        String fullFilePath = externalCacheDirePath + fileName;
        try{
            BufferedReader reader = new BufferedReader( new FileReader(fullFilePath));
            output = reader.readLine();
            reader.close();
        }
        catch (IOException e){

        }
        return output;
    }
    public void stringWriteInFile(String fileName,String data){
        String fullFilePath = externalCacheDirePath + fileName;
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath,false));
            writer.write(data);
            writer.close();
        }
        catch (IOException e){}

    }

    public byte[] byteReadFromFile(String fileName){
        byte input[] = new byte[9];
        try {
            FileInputStream reader = context.openFileInput(fileName);
            reader.read(input);
            reader.close();
        }
        catch (IOException e){ }
        return input;
    }

    public void byteWriteInFile(String fileName,byte data[]){
        try{
            FileOutputStream writer = context.openFileOutput(fileName,context.MODE_PRIVATE);
            writer.write(data);
            writer.close();
        }
        catch (IOException e){  }

    }

}
