/**
 * Copyright 2014 Groupon.com
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

import com.arpnetworking.metrics.Counter;
import com.arpnetworking.metrics.Metrics;
import com.arpnetworking.metrics.Timer;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tests the {@link SharedMetrics} class.
 *
 * @author Gil Markham (gil at groupon dot com)
 * @since 0.2.1
 */
public class SharedMetricsTest {

    @Before
    public void setUp() throws Exception {
        _mocks = MockitoAnnotations.openMocks(this);
        Mockito.when(_mockMetrics.createTimer(Mockito.any(String.class))).thenReturn(_mockTimer);
        Mockito.when(_mockMetrics.createCounter(Mockito.any(String.class))).thenReturn(_mockCounter);
        _sharedMetrics = new SharedMetrics(_mockMetrics);
    }

    @After
    public void tearDown() throws Exception {
        _mocks.close();
    }

    @Test
    public void testCreateCounter() throws Exception {
        final Counter counter = _sharedMetrics.createCounter("name");
        Assert.assertEquals(_mockCounter, counter);
        Mockito.verify(_mockMetrics, Mockito.times(1)).createCounter("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testIncrementCounter() throws Exception {
        _sharedMetrics.incrementCounter("name");
        Mockito.verify(_mockMetrics, Mockito.times(1)).incrementCounter("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testIncrementCounterWithValue() throws Exception {
        _sharedMetrics.incrementCounter("name", 1234L);
        Mockito.verify(_mockMetrics, Mockito.times(1)).incrementCounter("name", 1234L);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testDecrementCounter() throws Exception {
        _sharedMetrics.decrementCounter("name");
        Mockito.verify(_mockMetrics, Mockito.times(1)).decrementCounter("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testDecrementCounterWithValue() throws Exception {
        _sharedMetrics.decrementCounter("name", 1234L);
        Mockito.verify(_mockMetrics, Mockito.times(1)).decrementCounter("name", 1234L);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testResetCounter() throws Exception {
        _sharedMetrics.resetCounter("name");
        Mockito.verify(_mockMetrics, Mockito.times(1)).resetCounter("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testCreateTimer() throws Exception {
        final Timer timer = _sharedMetrics.createTimer("name");
        Assert.assertEquals(timer, _mockTimer);
        Mockito.verify(_mockMetrics, Mockito.times(1)).createTimer("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testStartTimer() throws Exception {
        _sharedMetrics.startTimer("name");
        Mockito.verify(_mockMetrics, Mockito.times(1)).startTimer("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testStopTimer() throws Exception {
        _sharedMetrics.stopTimer("name");
        Mockito.verify(_mockMetrics, Mockito.times(1)).stopTimer("name");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetTimerTimeUnit() throws Exception {
        _sharedMetrics.setTimer("name", 1234L, TimeUnit.MILLISECONDS);
        Mockito.verify(_mockMetrics, Mockito.times(1)).setTimer("name", 1234L, TimeUnit.MILLISECONDS);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }


    @Test
    public void testSetGaugeLong() throws Exception {
        _sharedMetrics.setGauge("name", 1234L);
        Mockito.verify(_mockMetrics, Mockito.times(1)).setGauge("name", 1234L);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }


    @Test
    public void testSetGaugeDouble() throws Exception {
        _sharedMetrics.setGauge("name", 1.2D);
        Mockito.verify(_mockMetrics, Mockito.times(1)).setGauge("name", 1.2D);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }


    @Test
    public void testAddAnnotation() throws Exception {
        _sharedMetrics.addAnnotation("key", "value");
        Mockito.verify(_mockMetrics, Mockito.times(1)).addAnnotation("key", "value");
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testAddAnnotations() throws Exception {
        final Map<String, String> annotations = ImmutableMap.of("key1", "value1", "key2", "value2");
        _sharedMetrics.addAnnotations(annotations);
        Mockito.verify(_mockMetrics, Mockito.times(1)).addAnnotations(annotations);
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testIsOpen() throws Exception {
        _sharedMetrics.isOpen();
        Mockito.verify(_mockMetrics, Mockito.times(1)).isOpen();
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testClose() throws Exception {
        _sharedMetrics.close();
        Mockito.verify(_mockMetrics, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testGetOpenTime() throws Exception {
        _sharedMetrics.getOpenTime();
        Mockito.verify(_mockMetrics, Mockito.times(1)).getOpenTime();
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Test
    public void testGetCloseTime() throws Exception {
        _sharedMetrics.getCloseTime();
        Mockito.verify(_mockMetrics, Mockito.times(1)).getCloseTime();
        Mockito.verifyNoMoreInteractions(_mockMetrics);
    }

    @Mock
    private Metrics _mockMetrics;
    @Mock
    private Timer _mockTimer;
    @Mock
    private Counter _mockCounter;
    private SharedMetrics _sharedMetrics;
    private AutoCloseable _mocks;
}
