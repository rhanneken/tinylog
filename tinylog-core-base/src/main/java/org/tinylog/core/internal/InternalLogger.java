package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Static logger for issuing internal log entries.
 */
public final class InternalLogger {

	/**
	 * The tag to use for internal tinylog log entries.
	 */
	public static final String TAG = "tinylog";

	private static final int INTERNAL_STACK_TRACE_DEPTH = 1;
	private static final int CALLER_STACK_TRACE_DEPTH = 3;

	private static final Object mutex = new Object();
	private static volatile State state = new State(null, null, null);
	private static List<LogEntry> entries = new ArrayList<>();

	/** */
	private InternalLogger() {
	}

	/**
	 * Initializes the internal logger.
	 *
	 * @param framework Fully initialized framework for setting this logger up
	 */
	public static void init(Framework framework) {
		RuntimeFlavor runtime = framework.getRuntime();
		StackTraceLocation location = runtime.getStackTraceLocationAtIndex(INTERNAL_STACK_TRACE_DEPTH);
		LoggingBackend backend = framework.getLoggingBackend();
		MessageFormatter formatter = new EnhancedMessageFormatter(framework);

		synchronized (mutex) {
			state = new State(runtime, backend, formatter);

			for (LogEntry entry : entries) {
				backend.log(location, TAG, entry.getLevel(), entry.getThrowable(), entry.getMessage(),
					entry.getArguments(), formatter);
			}

			entries = new ArrayList<>();
		}
	}

	/**
	 * Resets the internal logger to an uninitialized state.
	 */
	public static void reset() {
		synchronized (mutex) {
			state = new State(null, null, null);
			entries = new ArrayList<>();
		}
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void trace(Throwable ex, String message) {
		if (state.visibility.getTrace() != OutputDetails.DISABLED) {
			log(Level.TRACE, ex, message, null);
		}
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void trace(Throwable ex, String message, Object... arguments) {
		if (state.visibility.getTrace() != OutputDetails.DISABLED) {
			log(Level.TRACE, ex, message, arguments);
		}
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void debug(Throwable ex, String message) {
		if (state.visibility.getDebug() != OutputDetails.DISABLED) {
			log(Level.DEBUG, ex, message, null);
		}
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void debug(Throwable ex, String message, Object... arguments) {
		if (state.visibility.getDebug() != OutputDetails.DISABLED) {
			log(Level.DEBUG, ex, message, arguments);
		}
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void info(Throwable ex, String message) {
		if (state.visibility.getInfo() != OutputDetails.DISABLED) {
			log(Level.INFO, ex, message, null);
		}
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void info(Throwable ex, String message, Object... arguments) {
		if (state.visibility.getInfo() != OutputDetails.DISABLED) {
			log(Level.INFO, ex, message, arguments);
		}
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void warn(Throwable ex, String message) {
		if (state.visibility.getWarn() != OutputDetails.DISABLED) {
			log(Level.WARN, ex, message, null);
		}
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void warn(Throwable ex, String message, Object... arguments) {
		if (state.visibility.getWarn() != OutputDetails.DISABLED) {
			log(Level.WARN, ex, message, arguments);
		}
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void error(Throwable ex, String message) {
		if (state.visibility.getError() != OutputDetails.DISABLED) {
			log(Level.ERROR, ex, message, null);
		}
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void error(Throwable ex, String message, Object... arguments) {
		if (state.visibility.getError() != OutputDetails.DISABLED) {
			log(Level.ERROR, ex, message, arguments);
		}
	}

	/**
	 * Issues a log entry at the defined severity level.
	 *
	 * @param level Severity level
	 * @param throwable Exception or any other kind of throwable
	 * @param message Human-readable text message
	 * @param arguments Argument values for placeholders in the text message
	 */
	private static void log(Level level, Throwable throwable, String message, Object[] arguments) {
		State state = InternalLogger.state;

		if (state.backend == null) {
			synchronized (mutex) {
				state = InternalLogger.state;
				if (state.backend == null) {
					entries.add(new LogEntry(level, throwable, message, arguments));
				}
			}
		}

		if (state.backend != null) {
			StackTraceLocation location = state.runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			state.backend.log(location, TAG, level, throwable, message, arguments, state.formatter);
		}
	}

	/**
	 * Internal logger state with {@link RuntimeFlavor}, {@link LoggingBackend}, and {@link MessageFormatter}.
	 */
	private static final class State {

		private final RuntimeFlavor runtime;
		private final LoggingBackend backend;
		private final MessageFormatter formatter;
		private final LevelVisibility visibility;

		/**
		 * @param runtime Runtime flavor for extraction of stack trace location
		 * @param backend Logging backend for output log entries
		 * @param formatter Message formatter for replacing placeholders by real values
		 */
		private State(RuntimeFlavor runtime, LoggingBackend backend, MessageFormatter formatter) {
			this.runtime = runtime;
			this.backend = backend;
			this.formatter = formatter;

			if (backend == null) {
				visibility = new LevelVisibility(
					OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
					OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
					OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
					OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
					OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION
				);
			} else {
				visibility = backend.getLevelVisibility(TAG);
			}
		}

	}

}
