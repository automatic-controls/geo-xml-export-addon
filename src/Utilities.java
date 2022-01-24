/*
  BSD 3-Clause License
  Copyright (c) 2022, Automatic Controls Equipment Systems, Inc.
  Contributors: Cameron Vogt (@cvogt729)
*/
import java.util.Arrays;
/**
 * Contains various utility methods used elsewhere in the application.
 */
public class Utilities {
  /**
   * Escapes a {@code String} for usage in XML or HTML attribute values.
   * @param str is the {@code String} to escape.
   * @return the escaped {@code String}.
   */
  public static String escape(String str){
    int len = str.length();
    StringBuilder sb = new StringBuilder(len+16);
    char c;
    int j;
    for (int i=0;i<len;++i){
      c = str.charAt(i);
      j = c;
      if (j>=32 && j<127){
        switch (c){
          case '&':{
            sb.append("&amp;");
            break;
          }
          case '"':{
            sb.append("&quot;");
            break;
          }
          case '\'':{
            sb.append("&apos;");
            break;
          }
          case '<':{
            sb.append("&lt;");
            break;
          }
          case '>':{
            sb.append("&gt;");
            break;
          }
          default:{
            sb.append(c);
          }
        }
      }else if (j<1114111 && (j<=55296 || j>57343)){
        sb.append("&#").append(Integer.toString(j)).append(";");
      }
    }
    return sb.toString();
  }
  /**
   * Escapes backslashes, single quotes, and double quotes.
   */
  public static String escapeJS(String str){
    int len = str.length();
    StringBuilder sb = new StringBuilder(len+16);
    char c;
    for (int i=0;i<len;++i){
      c = str.charAt(i);
      if (c=='\\' || c=='\'' || c=='"'){
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }
  /**
   * @param depth specifies the length of the generated string.
   * @return a {@code String} of length {@code depth} filled with spaces.
   */
  public static String tab(int depth){
    char[] arr = new char[depth<<1];
    Arrays.fill(arr,' ');
    return new String(arr);
  }
  /**
   * Custom string comparison algorithm used for sorting.
   */
  public static int compare(String a, String b){
    int x,i;
    char c;
    int alen = a.length();
    for (i=alen-1;i>=0;--i){
      c = a.charAt(i);
      if (c<'0' || c>'9'){
        break;
      }
    }
    String aint = a.substring(++i);
    a = a.substring(0,i);
    alen = i;
    int blen = b.length();
    for (i=blen-1;i>=0;--i){
      c = b.charAt(i);
      if (c<'0' || c>'9'){
        break;
      }
    }
    String bint = b.substring(++i);
    b = b.substring(0,i);
    blen = i;
    int len = Math.min(alen,blen);
    for (i=0;i<len;++i){
      x = a.charAt(i)-b.charAt(i);
      if (x!=0){
        return x;
      }
    }
    if (aint.length()>0 && bint.length()>0){
      try{
        return Integer.parseInt(aint)-Integer.parseInt(bint);
      }catch(Exception e){}
    }
    return alen-blen;
  }
}