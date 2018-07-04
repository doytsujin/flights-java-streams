package airtraffic.app;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.beryx.textio.TextTerminal;
import airtraffic.ReportContext;
import airtraffic.reports.FlightReports;
import airtraffic.reports.ReportException;


/**
 * Provides methods for executing each of the flight reports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class FlightReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   public void reportTotalFlightsFromOrigin(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"));

      try (ResultSet rs = impl.reportTotalFlightsFromOrigin(context)) {
         if (rs.next()) {
            context.getTerminal()
                   .printf("Total flights from %s is %,d\n", 
                           rs.getString("Origin"),
                           rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsToDestination(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setDestination(readAirport("Destination"));
      try (ResultSet rs = impl.reportTotalFlightsToDestination(context)) {
         if (rs.next()) {
            context.getTerminal()
                   .printf("Total flights to %s is %,d\n",
                           rs.getString("Destination"), 
                           rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsFromOriginToDestination(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setDestination(readAirport("Destination"));

      try (ResultSet rs = impl.reportTotalFlightsFromOriginToDestination(context)) {
         if (rs.next()) {
            context.getTerminal()
                   .printf("Total of %,d flights from %s (%s)\nto %s (%s)\n", 
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("\nOrigin\t\tCount");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportTopFlightsByOrigin(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setOrigin(readAirport("Origin"))
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.printf("Top destinations from %s\n\n", context.getOrigin().getName());
      terminal.println("Destination\t   Count");
      terminal.println(repeat("-", 30));

      try (ResultSet rs = impl.reportTopDestinationsFromOrigin(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Route\t\t    Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportMostPopularRoutes(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\tDelay (min)");
      terminal.println(repeat("-", 22));

      try (ResultSet rs = impl.reportWorstAverageDepartureDelayByOrigin(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Destination\tDelay (min)");
      terminal.println(repeat("-", 28));

      try (ResultSet rs = impl.reportWorstAverageArrivalDelayByDestination(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t  Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportMostCancelledFlightsByOrigin(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\t  Count");
      terminal.println(repeat("-", 19));

      try (ResultSet rs = impl.reportTotalFlightsByOriginState(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("State\tCount");
      terminal.println(repeat("-", 19));

      try (ResultSet rs = impl.reportTotalFlightsByDestinationState(context)) {
         while(rs.next()) {
            terminal.printf("%2s\t%,10d\n",
                            rs.getString("State"),
                            rs.getInt("TotalFlights"));
         }
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   private void printFlights(TextTerminal<?> terminal, ResultSet rs) 
      throws SQLException {
      if (rs.next()) {
         terminal.printf("%-8s  %10s\t  %2s\t %3s\t    %3s\t\t%6d\n",
                         rs.getString("FlightNumber"),
                         rs.getDate("Date"),
                         rs.getString("Carrier"),
                         rs.getString("Origin"),
                         rs.getString("Destination"),
                         rs.getInt("Distance"));
      }
   }

   public void reportLongestFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      try (ResultSet rs = impl.reportLongestFlights(context)) {
         printFlights(terminal, rs);
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportShortestFlights(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      terminal.println(repeat("-", 65));

      try (ResultSet rs = impl.reportShortestFlights(context)) {
         printFlights(terminal, rs);
      } catch (SQLException e) {
         throw new ReportException(e);
      }
   }

   public void reportTotalFlightsByDistanceRange(ReportContext context) {
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Range\t\tCount");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportTotalFlightsByDistanceRange(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      try (ResultSet rs = impl.reportDaysWithLeastCancellations(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Date\t\tCount");
      terminal.println(repeat("-", 24));

      try (ResultSet rs = impl.reportDaysWithMostCancellations(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Month\t\tCount");
      terminal.println(repeat("-", 27));

      try (ResultSet rs= impl.reportTotalMonthlyFlights(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportTotalDailyFlights(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear());

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day of Week\t   Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportTotalFlightsByDayOfWeek(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportMostFlightsByDay(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Day\t\t   Count");
      terminal.println(repeat("-", 27));

      try (ResultSet rs = impl.reportLeastFlightsByDay(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Origin\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      try (ResultSet rs = impl.reportMostFlightsByOriginByDay(context)) {
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
      FlightReports impl = getBean(FlightReports.class, readStyle());
      context.setYear(readYear())
             .setLimit(readLimit(10, 1, 100));

      TextTerminal<?> terminal = context.getTerminal();
      terminal.println("Carrier\t\t\t\tDate\t\t     Count");
      terminal.println(repeat("-", 59));

      try (ResultSet rs = impl.reportMostFlightsByCarrierByDay(context)) {
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