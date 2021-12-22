import java.nio.charset.StandardCharsets;
/**
 * Utility used for serializing and deserializing data.
 */
public class SerializationStream {
  public byte[] data;
  public int pos = 0;
  private int mark = 0;
  public SerializationStream(){}
  public SerializationStream(int capacity){
    data = new byte[capacity];
  }
  public SerializationStream(byte[] data){
    this.data = data;
  }
  public void reset(){
    pos = mark;
  }
  public void mark(){
    mark = pos;
  }
  public boolean end(){
    return pos>=data.length;
  }
  public void write(byte b){
    data[pos++] = b;
  }
  public void write(boolean b){
    write((byte)(b?1:0));
  }
  public void write(int x){
    for (int i=pos+3;;){
      data[i] = (byte)x;
      if (--i<pos){
        break;
      }else{
        x>>=8;
      }
    }
    pos+=4;
  }
  public void write(long x){
    for (int i=pos+7;;){
      data[i] = (byte)x;
      if (--i<pos){
        break;
      }else{
        x>>=8;
      }
    }
    pos+=8;
  }
  public void write(double x){
    write(Double.doubleToRawLongBits(x));
  }
  public void write(byte[] arr){
    write(arr,0,arr.length);
  }
  public void write(byte[] arr, int offset, int length){
    write(length);
    System.arraycopy(arr,offset,data,pos,length);
    pos+=length;
  }
  public void write(String str){
    write(str.getBytes(StandardCharsets.UTF_8));
  }
  public byte readByte(){
    return data[pos++];
  }
  public boolean readBoolean(){
    return readByte()!=0;
  }
  public int readInt(){
    int x = 0;
    int i = pos;
    pos+=4;
    while (true){
      x|=(int)data[i]&0xFF;
      if (++i==pos){
        break;
      }else{
        x<<=8;
      }
    }
    return x;
  }
  public long readLong(){
    long x = 0;
    int i = pos;
    pos+=8;
    while (true){
      x|=(long)data[i]&0xFF;
      if (++i==pos){
        break;
      }else{
        x<<=8;
      }
    }
    return x;
  }
  public double readDouble(){
    return Double.longBitsToDouble(readLong());
  }
  public byte[] readBytes(){
    int len = readInt();
    byte[] arr = new byte[len];
    System.arraycopy(data,pos,arr,0,len);
    pos+=len;
    return arr;
  }
  public int readBytes(byte[] arr, int offset){
    int len = readInt();
    System.arraycopy(data,pos,arr,offset,len);
    pos+=len;
    return len;
  }
  public String readString(){
    return new String(readBytes(), StandardCharsets.UTF_8);
  }
}