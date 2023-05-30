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

/**
 * Test implementation of {@link Quantity} interface.
 *
 * @author Deepika Misra (deepika at groupon dot com)
 */
public final class TestQuantityImpl implements Quantity {

    @Override
    public Number getValue() {
        return _value;
    }


    private TestQuantityImpl(final Builder builder) {
        _value = builder._value;
    }

    private final Number _value;

    /**
     * Builder implementation of {@link TestQuantityImpl}.
     */
    public static final class Builder {

        /**
         * Builds an instance of {@link TestQuantityImpl}.
         *
         * @return A new instance of {@link TestQuantityImpl}.
         */
        public TestQuantityImpl build() {
            return new TestQuantityImpl(this);
        }

        /**
         * Sets the value attribute.
         *
         * @param value An instance of {@link Number}.
         * @return This instance of {@link Builder}.
         */
        public Builder setValue(final Number value) {
            _value = value;
            return this;
        }

        private Number _value;
    }
}
