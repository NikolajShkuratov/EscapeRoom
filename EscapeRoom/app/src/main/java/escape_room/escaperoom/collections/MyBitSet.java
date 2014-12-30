package escape_room.escaperoom.collections;

/**
 * Created by Dell on 20.12.2014.
 */
public class MyBitSet {

    public static final int LAZER_SET_BYTE_SIZE = 3;

    private byte set[];

    public MyBitSet(int byteLength){
        set = new byte[byteLength];
    }

    public void setBitSet(MyBitSet myBitSet){
        for (int i=0;i<set.length;i++){
            set[i]=myBitSet.getByte(i);
        }
    }

    public void clearBitSet(){
        for (int i=0;i<set.length;i++){
            set[i]=0;
        }
    }

    public void setBit(int index){

        set[index/8] |= 1<<(index%8);

    }

    public void clearBit(int index){

        set[index/8] &= ~(1<<index%8);

    }

    public boolean getBit(int index){

        //if ((lazerSet[index/8]&((~(1<<index%8))+1))!=0){
        if ((set[index/8]&(1<<index%8))!=0){
            return true;
        }
        else{
            return false;
        }

    }

    public byte getByte(int indexOfByte){

        return set[indexOfByte];

    }

    public void setByte(int indexOfByte, byte dataByte){
        set[indexOfByte] = dataByte;
    }

    public byte[] getBytes(){
        return set;
    }
}
