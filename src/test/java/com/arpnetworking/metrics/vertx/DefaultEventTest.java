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
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for <code>SinkVerticle.DefaultEvent</code>.
 *
 * @author Ville Koskela (vkoskela at groupon dot com)
 */
public class DefaultEventTest {

    @Test
    public void test() {
        // CHECKSTYLE.OFF: IllegalInstantiation - No Guava
        final Map<String, String> annotations = new HashMap<>();
        annotations.put("foo", "bar");
        final Map<String, List<Quantity>> timerSamples = new HashMap<>();
        timerSamples.put("timer", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(1)));
        final Map<String, List<Quantity>> counterSamples = new HashMap<>();
        counterSamples.put("counter", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(1.23)));
        final Map<String, List<Quantity>> gaugeSamples = new HashMap<>();
        gaugeSamples.put("gauge", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(1.23)));
        // CHECKSTYLE.ON: IllegalInstantiation
        final Event event = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(gaugeSamples)
                .build();

        Assert.assertEquals(annotations, event.getAnnotations());
        Assert.assertEquals(timerSamples, event.getTimerSamples());
        Assert.assertEquals(counterSamples, event.getCounterSamples());
        Assert.assertEquals(gaugeSamples, event.getGaugeSamples());
    }

    @Test
    public void testEquals() {
        // CHECKSTYLE.OFF: IllegalInstantiation - No Guava
        final Map<String, String> annotations = new HashMap<>();
        annotations.put("foo", "bar");
        final Map<String, List<Quantity>> timerSamples = new HashMap<>();
        timerSamples.put("timer", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Integer.valueOf(1))));
        final Map<String, List<Quantity>> counterSamples = new HashMap<>();
        counterSamples.put("counter", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        final Map<String, List<Quantity>> gaugeSamples = new HashMap<>();
        gaugeSamples.put("gauge", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        // CHECKSTYLE.ON: IllegalInstantiation
        final Event event = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(gaugeSamples)
                .build();

        Assert.assertTrue(event.equals(event));

        Assert.assertFalse(event.equals(null));
        Assert.assertFalse(event.equals("This is a String"));

        // CHECKSTYLE.OFF: IllegalInstantiation - No Guava
        final Map<String, String> differentAnnotations = new HashMap<>();
        annotations.put("foo2", "bar");
        final Map<String, List<Quantity>> differentTimerSamples = new HashMap<>();
        differentTimerSamples.put("timer2", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Integer.valueOf(1))));
        final Map<String, List<Quantity>> differentCounterSamples = new HashMap<>();
        differentCounterSamples.put("counter2", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        final Map<String, List<Quantity>> differentGaugeSamples = new HashMap<>();
        differentGaugeSamples.put("gauge2", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        // CHECKSTYLE.ON: IllegalInstantiation

        final Event differentEvent1 = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(differentAnnotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(gaugeSamples)
                .build();

        final Event differentEvent2 = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(differentTimerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(gaugeSamples)
                .build();

        final Event differentEvent3 = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(differentCounterSamples)
                .setGaugeSamples(gaugeSamples)
                .build();

        final Event differentEvent4 = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(differentGaugeSamples)
                .build();


        Assert.assertFalse(event.equals(differentEvent1));
        Assert.assertFalse(event.equals(differentEvent2));
        Assert.assertFalse(event.equals(differentEvent3));
        Assert.assertFalse(event.equals(differentEvent4));
    }

    @Test
    public void testHashCode() {
        // CHECKSTYLE.OFF: IllegalInstantiation - No Guava
        final Map<String, String> annotations = new HashMap<>();
        annotations.put("foo", "bar");
        final Map<String, List<Quantity>> timerSamples = new HashMap<>();
        timerSamples.put("timer", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Integer.valueOf(1))));
        final Map<String, List<Quantity>> counterSamples = new HashMap<>();
        counterSamples.put("counter", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        final Map<String, List<Quantity>> gaugeSamples = new HashMap<>();
        gaugeSamples.put("gauge", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        // CHECKSTYLE.ON: IllegalInstantiation

        Assert.assertEquals(
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(annotations)
                        .setTimerSamples(timerSamples)
                        .setCounterSamples(counterSamples)
                        .setGaugeSamples(gaugeSamples)
                        .build()
                        .hashCode(),
                new SinkVerticle.DefaultEvent.Builder()
                        .setAnnotations(annotations)
                        .setTimerSamples(timerSamples)
                        .setCounterSamples(counterSamples)
                        .setGaugeSamples(gaugeSamples)
                        .build()
                        .hashCode());
    }

    @Test
    public void testToString() {
        // CHECKSTYLE.OFF: IllegalInstantiation - No Guava
        final Map<String, String> annotations = new HashMap<>();
        annotations.put("foo", "bar");
        final Map<String, List<Quantity>> timerSamples = new HashMap<>();
        timerSamples.put("timer", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Integer.valueOf(1))));
        final Map<String, List<Quantity>> counterSamples = new HashMap<>();
        counterSamples.put("counter", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        final Map<String, List<Quantity>> gaugeSamples = new HashMap<>();
        gaugeSamples.put("gauge", Collections.<Quantity>singletonList(
                SinkVerticle.DefaultQuantity.newInstance(Double.valueOf(1.23))));
        // CHECKSTYLE.ON: IllegalInstantiation
        final String asString = new SinkVerticle.DefaultEvent.Builder()
                .setAnnotations(annotations)
                .setTimerSamples(timerSamples)
                .setCounterSamples(counterSamples)
                .setGaugeSamples(gaugeSamples)
                .build()
                .toString();

        Assert.assertNotNull(asString);
        Assert.assertFalse(asString.isEmpty());
    }
}
