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
import airtraffic.reports.CarrierReports;
import airtraffic.reports.iterator.IteratorCarrierReports;
import airtraffic.reports.stream.StreamCarrierReports;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CarrierReportsBenchmark extends AbstractReportsBenchmark {
   private final CarrierReports iteratorImpl = new IteratorCarrierReports();
   private final CarrierReports streamImpl = new StreamCarrierReports();

   @Benchmark
   public void iteratorCarrierMetrics() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportCarrierMetrics(context);
   }

   @Benchmark
   public void streamCarrierMetrics() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportCarrierMetrics(context);
   }

   @Benchmark
   public void iteratorCarriersWithHighestCancellationRate() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportCarriersWithHighestCancellationRate(context);
   }

   @Benchmark
   public void streamCarriersWithHighestCancellationRate() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportCarriersWithHighestCancellationRate(context);
   }

   @Benchmark
   public void iteratorMostCancelledFlightsByCarrier() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      iteratorImpl.reportMostCancelledFlightsByCarrier(context);
   }

   @Benchmark
   public void streamMostCancelledFlightsByCarrier() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setLimit(10);
      streamImpl.reportMostCancelledFlightsByCarrier(context);
   }
}