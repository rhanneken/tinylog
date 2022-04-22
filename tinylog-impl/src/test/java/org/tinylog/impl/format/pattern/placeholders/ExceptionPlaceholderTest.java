package org.tinylog.impl.format.pattern.placeholders;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#EXCEPTION} is defined as required by the exception
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that {@code null} will be resolved, if no exception or other kind of
	 * throwable is set.
	 */
	@Test
	void resolveWithoutException() {
		LogEntry logEntry = new LogEntryBuilder().create();

		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that an exception without description message is resolved correctly.
	 */
	@Test
	void resolveExceptionWithoutMessage() {
		RuntimeException exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();

		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception with description message is resolved correctly.
	 */
	@Test
	void resolveExceptionWithMessage() {
		RuntimeException exception = new RuntimeException("Oops!");
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();

		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception containing a suppressed exception is resolved correctly.
	 */
	@Test
	void resolveExceptionWithSuppression() {
		RuntimeException exception = new RuntimeException();
		exception.addSuppressed(new IllegalAccessException());
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();

		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception containing a cause exception is resolved correctly.
	 */
	@Test
	void resolveExceptionWithCause() {
		IllegalAccessException cause = new IllegalAccessException();
		RuntimeException exception = new RuntimeException(cause);
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();

		ExceptionPlaceholder placeholder = new ExceptionPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an empty string is rendered for a log entry without any stored exception or any other kind
	 * of throwable.
	 */
	@Test
	void renderWithoutException() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ExceptionPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEmpty();
	}

	/**
	 * Verifies that an exception without description message is rendered correctly.
	 */
	@Test
	void renderExceptionWithoutMessage() {
		RuntimeException exception = new RuntimeException();

		FormatOutputRenderer renderer = new FormatOutputRenderer(new ExceptionPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception with description message is rendered correctly.
	 */
	@Test
	void renderExceptionWithMessage() {
		RuntimeException exception = new RuntimeException("Oops!");

		FormatOutputRenderer renderer = new FormatOutputRenderer(new ExceptionPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception containing a suppressed exception is rendered correctly.
	 */
	@Test
	void renderExceptionWithSuppression() {
		RuntimeException exception = new RuntimeException();
		exception.addSuppressed(new IllegalAccessException());

		FormatOutputRenderer renderer = new FormatOutputRenderer(new ExceptionPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that an exception containing a cause exception is rendered correctly.
	 */
	@Test
	void renderExceptionWithCause() {
		IllegalAccessException cause = new IllegalAccessException();
		RuntimeException exception = new RuntimeException(cause);

		FormatOutputRenderer renderer = new FormatOutputRenderer(new ExceptionPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Prints a throwable including its stack trace as string.
	 *
	 * @param throwable The throwable to print
	 * @return The completely rendered throwable including stack trace
	 */
	private String print(Throwable throwable) {
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		return writer.toString().trim();
	}

}
