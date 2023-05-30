/**
 * Copyright 2015 Groupon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arpnetworking.metrics.vertx;

import com.arpnetworking.metrics.Event;
import com.arpnetworking.metrics.Quantity;
import com.arpnetworking.metrics.Sink;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An abstract verticle responsible for writing the metrics data to the targeted sink. This verticle subscribes to the
 * vertx event bus to receive the metrics data. This approach lets the client have multiple verticles write to the same
 * sink without having to share the {@link com.arpnetworking.metrics.MetricsFactory} instance. Alternatively, in cases where the verticles
 * can share an instance of {@link com.arpnetworking.metrics.MetricsFactory}, they can do so by defining an instance of the
 * {@link SharedMetricsFactory} in the shared data space of vertx.
 *
 * Implementations of this class should define the implementation for the {@link #createSinks()} method, that
 * returns a not null {@link List} of sinks. The client may choose to override the implementation of the method
 * {@link #initializeHandler()}. Implementations of this class should be deployed as a worker verticle
 * since writing to a sink is a blocking operation. The config for this verticle should contain the "sinkAddress" key.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
public abstract class SinkVerticle extends AbstractVerticle {

    @Override
    public void start() {
        _sinks = new ArrayList<>(createSinks());
        _handler = initializeHandler();
        _sinkAddress = config().getString("sinkAddress", DEFAULT_SINK_ADDRESS);

        vertx.eventBus().localConsumer(_sinkAddress, _handler);
    }

    /**
     * Initializes the member sinks with a list of sinks to write to.
     *
     * @return A {@link List} of sinks.
     */
    protected abstract List<Sink> createSinks();

    /**
     * Initializes the member handler with an appropriate message handler. The default implementation is to initialize
     * with the {@link SinkHandler} instance.
     *
     * @return An instance of {@link Handler}.
     */
    protected Handler<Message<String>> initializeHandler() {
        return new SinkHandler(_sinks);
    }

    protected String _sinkAddress;
    protected List<Sink> _sinks;
    protected Handler<Message<String>> _handler;

    private static final String DEFAULT_SINK_ADDRESS = "metrics.sink.default";

    /**
     * Event bus message handler class for {@link SinkVerticle}.
     */
    protected static class SinkHandler implements Handler<Message<String>> {

        /**
         * Public constructor.
         *
         * @param sinks A {@link List} of sinks.
         */
        public SinkHandler(final List<Sink> sinks) {
            _sinks = sinks;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handle(final Message<String> message) {
            if (message == null || message.body() == null) {
                LOGGER.warn("Null message received.");
                return;
            }
            try {
                processMessage(message);
                // CHECKSTYLE.OFF: IllegalCatch - By design
            } catch (final Exception e) {
                // CHECKSTYLE.ON: IllegalCatch
                LOGGER.warn("Message is not in expected format.", e.getMessage());
            }
        }

        /**
         * Process a message. All exceptions are propagated to callers.
         *
         * @param message The Message instance to process.
         * @throws Exception if Message processing fails.
         */
        protected void processMessage(final Message<String> message) throws Exception {
            final DefaultEvent.Builder eventBuilder = OBJECT_MAPPER.readValue(message.body(), DefaultEvent.Builder.class);
            final Event event = eventBuilder.build();
            for (final Sink sink: _sinks) {
                sink.record(event);
            }
        }

        protected final List<Sink> _sinks;

        private static final Logger LOGGER = LoggerFactory.getLogger(SinkHandler.class);
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            final SimpleModule module = new SimpleModule();
            module.addAbstractTypeMapping(Quantity.class, DefaultQuantity.class);
            OBJECT_MAPPER.registerModule(module);
        }


    }

    /**
     * Default implementation of {@link Event} for deserialization purposes.
     */
    public static final class DefaultEvent implements Event {

        @Override
        public Map<String, String> getAnnotations() {
            return Collections.unmodifiableMap(_annotations);
        }

        @Override
        public Map<String, List<Quantity>> getTimerSamples() {
            return Collections.unmodifiableMap(_timerSamples);
        }

        @Override
        public Map<String, List<Quantity>> getCounterSamples() {
            return Collections.unmodifiableMap(_counterSamples);
        }

        @Override
        public Map<String, List<Quantity>> getGaugeSamples() {
            return Collections.unmodifiableMap(_gaugeSamples);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DefaultEvent)) {
                return false;
            }
            final DefaultEvent otherEvent = (DefaultEvent) other;
            return Objects.equals(_annotations, otherEvent._annotations)
                    && Objects.equals(_counterSamples, otherEvent._counterSamples)
                    && Objects.equals(_timerSamples, otherEvent._timerSamples)
                    && Objects.equals(_gaugeSamples, otherEvent._gaugeSamples);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_annotations, _counterSamples, _timerSamples, _gaugeSamples);
        }

        @Override
        public String toString() {
            return String.format(
                    "DefaultEvent{Annotations=%s, TimerSamples=%s, CounterSamples=%s, GaugeSamples=%s}",
                    _annotations,
                    _timerSamples,
                    _counterSamples,
                    _gaugeSamples);
        }

        private DefaultEvent(final Builder builder) {
            _annotations = Collections.unmodifiableMap(builder._annotations);
            _timerSamples = Collections.unmodifiableMap(builder._timerSamples);
            _gaugeSamples = Collections.unmodifiableMap(builder._gaugeSamples);
            _counterSamples = Collections.unmodifiableMap(builder._counterSamples);
        }

        private final Map<String, String> _annotations;
        private final Map<String, List<Quantity>> _timerSamples;
        private final Map<String, List<Quantity>> _counterSamples;
        private final Map<String, List<Quantity>> _gaugeSamples;

        /**
         * Builder implementation for {@link com.arpnetworking.metrics.impl.TsdEvent}.
         */
        public static final class Builder {

            /**
             * Builds an instance of {@link com.arpnetworking.metrics.impl.TsdEvent}.
             *
             * @return An instance of {@link com.arpnetworking.metrics.impl.TsdEvent}.
             */
            public Event build() {
                if (_annotations == null) {
                    throw new IllegalArgumentException("Annotations cannot be null.");
                }
                if (_timerSamples == null) {
                    throw new IllegalArgumentException("TimerSamples cannot be null.");
                }
                if (_counterSamples == null) {
                    throw new IllegalArgumentException("CounterSamples cannot be null.");
                }
                if (_gaugeSamples == null) {
                    throw new IllegalArgumentException("GaugeSamples cannot be null.");
                }
                return new DefaultEvent(this);
            }

            /**
             * Sets the annotations.
             *
             * @param value A {@link Map} for annotations.
             * @return This instance of {@link Builder}.
             */
            public Builder setAnnotations(final Map<String, String> value) {
                _annotations = Collections.unmodifiableMap(value);
                return this;
            }

            /**
             * Sets the timer samples.
             *
             * @param value A {@link Map} for timer samples.
             * @return This instance of {@link Builder}.
             */
            public Builder setTimerSamples(final Map<String, List<Quantity>> value) {
                _timerSamples = Collections.unmodifiableMap(value);
                return this;
            }

            /**
             * Sets the counter samples.
             *
             * @param value A {@link Map} for counter samples.
             * @return This instance of {@link Builder}.
             */
            public Builder setCounterSamples(final Map<String, List<Quantity>> value) {
                _counterSamples = Collections.unmodifiableMap(value);
                return this;
            }

            /**
             * Sets the gauge samples.
             *
             * @param value A {@link Map} for gauge samples.
             * @return This instance of {@link Builder}.
             */
            public Builder setGaugeSamples(final Map<String, List<Quantity>> value) {
                _gaugeSamples = Collections.unmodifiableMap(value);
                return this;
            }

            private Map<String, String> _annotations;
            private Map<String, List<Quantity>> _timerSamples;
            private Map<String, List<Quantity>> _counterSamples;
            private Map<String, List<Quantity>> _gaugeSamples;
        }
    }

    /**
     * Default implementation of {@link Quantity} for deserialization purposes.
     */
    public static class DefaultQuantity implements Quantity {

        /**
         * Default constructor.
         */
        public DefaultQuantity() {}

        /**
         * Static factory method.
         *
         * @param value An instance of {@link Number}.
         * @return An instance of {@link Quantity}.
         */
        public static Quantity newInstance(final Number value) {
            return new DefaultQuantity(value);
        }

        @Override
        public Number getValue() {
            return _value;
        }


        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof DefaultQuantity)) {
                return false;
            }

            final DefaultQuantity otherQuantity = (DefaultQuantity) other;
            return Objects.equals(getValue(), otherQuantity.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(_value);
        }

        private DefaultQuantity(final Number value) {
            _value = value;
        }

        @JsonProperty("value")
        private Number _value;
    }
}
