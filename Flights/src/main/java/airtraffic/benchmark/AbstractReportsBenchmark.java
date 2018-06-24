package airtraffic.benchmark;

import org.beryx.textio.mock.MockTextTerminal;

import airtraffic.ReportContext;
import airtraffic.Repository;

public abstract class AbstractReportsBenchmark {
   protected ReportContext createReportContext() {
      return new ReportContext().setRepository(new Repository())
                                .setTerminal(new MockTextTerminal());
   }
}