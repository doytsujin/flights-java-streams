package airtraffic;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

/**
 * Parent class for reporting apps.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class AbstractReportsApp {
	private static final String REPORT_METHOD_NAME_PREFIX = "report";
	private static final int REPORT_METHOD_PARAMETER_COUNT = 1;
	private static final Class<?> REPORT_METHOD_RETURN_TYPE = Void.TYPE;

	private TextIO io = TextIoFactory.getTextIO();
	private TextTerminal<?> terminal = io.getTextTerminal();

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

	protected int readLimit(int defaultValue, int min, int max) {
		return io.newIntInputReader()
				 .withDefaultValue(defaultValue)
				 .withMinVal(min)
				 .withMaxVal(max)
				 .read("Limit");
	}

	protected void executeSelectedReport(Stream<?> source) throws Exception {
		List<Method> reportMethods = getReportMethods();
		int optionNum = getReportOption(reportMethods);
		if(optionNum == 0) {
			System.exit(0);
		}
		Method method = reportMethods.get(optionNum-1);
		terminal.println(getReportDescription(method));
		method.invoke(this, source);
	}

	protected int getReportOption(List<Method> printMethods) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("\nProgram options:\n");
		String format = "%2d  %s\n";
		int n = 0;
		terminal.printf(format, n, "Exit program");
		for(Method m : printMethods) {
			terminal.printf(format, ++n, getReportDescription(m));
		}
		return io.newIntInputReader()
				 .withDefaultValue(0)
				 .withMinVal(0)
				 .withMaxVal(printMethods.size())
				 .read("\nOption");
	}

	protected String getReportDescription(Method method) {
		String name = method.getName().substring(REPORT_METHOD_NAME_PREFIX.length());
		String[] words = splitByCharacterTypeCamelCase(name);
		return Arrays.stream(words).collect(joining(" "));
	}
}