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
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for <code>SinkVerticle.DefaultQuantity</code>.
 *
 * @author Ville Koskela (vkoskela at groupon dot com)
 */
public class DefaultQuantityTest {

    @Test
    public void testQuantity() {
        final Long expectedValue = Long.valueOf(1);
        final Unit expectedUnit = Units.BYTE;
        final Quantity q = SinkVerticle.DefaultQuantity.newInstance(expectedValue, expectedUnit);
        Assert.assertEquals(expectedValue, q.getValue());
        Assert.assertEquals(expectedUnit, q.getUnit());
    }

    @Test
    public void testEquals() {
        final Quantity quantity = SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE);
        Assert.assertTrue(quantity.equals(quantity));

        Assert.assertTrue(
                SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE).equals(
                        SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE)));

        Assert.assertFalse(quantity.equals(null));
        Assert.assertFalse(quantity.equals("This is a String"));

        final Quantity differentQuantity1 = SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BIT);
        final Quantity differentQuantity2 = SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(2), Units.BYTE);

        Assert.assertFalse(quantity.equals(differentQuantity1));
        Assert.assertFalse(quantity.equals(differentQuantity2));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(
                SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE).hashCode(),
                SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE).hashCode());
    }

    @Test
    public void testToString() {
        final String asString = SinkVerticle.DefaultQuantity.newInstance(Long.valueOf(1), Units.BYTE).toString();
        Assert.assertNotNull(asString);
        Assert.assertFalse(asString.isEmpty());
    }
}
