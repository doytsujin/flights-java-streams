package airtraffic;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.beryx.textio.TextTerminal;

@Retention(RUNTIME)
@Target(METHOD)
public @interface TerminalType {
   public Class<? extends TextTerminal<?>> value();
}