package org.tinylog.impl.path.segments;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of a {@link PathSegment}.
 *
 * <p>
 *     New path segment builders can be provided as {@link java.util.ServiceLoader service} in
 *     {@code META-INF/services}.
 * </p>
 */
public interface PathSegmentBuilder {

	/**
	 * Gets the name of the path segment, which can be used to address the policy in a configuration.
	 *
	 * <p>
	 *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
	 *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
	 *     allowed.
	 * </p>
	 *
	 * @return The name of the path segment
	 */
	String getName();

	/**
	 * Creates a new instance of the path segment.
	 *
	 * @param framework The actual logging framework instance
	 * @param value Optional configuration value for the created path segment
	 * @return New instance of the path segment
	 * @throws Exception Failed to create a new path segment for the passed configuration value
	 */
	PathSegment create(Framework framework, String value) throws Exception;

}
