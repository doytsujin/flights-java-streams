package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.FlightReports;
import airtraffic.reports.ReportException;

public class FlightReportsApp extends AbstractReportsApp<FlightReports> {
   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   public void reportTotalFlightsFromOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"));

      ResultSet rs = impl.reportTotalFlightsFromOrigin(context);
      try {
         if (rs.next()) {
            context.getTerminal().printf("Total flights from %s is %,d\n", 
                                         rs.getString("Origin"),
                                         rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsToDestination(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setDestination(readAirport("Destination"));
      ResultSet rs = impl.reportTotalFlightsToDestination(context);
      try {
         if (rs.next()) {
            context.getTerminal().printf("Total flights to %s is %,d\n",
                                         rs.getString("Destination"), 
                                         rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsFromOriginToDestination(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setDestination(readAirport("Destination"));

      ResultSet rs = impl.reportTotalFlightsFromOrigin(context);
      try {
         if (rs.next()) {
            context.getTerminal().printf("Total of %,d flights from %s (%s)\nto %s (%s)\n", 
                                         rs.getInt("TotalFlights"),
                                         rs.getString("OriginName"), 
                                         rs.getString("OriginIATA"), 
                                         rs.getString("DestinationName"), 
                                         rs.getString("DestinationIATA"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTopFlightsByOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nOrigin\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportTopFlightsByOrigin(context);
      try {
         while (rs.next()) {
            terminal.printf("%3s\t\t%,10d\n", 
                            rs.getString("Origin"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTopDestinationsFromOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Top destinations from %s\n\n", context.getOrigin().getName());
      terminal.println("Destination\t   Count");
      terminal.println(repeat("-", 30));

      ResultSet rs = impl.reportTopDestinationsFromOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%,10d\n", 
                            rs.getString("Origin"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostPopularRoutes(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Route\t\t    Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportMostPopularRoutes(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n", 
                            rs.getString("Route"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\tDelay (min)");
      terminal.println(repeat("-", 22));

      ResultSet rs = impl.reportWorstAverageDepartureDelayByOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%.0f\n", 
                            rs.getString("Origin"), 
                            rs.getFloat("Delay"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Destination\tDelay (min)");
      terminal.println(repeat("-", 28));

      ResultSet rs = impl.reportWorstAverageArrivalDelayByDestination(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%.0f\n",  
                            rs.getString("Destination"), 
                            rs.getFloat("Delay"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostCancelledFlightsByOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t  Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportMostCancelledFlightsByOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%,8d\n", 
                            rs.getString("Origin"), 
                            rs.getFloat("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByOriginState(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\t  Count");
      terminal.println(repeat("-", 19));

      ResultSet rs = impl.reportTotalFlightsByOriginState(context);
      try {
         while(rs.next()) {
            terminal.printf("%2s\t%,10d\n",
                            rs.getString("State"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByDestinationState(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\tCount");
      terminal.println(repeat("-", 19));

      ResultSet rs = impl.reportTotalFlightsByDestinationState(context);
      try {
         while(rs.next()) {
            terminal.printf("%2s\t%,10d\n",
                            rs.getString("State"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   private void printFlights(TextTerminal<?> terminal, ResultSet rs) {
      try {
         if (rs.next()) {
            terminal.printf("%-8s  %10s\t  %2s\t %3s\t    %3s\t\t%6d\n",
                            rs.getString("FlightNumber"),
                            rs.getDate("Date"),
                            rs.getString("Carrier"),
                            rs.getString("Origin"),
                            rs.getString("Destination"),
                            rs.getInt("Distance"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportLongestFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      ResultSet rs = impl.reportLongestFlights(context);
      printFlights(terminal, rs);
   }

   public void reportShortestFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      ResultSet rs = impl.reportShortestFlights(context);
      printFlights(terminal, rs);
   }

   public void reportTotalFlightsByDistanceRange(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Range\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportTotalFlightsByDistanceRange(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s\t%,10d\n", 
                            rs.getString("Range"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportDaysWithLeastCancellations(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      ResultSet rs = impl.reportDaysWithLeastCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s       %,3d\n", 
                            rs.getDate("Date"),
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportDaysWithMostCancellations(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      ResultSet rs = impl.reportDaysWithMostCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s       %,3d\n", 
                            rs.getDate("Date"),
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalMonthlyFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Month\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs= impl.reportTotalMonthlyFlights(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n", 
                            rs.getString("YearMonth"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalDailyFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportTotalDailyFlights(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByDayOfWeek(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day of Week\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportTotalFlightsByDayOfWeek(context);
      try {
         while(rs.next()) {
            terminal.printf("%10s\t%,10d\n",
                            rs.getString("DayOfWeek"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostFlightsByDay(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportMostFlightsByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportLeastFlightsByDay(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = impl.reportLeastFlightsByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostFlightsByOriginByDay(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      ResultSet rs = impl.reportMostFlightsByOriginByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%-30s\t%s\t%,10d\n",
                            left(rs.getString("Origin"), 30),
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportMostFlightsByCarrierByDay(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, getStyleAnnotation());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      ResultSet rs = impl.reportMostFlightsByCarrierByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%-30s\t%s\t%,10d\n",
                            left(rs.getString("Carrier"), 30),
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }
}