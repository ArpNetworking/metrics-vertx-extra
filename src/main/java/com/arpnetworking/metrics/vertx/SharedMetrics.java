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
import io.vertx.core.shareddata.Shareable;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/**
 * Metrics object that extends Vertx's {@link io.vertx.core.shareddata.SharedData} object which allows use in a shared data map.
 *
 * @author Gil Markham (gil at groupon dot com)
 * @since 0.2.1
 */
public class SharedMetrics implements Metrics, Shareable {
    /**
     *  Constructs a new SharedMetrics object that can be added to a vertx shared data map/set.
     *  @param wrappedMetrics - Metrics object to wrap.
     */
    public SharedMetrics(final Metrics wrappedMetrics) {
        _wrappedMetrics = wrappedMetrics;
    }

    @Override
    public Counter createCounter(final String name) {
        return _wrappedMetrics.createCounter(name);
    }

    @Override
    public void incrementCounter(final String name) {
        _wrappedMetrics.incrementCounter(name);
    }

    @Override
    public void incrementCounter(final String name, final long value) {
        _wrappedMetrics.incrementCounter(name, value);
    }

    @Override
    public void decrementCounter(final String name) {
        _wrappedMetrics.decrementCounter(name);
    }

    @Override
    public void decrementCounter(final String name, final long value) {
        _wrappedMetrics.decrementCounter(name, value);
    }

    @Override
    public void resetCounter(final String name) {
        _wrappedMetrics.resetCounter(name);
    }

    @Override
    public Timer createTimer(final String name) {
        return _wrappedMetrics.createTimer(name);
    }

    @Override
    public void startTimer(final String name) {
        _wrappedMetrics.startTimer(name);
    }

    @Override
    public void stopTimer(final String name) {
        _wrappedMetrics.stopTimer(name);
    }

    /**
     * @deprecated See {@code Metrics} interface.
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void setTimer(final String name, final long duration, final TimeUnit unit) {
        _wrappedMetrics.setTimer(name, duration, unit);
    }

    @Override
    public void setGauge(final String name, final double value) {
        _wrappedMetrics.setGauge(name, value);
    }

    @Override
    public void setGauge(final String name, final long value) {
        _wrappedMetrics.setGauge(name, value);
    }

    @Override
    public void addAnnotation(final String key, final String value) {
        _wrappedMetrics.addAnnotation(key, value);
    }

    @Override
    public void addAnnotations(final Map<String, String> map) {
        _wrappedMetrics.addAnnotations(map);
    }

    @Override
    public boolean isOpen() {
        return _wrappedMetrics.isOpen();
    }

    @Override
    public void close() {
        _wrappedMetrics.close();
    }

    @Override
    @Nullable
    public Instant getOpenTime() {
        return _wrappedMetrics.getOpenTime();
    }

    @Override
    @Nullable
    public Instant getCloseTime() {
        return _wrappedMetrics.getCloseTime();
    }

    private final Metrics _wrappedMetrics;
}
