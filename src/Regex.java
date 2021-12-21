import java.nio.charset.StandardCharsets;
public class Regex {
  public byte[] findBytes = null;
  public byte[] replaceBytes = null;
  public String find = null;
  public String replace = null;
  public Regex(byte[] findBytes, byte[] replaceBytes){
    this.findBytes = findBytes;
    this.replaceBytes = replaceBytes;
    find = new String(findBytes, StandardCharsets.UTF_8);
    replace = new String(replaceBytes, StandardCharsets.UTF_8);
  }
  public Regex(String find, String replace){
    this.find = find;
    this.replace = replace;
    findBytes = find.getBytes(StandardCharsets.UTF_8);
    replaceBytes = replace.getBytes(StandardCharsets.UTF_8);
  }
}