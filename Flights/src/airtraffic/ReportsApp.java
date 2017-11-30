package airtraffic;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

public final class ReportsApp {
   private static final String METHOD_NAME_PREFIX = "report";
   private static final int METHOD_PARAMETER_COUNT = 1;
   private static final Class<?> METHOD_RETURN_TYPE = Void.TYPE;

   private final Logger logger = LoggerFactory.getLogger(ReportsApp.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();

   public static void main(String[] args) throws Exception {
      new ReportsApp().executeSelectedApp();
   }

   private void executeSelectedApp() throws Exception {
      List<Class<?>> classes = getClasses("airtraffic." + readStyleOption());
      clearScreen();
      int option = getAppOption(classes);
      if(option == 0) {
         System.exit(0);
      }
      Class<?> klass = classes.get(option-1);
      logger.debug("User requested class {}", klass);
      clearScreen();
      executeSelectedReport(klass);
   }

   private List<Class<?>> getClasses(String pkg) throws IOException {
      ClassLoader cl = this.getClass().getClassLoader();
      Set<ClassInfo> infos = ClassPath.from(cl).getTopLevelClasses(pkg);
      List<Class<?>> classes = new ArrayList<>();
      for(ClassInfo info : infos) {
         Class<?> klass = info.load();
         if(!klass.isInterface() && 
             klass.getSuperclass().equals(AbstractReportsProvider.class)) {
            classes.add(klass);
         }
      }
      Collections.sort(classes, (c1, c2) -> c1.getName().compareTo(c2.getName()));
      return classes;
   }

   private String readStyleOption() {
      String format = "%2d  %s\n";
      terminal.println("Style options:\n");
      terminal.printf(format, 0, "Exit program");
      terminal.printf(format, 1, "Iterator-based");
      terminal.printf(format, 2, "Stream-based");
      terminal.println();
      int option = io.newIntInputReader()
                     .withDefaultValue(0)
                     .withMinVal(0)
                     .withMaxVal(2)
                     .read("Style");
      if(option == 0) {
         System.exit(0);
      }
      return option == 1 ? "iterator" : "stream";
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

   private void clearScreen() {
      if(terminal instanceof JLineTextTerminal) {
         try {
            ((JLineTextTerminal) terminal).getReader().clearScreen();
         } catch (IOException e) {
            logger.debug("Unable to clear screen");
         }
      }
   }

   private String getDescription(Class<?> klass) {
      String name = klass.getSimpleName().replace("App", new String());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }

   protected List<Method> getReportMethods(Class<?> klass) {
      return Arrays.stream(klass.getDeclaredMethods())
                   .filter(method -> methodFilter(method))
                   .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                   .collect(toList());
   }

   private boolean methodFilter(Method method) {
      return Modifier.isPublic(method.getModifiers()) &&
            method.getName().startsWith(METHOD_NAME_PREFIX) &&
            method.getParameterTypes().length == METHOD_PARAMETER_COUNT &&
            method.getReturnType().equals(METHOD_RETURN_TYPE);
   }

   private void executeSelectedReport(Class<?> klass) throws Exception {
      List<Method> reportMethods = getReportMethods(klass);
      int option = getReportOption(reportMethods);
      if(option == 0) {
         System.exit(0);
      }
      Method method = reportMethods.get(option-1);
      logger.debug("User requested invocation of method {}", method.getName());
      terminal.println();
      terminal.println(getReportDescription(method));
      terminal.println();
      method.invoke(klass.newInstance(), new Repository());
      terminal.println("\n=== Report complete ===");
   }

   private int getReportOption(List<Method> printMethods) {
      if(printMethods.size() == 0) {
         logger.warn("No report options available for this class");
         return 0;
      }
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println("Report options:\n");
      String format = "%2d  %s\n";
      int n = 0;
      terminal.printf(format, n++, "Exit program");
      for(Method m : printMethods) {
         terminal.printf(format, n++, getReportDescription(m));
         logger.debug("Found report method {}", m.getName());
      }
      terminal.println();
      return io.newIntInputReader()
               .withDefaultValue(0)
               .withMinVal(0)
               .withMaxVal(printMethods.size())
               .read("Option");
   }

   private String getReportDescription(Method method) {
      String name = method.getName().substring(METHOD_NAME_PREFIX.length());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }
}