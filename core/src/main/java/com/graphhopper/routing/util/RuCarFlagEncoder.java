/*
 * Copyright 2016 slavb.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.util.PMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author slavb
 */
public class RuCarFlagEncoder extends CarFlagEncoder {
    protected final Map<String, Integer> surfaceSpeedMap = new HashMap<String, Integer>();

    public RuCarFlagEncoder() {
        this(5, 5, 0);
    }

    public RuCarFlagEncoder(PMap properties) {
        this(
                (int) properties.getLong("speed_bits", 5),
                properties.getDouble("speed_factor", 5),
                properties.getBool("turn_costs", false) ? 1 : 0
        );
        this.properties = properties;
        this.setBlockFords(properties.getBool("block_fords", true));
    }

    public RuCarFlagEncoder(String propertiesStr) {
        this(new PMap(propertiesStr));
    }

    public RuCarFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        super(speedBits, speedFactor, maxTurnCosts);
        badSurfaceSpeedMap.add("unpaved");
        badSurfaceSpeedMap.add("compacted");

        surfaceSpeedMap.put("asphalt", 50);
        surfaceSpeedMap.put("concrete:plates", 50);

    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected double getSpeed(ReaderWay way) {
        double speed = super.getSpeed(way);
        String surface = way.getTag("surface");
        String highway = way.getTag("highway");
        if ("tertiary".equals(highway) || "unclassified".equals(highway)) {
            if (surfaceSpeedMap.containsKey(surface)) {
                speed = surfaceSpeedMap.get(surface);
            }else {
                speed = 20;
            }
        }
        // limit speed to max 20 km/h if bad surface
        if (speed > 20 && way.hasTag("surface", badSurfaceSpeedMap)) {
            speed = 20;
        }

        return speed;
    }

    @Override
    public String toString() {
        return "rucar";
    }

}
