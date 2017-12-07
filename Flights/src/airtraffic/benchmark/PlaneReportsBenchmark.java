package airtraffic.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import airtraffic.PlaneReports;
import airtraffic.ReportContext;
import airtraffic.iterator.IteratorPlaneReports;
import airtraffic.stream.StreamPlaneReports;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class PlaneReportsBenchmark extends AbstractReportsBenchmark {
   private PlaneReports iteratorImpl = new IteratorPlaneReports();
   private PlaneReports streamImpl = new StreamPlaneReports();

   @Benchmark
   public void iteratorTotalPlanesByManfacturer() {
      ReportContext context = createReportContext();
      iteratorImpl.reportTotalPlanesByManfacturer(context);
   }

   @Benchmark
   public void streamTotalPlanesByManfacturer() {
      ReportContext context = createReportContext();
      streamImpl.reportTotalPlanesByManfacturer(context);
   }

   @Benchmark
   public void iteratorTotalPlanesByYear() {
      ReportContext context = createReportContext();
      iteratorImpl.reportTotalPlanesByYear(context);
   }

   @Benchmark
   public void streamTotalPlanesByYear() {
      ReportContext context = createReportContext();
      streamImpl.reportTotalPlanesByYear(context);
   }

   @Benchmark
   public void iteratorTotalPlanesByAircraftType() {
      ReportContext context = createReportContext();
      iteratorImpl.reportTotalPlanesByAircraftType(context);
   }

   @Benchmark
   public void streamTotalPlanesByAircraftType() {
      ReportContext context = createReportContext();
      streamImpl.reportTotalPlanesByAircraftType(context);
   }

   @Benchmark
   public void iteratorTotalPlanesByEngineType() {
      ReportContext context = createReportContext();
      iteratorImpl.reportTotalPlanesByEngineType(context);
   }

   @Benchmark
   public void streamTotalPlanesByEngineType() {
      ReportContext context = createReportContext();
      streamImpl.reportTotalPlanesByEngineType(context);
   }

   @Benchmark
   public void iteratorPlanesWithMostCancellations() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      iteratorImpl.reportPlanesWithMostCancellations(context);
   }

   @Benchmark
   public void streamPlanesWithMostCancellations() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      streamImpl.reportPlanesWithMostCancellations(context);
   }

   @Benchmark
   public void iteratorMostFlightsByPlane() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      iteratorImpl.reportMostFlightsByPlane(context);
   }

   @Benchmark
   public void streamMostFlightsByPlane() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      streamImpl.reportMostFlightsByPlane(context);
   }

   @Benchmark
   public void iteratorMostFlightsByPlaneModel() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      iteratorImpl.reportMostFlightsByPlaneModel(context);
   }

   @Benchmark
   public void streamMostFlightsByPlaneModel() {
      ReportContext context = createReportContext().setYear(2008).setLimit(10);
      streamImpl.reportMostFlightsByPlaneModel(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByPlaneManufacturer() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByPlaneManufacturer(context);
   }

   @Benchmark
   public void streamTotalFlightsByPlaneManufacturer() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByPlaneManufacturer(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByPlaneAgeRange() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByPlaneAgeRange(context);
   }

   @Benchmark
   public void streamTotalFlightsByPlaneAgeRange() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByPlaneAgeRange(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByAircraftType() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByAircraftType(context);
   }

   @Benchmark
   public void streamTotalFlightsByAircraftType() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByAircraftType(context);
   }

   @Benchmark
   public void iteratorTotalFlightsByEngineType() {
      ReportContext context = createReportContext().setYear(2008);
      iteratorImpl.reportTotalFlightsByEngineType(context);
   }

   @Benchmark
   public void streamTotalFlightsByEngineType() {
      ReportContext context = createReportContext().setYear(2008);
      streamImpl.reportTotalFlightsByEngineType(context);
   }
}