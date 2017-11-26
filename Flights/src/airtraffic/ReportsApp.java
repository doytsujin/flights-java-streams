package airtraffic;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.jline.JLineTextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import airtraffic.stream.AbstractReportsApp;

public final class ReportsApp {
   private final Logger logger = LoggerFactory.getLogger(ReportsApp.class);
   private final ClassLoader cl = this.getClass().getClassLoader();
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();

   public static void main(String[] args) throws Exception {
      new ReportsApp().executeSelectedApp();
   }

   private void executeSelectedApp() throws Exception {
      List<Class<?>> classes = getClasses();
      clearScreen();
      int option = getAppOption(classes);
      if(option == 0) {
         System.exit(0);
      }
      Class<?> klass = classes.get(option-1);
      logger.debug("User requested class {}", klass);
      clearScreen();
      Method method = klass.getMethod("main", String[].class);
      String[] params = null;
      method.invoke(null, (Object) params);
   }

   private List<Class<?>> getClasses() throws IOException {
      Set<ClassInfo> infos = ClassPath.from(cl).getTopLevelClasses("airtraffic.stream");
      List<Class<?>> classes = new ArrayList<>();
      for(ClassInfo info : infos) {
         Class<?> klass = info.load();
         if(klass.getSuperclass().equals(AbstractReportsApp.class)) {
            classes.add(klass);
         }
      }
      Collections.sort(classes, (c1, c2) -> c1.getName().compareTo(c2.getName()));
      return classes;
   }

   private int getAppOption(List<Class<?>> classes) {
      if(classes.size() == 0) {
         logger.warn("No report classes available");
         return 0;
      }
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println("Application options:\n");
      String format = "%2d  %s\n";
      int n = 0;
      terminal.printf(format, n++, "Exit program");
      for(Class<?> c : classes) {
         terminal.printf(format, n++, getDescription(c));
         logger.debug("Found app {}", c.getName());
      }
      terminal.println();
      return io.newIntInputReader()
               .withDefaultValue(0)
               .withMinVal(0)
               .withMaxVal(classes.size())
               .read("Option");
   }

   private String getDescription(Class<?> klass) {
      String name = klass.getSimpleName().replace("App", new String());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }

   private void clearScreen() {
      if(terminal instanceof JLineTextTerminal) {
         try {
            ((JLineTextTerminal) terminal).getReader().clearScreen();
         } catch (IOException e) {
            logger.debug("Unable to clear screen");
         }
      }
   }
}