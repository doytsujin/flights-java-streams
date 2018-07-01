package airtraffic.benchmark;

import org.beryx.textio.mock.MockTextTerminal;

import airtraffic.ReportContext;
import airtraffic.Repository;


/**
 * Base class for all report benchmarks
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public abstract class AbstractReportsBenchmark {
   protected ReportContext createReportContext() {
      return new ReportContext().setRepository(new Repository())
                                .setTerminal(new MockTextTerminal());
   }
}