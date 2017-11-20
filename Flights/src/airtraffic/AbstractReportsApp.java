package airtraffic;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for reporting apps.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class AbstractReportsApp {
	private static final String REPORT_METHOD_NAME_PREFIX = "report";
	private static final int REPORT_METHOD_PARAMETER_COUNT = 1;
	private static final Class<?> REPORT_METHOD_RETURN_TYPE = Void.TYPE;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private final Logger logger = LoggerFactory.getLogger(AbstractReportsApp.class);
	private final TextIO io = TextIoFactory.getTextIO();
	private final TextTerminal<?> terminal = io.getTextTerminal();

	protected List<Method> getReportMethods() {
		return Arrays.stream(this.getClass().getDeclaredMethods())
					 .filter(m -> Modifier.isPublic(m.getModifiers()) &&
							 	  m.getName().startsWith(REPORT_METHOD_NAME_PREFIX) &&
							 	  m.getParameterTypes().length == REPORT_METHOD_PARAMETER_COUNT &&
							 	  m.getReturnType().equals(REPORT_METHOD_RETURN_TYPE))
					 .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
					 .collect(toList());
	}

	protected String left(String str, int len) {
		return StringUtils.left(str, len);
	}

	protected String repeat(String str, int len) {
		return StringUtils.repeat(str, len);
	}

	protected void print(String message) {
		terminal.print(message);
	}

	protected void println(String message) {
		terminal.println(message);
	}

	protected void printf(String format, Object... args) {
		terminal.printf(format, args);
	}

	protected String readString(String prompt) {
		return io.newStringInputReader().read(prompt);
	}

	protected double readDouble(String prompt, double min, double max) {
		return io.newDoubleInputReader()
				 .withMinVal(min)
				 .withMaxVal(max)
				 .read(prompt);
	}

	protected int readInt(String prompt, int min, int max) {
		return io.newIntInputReader()
				 .withMinVal(min)
				 .withMaxVal(max)
				 .read(prompt);
	}

	protected int readLimit(int defaultValue, int min, int max) {
		return io.newIntInputReader()
				 .withDefaultValue(defaultValue)
				 .withMinVal(min)
				 .withMaxVal(max)
				 .read("Limit");
	}

	protected int readYear(int min, int max) {
		return io.newIntInputReader()
				 .withDefaultValue(max)
				 .withMinVal(min)
				 .withMaxVal(max)
				 .read("Year");
	}

	protected void executeSelectedReport(Stream<?> source) throws Exception {
		List<Method> reportMethods = getReportMethods();
		int optionNum = getReportOption(reportMethods);
		if(optionNum == 0) {
			System.exit(0);
		}
		Method method = reportMethods.get(optionNum-1);
		logger.debug("User requested invocation of method {}", method.getName());
		terminal.println();
		terminal.println(getReportDescription(method));
		terminal.println();
		method.invoke(this, source);
		terminal.println("\n=== Report complete ===");
	}

	protected int getReportOption(List<Method> printMethods) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("\nReport options:\n");
		String format = "%2d  %s\n";
		int n = 0;
		terminal.printf(format, n, "Exit program");
		for(Method m : printMethods) {
			terminal.printf(format, ++n, getReportDescription(m));
			logger.debug("Found report method {}", m.getName());
		}
		terminal.println();
		return io.newIntInputReader()
				 .withDefaultValue(0)
				 .withMinVal(0)
				 .withMaxVal(printMethods.size())
				 .read("Option");
	}

	protected String getReportDescription(Method method) {
		String name = method.getName().substring(REPORT_METHOD_NAME_PREFIX.length());
		String[] words = splitByCharacterTypeCamelCase(name);
		return Arrays.stream(words).collect(joining(" "));
	}

	protected String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}
}