package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.FlightReports;
import airtraffic.reports.ReportException;
import airtraffic.reports.iterator.IteratorFlightReports;
import airtraffic.reports.stream.StreamFlightReports;

public class FlightReportsApp extends AbstractReportsApp implements FlightReports {
   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   @Override
   public ResultSet reportTotalFlightsFromOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"));

      ResultSet rs = getImpl(style).reportTotalFlightsFromOrigin(context);
      try {
         if (rs.next()) {
            context.getTerminal().printf("Total flights from %s is %,d\n", 
                                         rs.getString("Origin"),
                                         rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsToDestination(ReportContext context) {
      context.setYear(readYear())
             .setDestination(readAirport("Destination"));
      ResultSet rs = getImpl(readStyleOption()).reportTotalFlightsToDestination(context);

      try {
         if (rs.next()) {
            context.getTerminal().printf("Total flights to %s is %,d\n",
                                         rs.getString("Destination"), 
                                         rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsFromOriginToDestination(ReportContext context) {
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setDestination(readAirport("Destination"));

      ResultSet rs = getImpl(readStyleOption()).reportTotalFlightsFromOrigin(context);

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

      return rs;
   }

   @Override
   public ResultSet reportTopFlightsByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nOrigin\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportTopFlightsByOrigin(context);
      try {
         while (rs.next()) {
            terminal.printf("%3s\t\t%,10d\n", 
                            rs.getString("Origin"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTopDestinationsFromOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Top destinations from %s\n\n", context.getOrigin().getName());
      terminal.println("Destination\t   Count");
      terminal.println(repeat("-", 30));

      ResultSet rs = getImpl(style).reportTopDestinationsFromOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%,10d\n", 
                            rs.getString("Origin"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostPopularRoutes(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Route\t\t    Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportMostPopularRoutes(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n", 
                            rs.getString("Route"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\tDelay (min)");
      terminal.println(repeat("-", 22));

      ResultSet rs = getImpl(style).reportWorstAverageDepartureDelayByOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%.0f\n", 
                            rs.getString("Origin"), 
                            rs.getFloat("Delay"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Destination\tDelay (min)");
      terminal.println(repeat("-", 28));

      ResultSet rs = getImpl(style).reportWorstAverageArrivalDelayByDestination(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%.0f\n",  
                            rs.getString("Destination"), 
                            rs.getFloat("Delay"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostCancelledFlightsByOrigin(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t  Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportMostCancelledFlightsByOrigin(context);
      try {
         while(rs.next()) {
            terminal.printf("%3s\t\t%,8d\n", 
                            rs.getString("Origin"), 
                            rs.getFloat("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByOriginState(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\t  Count");
      terminal.println(repeat("-", 19));

      ResultSet rs = getImpl(style).reportTotalFlightsByOriginState(context);
      try {
         while(rs.next()) {
            terminal.printf("%2s\t%,10d\n",
                            rs.getString("State"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByDestinationState(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\tCount");
      terminal.println(repeat("-", 19));

      ResultSet rs = getImpl(style).reportTotalFlightsByDestinationState(context);
      try {
         while(rs.next()) {
            terminal.printf("%2s\t%,10d\n",
                            rs.getString("State"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
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

   @Override
   public ResultSet reportLongestFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      ResultSet rs = getImpl(style).reportLongestFlights(context);
      printFlights(terminal, rs);

      return rs;
   }

   @Override
   public ResultSet reportShortestFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      ResultSet rs = getImpl(style).reportShortestFlights(context);
      printFlights(terminal, rs);

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByDistanceRange(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Range\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportTotalFlightsByDistanceRange(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s\t%,10d\n", 
                            rs.getString("Range"), 
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportDaysWithLeastCancellations(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      ResultSet rs = getImpl(style).reportDaysWithLeastCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s       %,3d\n", 
                            rs.getDate("Date"),
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportDaysWithMostCancellations(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      ResultSet rs = getImpl(style).reportDaysWithMostCancellations(context);
      try {
         while(rs.next()) {
            terminal.printf("%-10s       %,3d\n", 
                            rs.getDate("Date"),
                            rs.getInt("TotalCancellations"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalMonthlyFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Month\t\tCount");
      terminal.println(repeat("-", 27));

      ResultSet rs= getImpl(style).reportTotalMonthlyFlights(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n", 
                            rs.getString("YearMonth"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalDailyFlights(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportTotalDailyFlights(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportTotalFlightsByDayOfWeek(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day of Week\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportTotalFlightsByDayOfWeek(context);
      try {
         while(rs.next()) {
            terminal.printf("%10s\t%,10d\n",
                            rs.getString("DayOfWeek"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostFlightsByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportMostFlightsByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportLeastFlightsByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      ResultSet rs = getImpl(style).reportLeastFlightsByDay(context);
      try {
         while(rs.next()) {
            terminal.printf("%s\t%,10d\n",
                            rs.getString("Date"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }

      return rs;
   }

   @Override
   public ResultSet reportMostFlightsByOriginByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      ResultSet rs = getImpl(style).reportMostFlightsByOriginByDay(context);
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

      return rs;
   }

   @Override
   public ResultSet reportMostFlightsByCarrierByDay(ReportContext context) {
      final String style = readStyleOption();
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      ResultSet rs = getImpl(style).reportMostFlightsByCarrierByDay(context);
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

      return rs;
   }

   private FlightReports getImpl(String style) {
      switch(style) {
         case "iterator":
            return new IteratorFlightReports();
         case "stream":
            return new StreamFlightReports();
         default:
            throw new IllegalArgumentException("Unsupported style: " + style);
      }
   }
}
