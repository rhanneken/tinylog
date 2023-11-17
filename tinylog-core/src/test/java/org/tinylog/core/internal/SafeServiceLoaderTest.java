package org.tinylog.core.internal;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.core.test.service.RegisterService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
class SafeServiceLoaderTest {

    @Inject
    private Log log;

    /**
     * Verifies that all registered service implementations can be loaded.
     */
    @RegisterService(service = FooService.class, implementations = {FirstServiceImpl.class, SecondServiceImpl.class})
    @Test
    void loadAllServiceImplementations() {
        List<FooService> services = SafeServiceLoader.asList(
            Thread.currentThread().getContextClassLoader(),
            FooService.class,
            "foo services"
        );

        assertThat(services)
            .hasSize(2)
            .hasAtLeastOneElementOfType(FirstServiceImpl.class)
            .hasAtLeastOneElementOfType(SecondServiceImpl.class);
    }

    /**
     * Verifies that all registered service implementations can be mapped.
     */
    @RegisterService(service = FooService.class, implementations = {FirstServiceImpl.class, SecondServiceImpl.class})
    @Test
    void mapAllServiceImplementations() {
        List<String> names = SafeServiceLoader.asList(
            Thread.currentThread().getContextClassLoader(),
            FooService.class,
            "foo services",
            FooService::getName
        );

        assertThat(names).containsExactlyInAnyOrder("first", "second");
    }

    /**
     * Verifies that a consume operation can be executed for a single service implementation.
     */
    @SuppressWarnings("unchecked")
    @Test
    void consumeSingleServiceImplementation() {
        FirstServiceImpl implementation = new FirstServiceImpl();
        Consumer<FooService> action = (Consumer<FooService>) mock(Consumer.class);
        SafeServiceLoader.execute(implementation, "consume", action);

        verify(action).accept(implementation);
    }

    /**
     * Verifies that a failed consume operation is logged but does not throw any exception.
     */
    @SuppressWarnings("unchecked")
    @Test
    void logFailedConsuming() {
        FirstServiceImpl implementation = new FirstServiceImpl();
        Consumer<FooService> action = (Consumer<FooService>) mock(Consumer.class);
        doThrow(UnsupportedOperationException.class).when(action).accept(implementation);
        SafeServiceLoader.execute(implementation, "consume", action);

        assertThat(log.consume()).hasSize(1).allSatisfy(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getThrowable()).isExactlyInstanceOf(UnsupportedOperationException.class);
            assertThat(entry.getMessage()).contains("consume").contains(FirstServiceImpl.class.getName());
        });
    }

    /**
     * Test service interface.
     */
    public interface FooService {

        /**
         * Gets the name of the service implementation.
         *
         * @return The name of the service implementation
         */
        String getName();

    }

    /**
     * First test service implementation.
     */
    public static class FirstServiceImpl implements FooService {

        @Override
        public String getName() {
            return "first";
        }
    }

    /**
     * Second test service implementation.
     */
    public static class SecondServiceImpl implements FooService {

        @Override
        public String getName() {
            return "second";
        }
    }

    /**
     * Evil test service implementation that throws an {@link java.lang.UnsupportedOperationException}.
     */
    public static class EvilServiceImpl implements FooService {

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

    }

}
