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

import com.arpnetworking.metrics.vertx.test.TestClientVerticleImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Integration tests for client verticle writing to sinks through {@link EventBusSink} and {@link SinkVerticle}.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
@RunWith(VertxUnitRunner.class)
@NotThreadSafe
public class ClientVerticleTest {

    @Before
    public void setUp() {
        _mocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        _mocks.close();
    }

    @Test
    public void testClientVerticlePublishesToDefaultAddress(final TestContext context) throws JsonProcessingException {
        Mockito.doNothing().when(_handler).handle(ArgumentMatchers.any());
        final String expectedData = OBJECT_MAPPER.writeValueAsString(
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(TestClientVerticleImpl.ANNOTATIONS)
                        .setCounterSamples(TestClientVerticleImpl.COUNTER_SAMPLES)
                        .setTimerSamples(TestClientVerticleImpl.TIMER_SAMPLES)
                        .setGaugeSamples(TestClientVerticleImpl.GAUGE_SAMPLES)
                        .build());

        _rule.vertx().eventBus().localConsumer(
                DEFAULT_SINK_ADDRESS,
                (Handler<Message<String>>) message -> context.assertEquals(expectedData, message.body()));

        _rule.vertx().deployVerticle(
                TARGET_CLIENT_VERTICLE_NAME,
                context.asyncAssertSuccess()
        );
    }

    @Mock
    private Handler<Message<String>> _handler;

    @Rule
    public RunTestOnContext _rule = new RunTestOnContext();

    @Captor
    private ArgumentCaptor<Message<String>> _argumentCaptor;

    private AutoCloseable _mocks;

    private static final String DEFAULT_SINK_ADDRESS = "metrics.sink.default";
    private static final String TARGET_CLIENT_VERTICLE_NAME = TestClientVerticleImpl.class.getCanonicalName();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        final SimpleModule module = new SimpleModule();
        OBJECT_MAPPER.registerModule(module);
    }
}
