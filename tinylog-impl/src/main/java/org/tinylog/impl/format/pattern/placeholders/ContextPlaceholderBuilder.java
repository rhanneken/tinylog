package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link ContextPlaceholder}.
 */
public class ContextPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ContextPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "context";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Thread context key is not defined for context placeholder");
        } else {
            int commaIndex = value.indexOf(',');
            if (commaIndex < 0) {
                return new ContextPlaceholder(value, null, "<" + value + " not set>");
            } else {
                String key = value.substring(0, commaIndex);
                String defaultValue = value.substring(commaIndex + 1);
                return new ContextPlaceholder(key, defaultValue, defaultValue);
            }
        }
    }

}
