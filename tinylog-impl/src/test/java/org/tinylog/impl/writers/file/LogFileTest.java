package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

class LogFileTest {

	private Path path;

	/**
	 * Creates a temporary log file.
	 *
	 * @throws IOException Failed to create a temporary log file
	 */
	@BeforeEach
	void init() throws IOException {
		path = Files.createTempFile("tinylog", ".log");
		path.toFile().deleteOnExit();
	}

	/**
	 * Deletes the created temporary log file.
	 *
	 * @throws IOException Failed to delete the temporary log file
	 */
	@AfterEach
	void release() throws IOException {
		Files.deleteIfExists(path);
	}

	/**
	 * Verifies that an existing log file can be overwritten.
	 *
	 * @param charset The charset to use for writing text
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void overwriteLogFile(Charset charset) throws IOException {
		try (LogFile file = new LogFile(path.toString(), 64, charset, false)) {
			file.write("foo".getBytes(charset));
		}

		try (LogFile file = new LogFile(path.toString(), 64, charset, false)) {
			file.write("bar".getBytes(charset));
		}

		assertThat(path).usingCharset(charset).hasContent("bar");
	}

	/**
	 * Verifies that an existing log file can be continued.
	 *
	 * @param charset The charset to use for writing text
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void continueLogFile(Charset charset) throws IOException {
		try (LogFile file = new LogFile(path.toString(), 64, charset, true)) {
			file.write("foo".getBytes(charset));
		}

		try (LogFile file = new LogFile(path.toString(), 64, charset, true)) {
			file.write("bar".getBytes(charset));
		}

		assertThat(path).usingCharset(charset).hasContent("foobar");
	}

	/**
	 * Verifies that the buffer will be written into the file when the buffer is full.
	 */
	@Test
	void writingUntilBufferCapacity() throws IOException {
		Files.write(path, new byte[6]);

		try (LogFile file = new LogFile(path.toString(), 8, StandardCharsets.US_ASCII, true)) {
			assertThat(path).hasSize(6);
			file.write(new byte[1]);

			assertThat(path).hasSize(6);
			file.write(new byte[1]);

			assertThat(path).hasSize(8);
		}
	}

	/**
	 * Verifies that strings can be written that are larger than the buffer size.
	 */
	@Test
	void writingOversizedString() throws IOException {
		try (LogFile file = new LogFile(path.toString(), 8, StandardCharsets.US_ASCII, false)) {
			file.write(new byte[16 + 1]);
			assertThat(path).hasSize(16);
		}

		assertThat(path).hasSize(16 + 1);
	}

	/**
	 * Verifies that the buffer will be written into the file when flushing.
	 *
	 * @param charset The charset to use for writing text
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void flushing(Charset charset) throws IOException {
		try (LogFile file = new LogFile(path.toString(), 64, charset, false)) {
			file.write("foo".getBytes(charset));
			assertThat(path).usingCharset(charset).isEmptyFile();

			file.flush();
			assertThat(path).usingCharset(charset).hasContent("foo");
		}
	}

	/**
	 * Provider for all charsets to test.
	 */
	private static final class CharsetsProvider implements ArgumentsProvider {

		/** */
		private CharsetsProvider() {
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
				Arguments.of(StandardCharsets.US_ASCII),
				Arguments.of(StandardCharsets.ISO_8859_1),
				Arguments.of(StandardCharsets.UTF_8),
				Arguments.of(StandardCharsets.UTF_16)
			);
		}

	}

}
