package airtraffic.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorFlightReports;
import airtraffic.reports.FlightReports;
import airtraffic.stream.StreamFlightReports;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class FlightReportsBenchmark extends AbstractReportsBenchmark {
   private final FlightReports iteratorImpl = new IteratorFlightReports();
   private final FlightReports streamImpl = new StreamFlightReports();

   @Benchmark
   public void iteratorTotalFlightsFromOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH");
      iteratorImpl.reportTotalFlightsFromOrigin(context);
   }

   @Benchmark
   public void streamTotalFlightsFromOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH");
      streamImpl.reportTotalFlightsFromOrigin(context);
   }

   @Benchmark
   public void iteratorTotalFlightsToDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setDestinationByIATA("IAH");
      iteratorImpl.reportTotalFlightsToDestination(context);
   }

   @Benchmark
   public void streamTotalFlightsToDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setDestinationByIATA("IAH");
      streamImpl.reportTotalFlightsToDestination(context);
   }

   @Benchmark
   public void iteratorTotalFlightsFromOriginToDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH")
                                                   .setDestinationByIATA("DFW");
      iteratorImpl.reportTotalFlightsFromOriginToDestination(context);
   }

   @Benchmark
   public void streamTotalFlightsFromOriginToDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH")
                                                   .setDestinationByIATA("DFW");
      streamImpl.reportTotalFlightsFromOriginToDestination(context);
   }

   @Benchmark
   public void iteratorTopFlightsByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportTopFlightsByOrigin(context);
   }

   @Benchmark
   public void streamTopFlightsByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportTopFlightsByOrigin(context);
   }

   @Benchmark
   public void iteratorTopDestinationsFromOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH")
                                                   .setLimit(10);
      iteratorImpl.reportTopDestinationsFromOrigin(context);
   }

   @Benchmark
   public void streamTopDestinationsFromOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setOriginByIATA("IAH")
                                                   .setLimit(10);
      streamImpl.reportTopDestinationsFromOrigin(context);
   }

   @Benchmark
   public void iteratorMostPopularRoutes() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostPopularRoutes(context);
   }

   @Benchmark
   public void streamMostPopularRoutes() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostPopularRoutes(context);
   }

   @Benchmark
   public void iteratorWorstAverageDepartureDelayByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportWorstAverageDepartureDelayByOrigin(context);
   }

   @Benchmark
   public void streamWorstAverageDepartureDelayByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportWorstAverageDepartureDelayByOrigin(context);
   }

   @Benchmark
   public void iteratorWorstAverageArrivalDelayByDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportWorstAverageArrivalDelayByDestination(context);
   }

   @Benchmark
   public void streamWorstAverageArrivalDelayByDestination() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportWorstAverageArrivalDelayByDestination(context);
   }

   @Benchmark
   public void iteratorMostCancelledFlightsByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostCancelledFlightsByOrigin(context);
   }

   @Benchmark
   public void streamMostCancelledFlightsByOrigin() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostCancelledFlightsByOrigin(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByOriginState() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportTotalFlightsByOriginState(context);
   }

   @Benchmark
   public void streamTotalFlightsByOriginState() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportTotalFlightsByOriginState(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByDestinationState() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportTotalFlightsByDestinationState(context);
   }

   @Benchmark
   public void streamTotalFlightsByDestinationState() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportTotalFlightsByDestinationState(context);
   }

   @Benchmark
   public void iteratorLongestFlights() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportLongestFlights(context);
   }

   @Benchmark
   public void streamLongestFlights() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportLongestFlights(context);
   }

   @Benchmark
   public void iteratorShortestFlights() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportShortestFlights(context);
   }

   @Benchmark
   public void streamShortestFlights() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportShortestFlights(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByDistanceRange() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByDistanceRange(context);
   }

   @Benchmark
   public void streamTotalFlightsByDistanceRange() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByDistanceRange(context);
   }

   @Benchmark
   public void iteratorDaysWithLeastCancellations() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportDaysWithLeastCancellations(context);
   }

   @Benchmark
   public void streamDaysWithLeastCancellations() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportDaysWithLeastCancellations(context);
   }

   @Benchmark
   public void iteratorDaysWithMostCancellations() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportDaysWithMostCancellations(context);
   }

   @Benchmark
   public void streamDaysWithMostCancellations() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportDaysWithMostCancellations(context);
   }

   @Benchmark
   public void iteratorTotalMonthlyFlights() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalMonthlyFlights(context);
   }

   @Benchmark
   public void streamTotalMonthlyFlights() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalMonthlyFlights(context);
   }

   @Benchmark
   public void iteratorTotalDailyFlights() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalDailyFlights(context);
   }

   @Benchmark
   public void streamTotalDailyFlights() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalDailyFlights(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByDayOfWeek() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByDayOfWeek(context);
   }

   @Benchmark
   public void streamTotalFlightsByDayOfWeek() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByDayOfWeek(context);
   }

   @Benchmark
   public void iteratorMostFlightsByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostFlightsByDay(context);
   }

   @Benchmark
   public void streamMostFlightsByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostFlightsByDay(context);
   }

   @Benchmark
   public void iteratorLeastFlightsByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportLeastFlightsByDay(context);
   }

   @Benchmark
   public void streamLeastFlightsByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportLeastFlightsByDay(context);
   }

   @Benchmark
   public void iteratorMostFlightsByOriginByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostFlightsByOriginByDay(context);
   }

   @Benchmark
   public void streamMostFlightsByOriginByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostFlightsByOriginByDay(context);
   }

   @Benchmark
   public void iteratorMostFlightsByCarrierByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostFlightsByCarrierByDay(context);
   }

   @Benchmark
   public void streamMostFlightsByCarrierByDay() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostFlightsByCarrierByDay(context);
   }
}