package org.tinylog.core.runtime;

/**
 * Builder for creating an instance of a {@link RuntimeFlavor}.
 *
 * <p>
 *     New runtime builders can be provided as {@link java.util.ServiceLoader service} via {@code META-INF/services}.
 * </p>
 */
public interface RuntimeBuilder {

    /**
     * Tests whether this runtime flavor supports the actual virtual machine.
     *
     * @return {@code true} if supported, otherwise {@code false}
     */
    boolean isSupported();

    /**
     * Creates a new instance of the runtime flavor.
     *
     * @return New instance of the runtime flavor
     */
    RuntimeFlavor create();

}
