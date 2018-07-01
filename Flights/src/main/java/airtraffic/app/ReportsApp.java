package airtraffic.app;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.jline.JLineTextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;


/**
 * Class that discovers and makes available all report apps.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public final class ReportsApp {
   private final Logger logger = LoggerFactory.getLogger(ReportsApp.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();

   public static void main(String[] args) throws Exception {
      new ReportsApp().executeSelectedApp();
   }

   private void executeSelectedApp() throws Exception {
      clearScreen();
      List<Class<?>> classes = getClasses("airtraffic.app");
      int option = readAppOption(classes);
      if(option == 0) {
         System.exit(0);
      }
      Class<?> klass = classes.get(option-1);
      logger.debug("User requested class {}", klass);
      clearScreen();
      klass.getMethod("executeSelectedReport")
           .invoke(klass.newInstance());
   }

   private List<Class<?>> getClasses(String pkg) throws IOException {
      ClassLoader cl = this.getClass().getClassLoader();
      return ClassPath.from(cl)
                      .getTopLevelClasses(pkg)
                      .stream()
                      .map(i -> i.load())
                      .filter(c -> !c.isInterface() && 
                                   !isAbstract(c.getModifiers()) && 
                                   AbstractReportsApp.class.isAssignableFrom(c))
                      .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                      .collect(toList());
   }

   private int readAppOption(List<Class<?>> classes) {
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
         terminal.printf(format, n++, getAppDescription(c));
         logger.debug("Found app {}", c.getName());
      }
      terminal.println();
      return io.newIntInputReader()
               .withDefaultValue(0)
               .withMinVal(0)
               .withMaxVal(classes.size())
               .read("Option");
   }

   private String getAppDescription(Class<?> klass) {
      String name = klass.getSimpleName().replace("App", EMPTY);
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