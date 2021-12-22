import java.nio.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.controlj.green.addonsupport.*;
import com.controlj.green.addonsupport.access.*;
/**
 * Primary servlet which controls the life-cycle of this add-on.
 */
public class MainServlet extends HttpServlet {
  /** Data obtained from index.html */
  private volatile String html = null;
  /** Used for logging. */
  private volatile FileLogger logger = null;
  /** Path to configuration file. */
  private volatile Path config = null;
  /** Controls access to the configuration file. */
  private final AtomicBoolean configLock = new AtomicBoolean();
  /** A list of regular experssions. */
  private volatile ArrayList<Regex> regexArr = new ArrayList<Regex>();
  /** Controls access to {@code regexArr}. */
  private final ReentrantReadWriteLock regexLock = new ReentrantReadWriteLock();
  @Override public void init() throws ServletException {
    try{
      AddOnInfo info = AddOnInfo.getAddOnInfo();
      StringBuilder sb = new StringBuilder(1024);
      try (
        BufferedReader in = new BufferedReader(new InputStreamReader(MainServlet.class.getClassLoader().getResourceAsStream("index.html"), StandardCharsets.UTF_8));
      ){
        String str;
        while ((str = in.readLine())!=null){
          sb.append(str.stripLeading()).append('\n');
        }
      }
      html = sb.toString().replace("__name__", info.getName());
      sb = null;
      config = info.getPrivateDir().toPath().resolve("config.dat");
      logger = info.getDateStampLogger();
    }catch(Exception e){
      throw new ServletException(e);
    }
  }
  private void loadConfig(){
    if (configLock.compareAndSet(false, true)){
      try{
        if (Files.exists(config)){
          SerializationStream s = new SerializationStream(Files.readAllBytes(config));
          regexLock.writeLock().lock();
          try {
            int size = s.readInt();
            regexArr = new ArrayList<Regex>(size+8);
            for (int i=0;i<size;++i){
              regexArr.add(new Regex(s.readBytes(), s.readBytes(), s.readBytes(), s.readBoolean(), s.readBoolean()));
            }
          } finally {
            regexLock.writeLock().unlock();
          }
        }
      }catch(Exception e){
        logger.println(e);
      }
      configLock.set(false);
    }
  }
  private void saveConfig(){
    if (configLock.compareAndSet(false, true)){
      try{
        SerializationStream s;
        regexLock.readLock().lock();
        try {
          int size = regexArr.size();
          int len = 4+14*size;
          for (Regex r:regexArr){
            len+=r.findBytes.length+r.replaceBytes.length+r.nameBytes.length;
          }
          s = new SerializationStream(len);
          s.write(size);
          for (Regex r:regexArr){
            s.write(r.nameBytes);
            s.write(r.findBytes);
            s.write(r.replaceBytes);
            s.write(r.caseSensitive);
            s.write(r.equipOnly);
          }
          if (!s.end()){
            logger.println("Serialization length computation occurred incorrectly.");
          }
        } finally {
          regexLock.readLock().unlock();
        }
        ByteBuffer buf = ByteBuffer.wrap(s.data);
        try (
          FileChannel ch = FileChannel.open(config, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        ){
          while (buf.hasRemaining()){
            ch.write(buf);
          }
        }
      }catch(Exception e){
        logger.println(e);
      }
      configLock.set(false);
    }
  }
  private void add(Regex r){
    regexLock.writeLock().lock();
    try{
      int i = get(r.name);
      if (i==-1){
        regexArr.add(r);
      }else{
        regexArr.set(i, r);
      }
    }finally{
      regexLock.writeLock().unlock();
    }
  }
  private void delete(String name){
    regexLock.writeLock().lock();
    try{
      int i = get(name);
      if (i!=-1){
        regexArr.remove(i);
      }
    }finally{
      regexLock.writeLock().unlock();
    }
  }
  private int get(String name){
    int size = regexArr.size();
    for (int i=0;i<size;++i){
      if (regexArr.get(i).name.equals(name)){
        return i;
      }
    }
    return -1;
  }
  @Override protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
    doPost(req, res);
  }
  @Override protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
    try{
      req.setCharacterEncoding("UTF-8");
      final String type = req.getParameter("type");
      if (type==null){
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        out.print(html);
        out.flush();
      }else if (type.equals("save")){
        final String name = req.getParameter("name");
        final String find = req.getParameter("find");
        if (find==null){
          loadConfig();
          delete(name);
          saveConfig();
        }else{
          try{
            Pattern.compile(find);
          }catch(Exception e){
            res.setStatus(400);
            PrintWriter out = res.getWriter();
            res.setContentType("text/plain");
            res.setCharacterEncoding("UTF-8");
            out.println("Regex Compilation Error:");
            out.print(e.getMessage());
            out.flush();
            return;
          }
          Regex r = new Regex(name, find, req.getParameter("replace"), req.getParameter("insensitive")==null, req.getParameter("areas")==null);
          loadConfig();
          add(r);
          saveConfig();
        }
      }else if (type.equals("refresh")){
        loadConfig();
        final StringBuilder sb = new StringBuilder(128);
        regexLock.readLock().lock();
        try{
          for (Regex r:regexArr){
            sb.append("<button class=\"option\" onclick=\"setRegex("+Utilities.escape('"'+Utilities.escapeJS(r.name)+"\",\""+Utilities.escapeJS(r.find)+"\",\""+Utilities.escapeJS(r.replace)+"\","+r.caseSensitive+','+r.equipOnly)+")\">"+r.name+"</button>");
          }
        }finally{
          regexLock.readLock().unlock();
        }
        PrintWriter out = res.getWriter();
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        out.print(sb.toString());
        out.flush();
      }else{
        final StringBuilder sb = new StringBuilder(256);
        final String find = req.getParameter("find");
        final String replace = req.getParameter("replace");
        final boolean includeAreas = req.getParameter("areas")!=null;
        final boolean caseInsensitive = req.getParameter("insensitive")!=null;
        Pattern p = null;
        if (find!=null && replace!=null){
          try{
            p = caseInsensitive?Pattern.compile(find, Pattern.CASE_INSENSITIVE):Pattern.compile(find);
          }catch(Exception e){
            res.setStatus(400);
            PrintWriter out = res.getWriter();
            res.setContentType("text/plain");
            res.setCharacterEncoding("UTF-8");
            out.println("Regex Compilation Error:");
            out.print(e.getMessage());
            out.flush();
            return;
          }
        }
        final Area a = new Area();
        DirectAccess.getDirectAccess().getRootSystemConnection().runReadAction(FieldAccessFactory.newDisabledFieldAccess(), new ReadAction(){
          public void execute(SystemAccess sys){
            a.populate(sys.getGeoRoot(), 1);
          }
        });
        if (p!=null){
          a.regex(p, replace, includeAreas);
        }
        a.sort();
        if (type.equalsIgnoreCase("download")){
          sb.append("<DATA>\n");
          a.generateXML(sb);
          sb.append("</DATA>");
          PrintWriter out = res.getWriter();
          res.setContentType("application/octet-stream");
          res.setHeader("Content-Disposition","attachment;filename=\"GeoData.xml\"");
          out.print(sb.toString());
          out.flush();
        }else{
          a.generateHTML(sb);
          PrintWriter out = res.getWriter();
          res.setContentType("text/plain");
          res.setCharacterEncoding("UTF-8");
          out.print(sb.toString());
          out.flush();
        }
      }
    }catch(Exception e){
      logger.println(e);
      res.sendError(500, e.getClass().getSimpleName());
    }
  }
}