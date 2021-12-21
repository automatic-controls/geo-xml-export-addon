import java.util.*;
import java.util.regex.Pattern;
import com.controlj.green.addonsupport.access.*;
/**
 * Tree node used to store and process data harvested from the geographic tree.
 * {@code Area} nodes may contain other areas and equipment.
 */
public class Area implements Comparable<Area> {
  /** Display name of this location. */
  private String name;
  /** Reference name of this location. */
  private String ref;
  /** Controls tab width of generated XML encodings. */
  private int depth;
  /** List of areas under this location. */
  private ArrayList<Area> areas = new ArrayList<Area>();
  /** List of equipment under this location. */
  private ArrayList<Equipment> equipment = new ArrayList<Equipment>();
  /**
   * Populates this object with data from {@code loc}.
   * @param loc is the {@code Location} from which to harvest data.
   * @param depth controls the tab width of generated XML encodings.
   */
  public void populate(Location loc, int depth){
    name = loc.getDisplayName();
    ref = loc.getReferenceName();
    this.depth = depth;
    LocationType t;
    Area a;
    for (Location l:loc.getChildren()){
      t = l.getType();
      if (t==LocationType.Area){
        a = new Area();
        a.populate(l,depth+1);
        areas.add(a);
      }else if (t==LocationType.Equipment){
        equipment.add(new Equipment(l, depth+1));
      }
    }
  }
  /**
   * Recursively applies a regular expression to all display names.
   * @param p is the regular expression.
   * @param replace is used to replace all matches.
   */
  public void regex(Pattern p, String replace, boolean replaceAreas){
    if (replaceAreas){
      name = p.matcher(name).replaceAll(replace);
    }
    for (Area a:areas){
      a.regex(p,replace,replaceAreas);
    }
    for (Equipment e:equipment){
      e.regex(p,replace);
    }
  }
  /**
   * Recursively sorts all areas and equipments.
   */
  public void sort(){
    areas.sort(null);
    equipment.sort(null);
    for (Area a:areas){
      a.sort();
    }
  }
  /**
   * Encodes this object as an XML element and appends the encoded data to {@code sb}.
   * @param sb is the {@code StringBuilder} where the encoded data should be appeneded.
   */
  public void generateXML(StringBuilder sb){
    boolean b = areas.size()>0 || equipment.size()>0;
    String tab = Utilities.tab(depth);
    sb.append(tab+"<AREA name=\""+Utilities.escape(name)+"\" ref=\""+Utilities.escape(ref)+"\">");
    if (b){
      sb.append('\n');
    }
    for (Area a:areas){
      a.generateXML(sb);
    }
    for (Equipment e:equipment){
      e.generateXML(sb);
    }
    if (b){
      sb.append(tab);
    }
    sb.append("</AREA>\n");
  }
  /**
   * Encodes this object as an HTML element and appends the encoded data to {@code sb}.
   * @param sb is the {@code StringBuilder} where the encoded data should be appeneded.
   */
  public void generateHTML(StringBuilder sb){
    sb.append(Utilities.escape(name)+"<br>\n");
    sb.append("<div style=\"margin-left:1em\">\n");
    for (Area a:areas){
      a.generateHTML(sb);
    }
    for (Equipment e:equipment){
      e.generateHTML(sb);
    }
    sb.append("</div>");
  }
  /**
   * Compares display names for sorting purposes.
   */
  @Override public int compareTo(Area a){
    return Utilities.compare(name,a.name);
  }
}