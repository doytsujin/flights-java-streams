package airtraffic.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import airtraffic.LiveReports;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorLiveReports;
import airtraffic.stream.StreamLiveReports;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class LiveReportsBenchmark extends AbstractReportsBenchmark {
   private LiveReports iteratorImpl = new IteratorLiveReports();
   private LiveReports streamImpl = new StreamLiveReports();

   @Benchmark
   public void iteratorAirportMetrics() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setAirportByIATA("IAH");
      iteratorImpl.reportAirportMetrics(context);
   }

   @Benchmark
   public void streamAirportMetrics() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setAirportByIATA("IAH");
      streamImpl.reportAirportMetrics(context);
   }

   @Benchmark
   public void iteratorCarrierMetrics() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setCarrierByCode("UA");
      iteratorImpl.reportCarrierMetrics(context);
   }

   @Benchmark
   public void streamCarrierMetrics() {
      ReportContext context = createReportContext().setYear(2008)
                                                   .setCarrierByCode("UA");
      streamImpl.reportCarrierMetrics(context);
   }
}