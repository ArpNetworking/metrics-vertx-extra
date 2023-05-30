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
package com.arpnetworking.metrics.vertx.test;

import com.arpnetworking.metrics.Quantity;
import com.arpnetworking.metrics.Sink;
import com.arpnetworking.metrics.vertx.EventBusSink;
import com.arpnetworking.metrics.vertx.SinkVerticle;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.AbstractVerticle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Test verticle to integrate with the <code>EventBusSink</code>.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
public class TestClientVerticleImpl extends AbstractVerticle {

    @Override
    public void start() {
        final Sink sink = new EventBusSink.Builder()
                .setEventBus(vertx.eventBus())
                .build();
        sink.record(
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(ANNOTATIONS)
                        .setTimerSamples(TIMER_SAMPLES)
                        .setGaugeSamples(GAUGE_SAMPLES)
                        .setCounterSamples(COUNTER_SAMPLES)
                        .build());
    }

    /**
     * Static annotations map.
     */
    public static final Map<String, String> ANNOTATIONS = ImmutableMap.of("someAnnotationKey", "someAnnotationValue");
    /**
     * Static timer samples map.
     */
    public static final Map<String, List<Quantity>> TIMER_SAMPLES = ImmutableMap.of(
            "timerSamples",
            Arrays.asList(
                    SinkVerticle.DefaultQuantity.newInstance(100),
                    SinkVerticle.DefaultQuantity.newInstance(40)));
    /**
     * Static counter samples map.
     */
    public static final Map<String, List<Quantity>> COUNTER_SAMPLES = ImmutableMap.of(
            "counterSamples",
            Collections.singletonList(
                    SinkVerticle.DefaultQuantity.newInstance(400)));
    /**
     * Static gauge samples map.
     */
    public static final Map<String, List<Quantity>> GAUGE_SAMPLES = ImmutableMap.of(
            "gaugeSamples",
            Arrays.asList(
                    SinkVerticle.DefaultQuantity.newInstance(1000),
                    SinkVerticle.DefaultQuantity.newInstance(5)));
}
