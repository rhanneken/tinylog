package org.tinylog.core;

/**
 * Severity levels for log entries and configuration.
 */
public enum Level {

    /**
     * The off severity level is for configuration use only and disables any logging. Log entries must never have
     * an off severity level assigned.
     */
    OFF,

    /**
     * Error log entries contain severe technical errors that prevent normal operation.
     */
    ERROR,

    /**
     * Warn log entries contain technical warnings that indicate that something has gone wrong, but do not prevent
     * operation.
     */
    WARN,

    /**
     * Info log entries contain important and relevant information.
     */
    INFO,

    /**
     * Debug log entries contain detailed debug information for developers.
     */
    DEBUG,

    /**
     * Trace log entries contain very fine-grained debug information for developers, typically the flow through.
     */
    TRACE;

    /**
     * Tests if this severity level is at least as severe as the passed severity level.
     *
     * @param other The severity level to compare with
     * @return {@code true} if this severity level is same severe as the passed severity level or more severe than it,
     *         {@code false} if this severity level is less severe than the passed severity level
     */
    public boolean isAtLeastAsSevereAs(Level other) {
        return this.ordinal() <= other.ordinal();
    }

    /**
     * Calculates the least serve level of two passed severity level.
     *
     * @param first The first severity level to compare
     * @param second The second severity level to compare
     * @return The least serve level
     */
    public static Level leastSevereLevel(Level first, Level second) {
        if (first.ordinal() > second.ordinal()) {
            return first;
        } else {
            return second;
        }
    }

    /**
     * Calculates the most serve level of two passed severity level.
     *
     * @param first The first severity level to compare
     * @param second The second severity level to compare
     * @return The most serve level
     */
    public static Level mostSevereLevel(Level first, Level second) {
        if (first.ordinal() < second.ordinal()) {
            return first;
        } else {
            return second;
        }
    }

}
