/*
  BSD 3-Clause License
  Copyright (c) 2022, Automatic Controls Equipment Systems, Inc.
  Contributors: Cameron Vogt (@cvogt729)
*/
import java.util.regex.Pattern;
import com.controlj.green.addonsupport.access.Location;
/**
 * Tree node used to store and process data harvested from the geographic tree.
 * {@code Equipment} objects are leaf nodes in the tree (they possess no children).
 */
public class Equipment implements Comparable<Equipment> {
  /** Display name of this location. */
  private String name;
  /** Reference name of this location. */
  private String ref;
  /** Controls tab width of generated XML encodings. */
  private int depth;
  /**
   * Populates a new {@code Equipment} object with data from {@code l}.
   * @param l is the {@code Location} from which to harvest data.
   * @param depth controls the tab width of generated XML encodings.
   */
  public Equipment(Location l, int depth){
    name = l.getDisplayName();
    ref = l.getReferenceName();
    this.depth = depth;
  }
  /**
   * Encodes this object as an XML element and appends the encoded data to {@code sb}.
   * @param sb is the {@code StringBuilder} where the encoded data should be appeneded.
   */
  public void generateXML(StringBuilder sb){
    sb.append(Utilities.tab(depth)+"<EQUIPMENT name=\""+Utilities.escape(name)+"\" ref=\""+Utilities.escape(ref)+"\"></EQUIPMENT>\n");
  }
  /**
   * Encodes this object as an HTML element and appends the encoded data to {@code sb}.
   * @param sb is the {@code StringBuilder} where the encoded data should be appeneded.
   */
  public void generateHTML(StringBuilder sb){
    sb.append(Utilities.escape(name)+"<br>\n");
  }
  /**
   * Applies a regular expression to the display name.
   * @param p is the regular expression.
   * @param replace is used to replace all matches.
   */
  public void regex(Pattern p, String replace){
    name = p.matcher(name).replaceAll(replace);
  }
  /**
   * Compares display names for sorting purposes.
   */
  @Override public int compareTo(Equipment e){
    return Utilities.compare(name,e.name);
  }
}