package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

/**
 * Placeholder implementation for resolving the assigned tag of a log entry.
 */
public class TagPlaceholder implements Placeholder {

	private final String defaultRenderValue;
	private final String defaultApplyValue;

	/**
	 * @param defaultRenderValue The default value to append to string builders, if there is no assigned tag for a
	 *                           passed log entry
	 * @param defaultApplyValue The default value to apply to prepared SQL statements, if there is no assigned tag for a
	 * 	                        passed log entry
	 */
	public TagPlaceholder(String defaultRenderValue, String defaultApplyValue) {
		this.defaultRenderValue = defaultRenderValue;
		this.defaultApplyValue = defaultApplyValue;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.TAG);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String tag = entry.getTag();
		builder.append(tag == null ? defaultRenderValue : tag);
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		String tag = entry.getTag();
		return new SqlRecord<>(SqlType.STRING, tag == null ? defaultApplyValue : tag);
	}

}
