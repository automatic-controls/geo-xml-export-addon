/*
  BSD 3-Clause License
  Copyright (c) 2022, Automatic Controls Equipment Systems, Inc.
  Contributors: Cameron Vogt (@cvogt729)
*/
import java.nio.charset.StandardCharsets;
public class Regex {
  public byte[] nameBytes;
  public byte[] findBytes = null;
  public byte[] replaceBytes = null;
  public String name = null;
  public String find = null;
  public String replace = null;
  public boolean caseSensitive;
  public boolean equipOnly;
  public Regex(byte[] nameBytes, byte[] findBytes, byte[] replaceBytes, boolean caseSensitive, boolean equipOnly){
    this.nameBytes = nameBytes;
    this.findBytes = findBytes;
    this.replaceBytes = replaceBytes;
    name = new String(nameBytes, StandardCharsets.UTF_8);
    find = new String(findBytes, StandardCharsets.UTF_8);
    replace = new String(replaceBytes, StandardCharsets.UTF_8);
    this.caseSensitive = caseSensitive;
    this.equipOnly = equipOnly;
  }
  public Regex(String name, String find, String replace, boolean caseSensitive, boolean equipOnly){
    this.name = name;
    this.find = find;
    this.replace = replace;
    nameBytes = name.getBytes(StandardCharsets.UTF_8);
    findBytes = find.getBytes(StandardCharsets.UTF_8);
    replaceBytes = replace.getBytes(StandardCharsets.UTF_8);
    this.caseSensitive = caseSensitive;
    this.equipOnly = equipOnly;
  }
}