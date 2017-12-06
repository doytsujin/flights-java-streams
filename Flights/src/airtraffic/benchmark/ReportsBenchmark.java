package airtraffic.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class ReportsBenchmark {
   public static void main(String[] args) throws RunnerException {
      Options opt = new OptionsBuilder().include(".*" + AirportReportsBenchmark.class.getSimpleName() + ".*")
                                        .include(".*" + CarrierReportsBenchmark.class.getSimpleName() + ".*")
                                        .include(".*" + FlightReportsBenchmark.class.getSimpleName() + ".*")
                                        .include(".*" + LiveReportsBenchmark.class.getSimpleName() + ".*")
                                        .include(".*" + PlaneReportsBenchmark.class.getSimpleName() + ".*")
                                        .forks(1)
                                        .build();
      new Runner(opt).run();
   }
}