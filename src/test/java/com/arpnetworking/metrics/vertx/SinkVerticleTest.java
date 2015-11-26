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

import com.arpnetworking.metrics.Quantity;
import com.arpnetworking.metrics.Unit;
import com.arpnetworking.metrics.Units;
import com.arpnetworking.metrics.vertx.test.TestSinkVerticleImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tests <code>SinkVerticle</code> class.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
public class SinkVerticleTest extends TestVerticle {

    @Override
    public void start() {
        initialize();
        container.deployWorkerVerticle(
                TARGET_WORKER_VERTICLE_NAME,
                new JsonObject(Collections.singletonMap("sinkAddress", SINK_ADDRESS)),
                1,
                false,
                asyncResultWorker -> {
                    VertxAssert.assertTrue(asyncResultWorker.succeeded());
                    // If deployed correctly then start the tests
                    startTests();
                }
        );
    }

    @Test
    public void testValidMessageSentOnEB() throws JsonProcessingException, InterruptedException {
        final Map<String, String> annotationMap = ImmutableMap.of("someAnnotationKey", "someAnnotationValue");
        final Map<String, List<Quantity>> timerSampleMap = ImmutableMap.of(
                "timerSamples",
                Arrays.asList(
                        SinkVerticle.DefaultQuantity.newInstance(100, Units.BIT),
                        SinkVerticle.DefaultQuantity.newInstance(40, Units.GIGABIT)));
        final Map<String, List<Quantity>> counterSampleMap = ImmutableMap.of(
                "counterSamples",
                Collections.singletonList(
                        SinkVerticle.DefaultQuantity.newInstance(400, Units.BITS_PER_SECOND)));
        final Map<String, List<Quantity>> gaugeSampleMap = ImmutableMap.of(
                "gaugeSamples",
                Arrays.asList(
                        SinkVerticle.DefaultQuantity.newInstance(1000, Units.MILLISECOND),
                        SinkVerticle.DefaultQuantity.newInstance(5, Units.MINUTE)));
        final String data = OBJECT_MAPPER.writeValueAsString(
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(annotationMap)
                        .setTimerSamples(timerSampleMap)
                        .setCounterSamples(counterSampleMap)
                        .setGaugeSamples(gaugeSampleMap)
                        .build());
        vertx.eventBus().send(
                SINK_ADDRESS,
                data,
                (Message<String> reply) -> {
                    // TODO(vkoskela): The hook should get the deserialized data and compare it with equals().
                    VertxAssert.assertEquals(data, reply.body());
                    VertxAssert.testComplete();
                });
    }

    @Test
    public void testInvalidMessageSentOnEB() throws JsonProcessingException, InterruptedException {
        final Map<String, Object> dataMap = ImmutableMap.of("someKey", "someValue");
        vertx.eventBus().send(
                SINK_ADDRESS,
                OBJECT_MAPPER.writeValueAsString(dataMap),
                (Message<String> reply) -> {
                    VertxAssert.assertNull(reply);
                    VertxAssert.fail("No reply should have been sent to an invalid message");
                });
        // It is not possible to determine that a reply is _never_ sent
        // TODO(vkoskela): This test and its associated Verticle should be redesigned.
        Thread.sleep(1000);
        VertxAssert.testComplete();
    }

    private static final String TARGET_WORKER_VERTICLE_NAME = TestSinkVerticleImpl.class.getCanonicalName();
    private static final String SINK_ADDRESS = "sink.address.sinkVerticleTest";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(Unit.class, new EventBusSink.UnitSerializer());
        OBJECT_MAPPER.registerModule(module);
    }
}
