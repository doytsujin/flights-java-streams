package airtraffic;

import static org.apache.commons.lang3.StringUtils.repeat;

import org.beryx.textio.TextTerminal;

import airtraffic.iterator.IteratorPlaneReports;
import airtraffic.stream.StreamPlaneReports;

public class PlaneReportsApp extends AbstractReportsApp implements PlaneReports {
   public static void main(String[] args) throws Exception {
      new PlaneReportsApp().executeSelectedReport();
   }

   @Override
   public void reportTotalPlanesByManfacturer(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tCount");
      terminal.println("---------------------------------------");

      getImpl(style).reportTotalPlanesByManfacturer(context);
   }

   @Override
   public void reportTotalPlanesByYear(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Year\tCount");
      terminal.println("------------------");

      getImpl(style).reportTotalPlanesByYear(context);
   }

   @Override
   public void reportTotalPlanesByAircraftType(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("---------------------------------------");

      getImpl(style).reportTotalPlanesByAircraftType(context);
   }

   @Override
   public void reportTotalPlanesByEngineType(ReportContext context) {
      final String style = readStyleOption();

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("---------------------------------------");

      getImpl(style).reportTotalPlanesByEngineType(context);
   }

   @Override
   public void reportPlanesWithMostCancellations(ReportContext context) {
      final String style = readStyleOption();

      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t\tCount");
      terminal.println("-----------------------");

      getImpl(style).reportPlanesWithMostCancellations(context);
   }

   @Override
   public void reportMostFlightsByPlane(ReportContext context) {
      final String style = readStyleOption();

      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Tail #\t  Manufacturer\t\tModel #\t\tCount");
      terminal.println(repeat("-", 67));

      getImpl(style).reportMostFlightsByPlane(context);
   }

   @Override
   public void reportMostFlightsByPlaneModel(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
      terminal.println(repeat("-", 82));

      getImpl(style).reportMostFlightsByPlaneModel(context);
   }

   @Override
   public void reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Manufacturer\t\t\t Count");
      terminal.println("-------------------------------------------");

      getImpl(style).reportTotalFlightsByPlaneManufacturer(context);
   }

   @Override
   public void reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Age Range\tCount");
      terminal.println(repeat("-", 27));

      getImpl(style).reportTotalFlightsByPlaneAgeRange(context);
   }

   @Override
   public void reportTotalFlightsByAircraftType(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Aircraft Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      getImpl(style).reportTotalFlightsByAircraftType(context);
   }

   @Override
   public void reportTotalFlightsByEngineType(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Engine Type\t\t\tCount");
      terminal.println("-------------------------------------------");

      getImpl(style).reportTotalFlightsByEngineType(context);
   }

   private PlaneReports getImpl(String style) {
      return "iterator".equals(style) ? 
         new IteratorPlaneReports() : 
         new StreamPlaneReports();
   }
}