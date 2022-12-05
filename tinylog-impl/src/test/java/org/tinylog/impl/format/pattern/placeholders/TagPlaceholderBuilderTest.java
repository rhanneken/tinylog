package org.tinylog.impl.format.pattern.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class TagPlaceholderBuilderTest {

    private static final LogEntry emptyLogEntry = new LogEntryBuilder().create();
    private static final LogEntry filledLogEntry = new LogEntryBuilder().tag("foo").create();

    @Inject
    private LoggingContext context;

    /**
     * Verifies that tag placeholders without any custom default value are instantiated correctly.
     */
    @Test
    void creationWithoutDefaultValue() {
        Placeholder placeholder = new TagPlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(TagPlaceholder.class);
        assertThat(placeholder.getValue(emptyLogEntry)).isNull();
        assertThat(placeholder.getValue(filledLogEntry)).isEqualTo("foo");

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        assertThat(renderer.render(emptyLogEntry)).isEqualTo("<untagged>");
        assertThat(renderer.render(filledLogEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that tag placeholders with a custom default value are instantiated correctly.
     */
    @Test
    void creationWithDefaultValue() {
        Placeholder placeholder = new TagPlaceholderBuilder().create(context, "none");
        assertThat(placeholder).isInstanceOf(TagPlaceholder.class);
        assertThat(placeholder.getValue(emptyLogEntry)).isEqualTo("none");
        assertThat(placeholder.getValue(filledLogEntry)).isEqualTo("foo");

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        assertThat(renderer.render(emptyLogEntry)).isEqualTo("none");
        assertThat(renderer.render(filledLogEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(TagPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("tag");
        });
    }

}
