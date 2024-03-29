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

import com.arpnetworking.metrics.Metrics;
import com.arpnetworking.metrics.MetricsFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Tests {@link SharedMetricsFactory}.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
public final class SharedMetricsFactoryTest {

    @Before
    public void setUp() {
        _mocks = MockitoAnnotations.openMocks(this);
        Mockito.doReturn(_mockMetrics).when(_mockFactory).create();
        _sharedMetricsFactory = new SharedMetricsFactory(_mockFactory);
    }

    @After
    public void tearDown() throws Exception {
        _mocks.close();
    }

    @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "Itis what we're testing")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullWrapper() {
        new SharedMetricsFactory(null);
    }

    @Test
    public void testCreateMetrics() {
        Assert.assertEquals(_mockMetrics, _sharedMetricsFactory.create());
        Mockito.verify(_mockFactory).create();
    }

    @Mock
    private MetricsFactory _mockFactory;
    @Mock
    private Metrics _mockMetrics;
    private AutoCloseable _mocks;
    private SharedMetricsFactory _sharedMetricsFactory = null;
}
