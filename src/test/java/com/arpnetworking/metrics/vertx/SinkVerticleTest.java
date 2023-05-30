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
import com.arpnetworking.metrics.vertx.test.TestSinkVerticleImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tests <code>SinkVerticle</code> class.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
@RunWith(VertxUnitRunner.class)
public class SinkVerticleTest {

    @Before
    public void setUp(final TestContext context) {
        _rule.vertx().deployVerticle(
                TARGET_WORKER_VERTICLE_NAME,
                new DeploymentOptions()
                        .setConfig(new JsonObject(Collections.singletonMap("sinkAddress", SINK_ADDRESS)))
                        .setInstances(1)
                        .setWorker(true),
                context.asyncAssertSuccess()
        );
    }

    @Test
    public void testValidMessageSentOnEB(final TestContext context) throws JsonProcessingException, InterruptedException {
        final Map<String, String> annotationMap = ImmutableMap.of("someAnnotationKey", "someAnnotationValue");
        final Map<String, List<Quantity>> timerSampleMap = ImmutableMap.of(
                "timerSamples",
                Arrays.asList(
                        SinkVerticle.DefaultQuantity.newInstance(100),
                        SinkVerticle.DefaultQuantity.newInstance(40)));
        final Map<String, List<Quantity>> counterSampleMap = ImmutableMap.of(
                "counterSamples",
                Collections.singletonList(
                        SinkVerticle.DefaultQuantity.newInstance(400)));
        final Map<String, List<Quantity>> gaugeSampleMap = ImmutableMap.of(
                "gaugeSamples",
                Arrays.asList(
                        SinkVerticle.DefaultQuantity.newInstance(1000),
                        SinkVerticle.DefaultQuantity.newInstance(5)));
        final String data = OBJECT_MAPPER.writeValueAsString(
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(annotationMap)
                        .setTimerSamples(timerSampleMap)
                        .setCounterSamples(counterSampleMap)
                        .setGaugeSamples(gaugeSampleMap)
                        .build());
        _rule.vertx().eventBus().request(
                SINK_ADDRESS,
                data,
                (AsyncResult<Message<String>> reply) -> {
                    // TODO(vkoskela): The hook should get the deserialized data and compare it with equals().
                    context.assertEquals(data, reply.result().body());
                });
    }

//    @Test
    public void testInvalidMessageSentOnEB(final TestContext context) throws JsonProcessingException, InterruptedException {
        final Map<String, Object> dataMap = ImmutableMap.of("someKey", "someValue");
        _rule.vertx().eventBus().request(
                SINK_ADDRESS,
                OBJECT_MAPPER.writeValueAsString(dataMap),
                (AsyncResult<Message<String>> reply) -> {
                    context.assertNull(reply.result());
                    context.fail("No reply should have been sent to an invalid message");
                });
        // It is not possible to determine that a reply is _never_ sent
        // TODO(vkoskela): This test and its associated Verticle should be redesigned.
        Thread.sleep(1000);
    }

    private static final String TARGET_WORKER_VERTICLE_NAME = TestSinkVerticleImpl.class.getCanonicalName();
    private static final String SINK_ADDRESS = "sink.address.sinkVerticleTest";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Rule
    public RunTestOnContext _rule = new RunTestOnContext();

    static {
        final SimpleModule module = new SimpleModule();
        OBJECT_MAPPER.registerModule(module);
    }
}
