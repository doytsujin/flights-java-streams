package airtraffic.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import airtraffic.GeoLocation;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorAirportReports;
import airtraffic.reports.AirportReports;
import airtraffic.stream.StreamAirportReports;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class AirportReportsBenchmark extends AbstractReportsBenchmark {
   private static final GeoLocation HOUSTON = new GeoLocation() {
      @Override public double getLatitude()  { return 29.7604270;  }
      @Override public double getLongitude() { return -95.3698030; }
   };
   private final AirportReports iteratorImpl = new IteratorAirportReports();
   private final AirportReports streamImpl = new StreamAirportReports();

   @Benchmark
   public void iteratorAirportMetrics() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportAirportMetrics(context);
   }

   @Benchmark
   public void streamAirportMetrics() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportAirportMetrics(context);
   }

   @Benchmark
   public void iteratorAirportsForState() {
      ReportContext context = createReportContext().setState("TX");
      iteratorImpl.reportAirportsForState(context);
   }

   @Benchmark
   public void streamAirportsForState() {
      ReportContext context = createReportContext().setState("TX");
      streamImpl.reportAirportsForState(context);
   }

   @Benchmark
   public void iteratorAirportsNearLocation() {
      ReportContext context = createReportContext().setLocation(HOUSTON)
                                                   .setDistance(100);
      iteratorImpl.reportAirportsNearLocation(context);
   }

   @Benchmark
   public void streamAirportsNearLocation() {
      ReportContext context = createReportContext().setLocation(HOUSTON)
                                                   .setDistance(100);
      streamImpl.reportAirportsNearLocation(context);
   }

   @Benchmark
   public void iteratorAirportsWithHighestCancellationRate() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportAirportsWithHighestCancellationRate(context);
   }

   @Benchmark
   public void streamAirportsWithHighestCancellationRate() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportAirportsWithHighestCancellationRate(context);
   }
}