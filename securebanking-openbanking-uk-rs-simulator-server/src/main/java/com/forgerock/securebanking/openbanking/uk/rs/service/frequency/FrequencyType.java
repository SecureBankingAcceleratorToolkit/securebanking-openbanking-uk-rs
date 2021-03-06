/**
 * Copyright © 2020 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.securebanking.openbanking.uk.rs.service.frequency;

import java.util.regex.Pattern;

public enum FrequencyType {
    EVERYDAY("EvryDay"),
    EVERYWORKINGDAY("EvryWorkgDay"),
    INTERVALWEEKDAY("IntrvlWkDay", "0?([1-9]):0?([1-7])$"),
    WEEKINMONTHDAY("WkInMnthDay", "0?([1-5]):0?([1-7])$"),
    INTERVALMONTHDAY("IntrvlMnthDay", "(0?[1-6]|12|24):(-0?[1-5]|0?[1-9]|[12][0-9]|3[01])$"),
    QUARTERDAY("QtrDay", "(ENGLISH|SCOTTISH|RECEIVED)$"),
    INTERVALDAY("IntrvlDay", "(0?[2-9]|[1-2][0-9]|3[0-1])$");

    private String frequencyStr;
    private Pattern pattern;

    FrequencyType(String frequencyStr) {
        this.frequencyStr = frequencyStr;
        this.pattern = Pattern.compile(frequencyStr);
    }

    FrequencyType(String frequencyStr, String pattern) {
        this.frequencyStr = frequencyStr;
        this.pattern = Pattern.compile(frequencyStr + ":" + pattern);
    }

    public String getFrequencyStr() {
        return frequencyStr;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static FrequencyType fromFrequencyString(String freqStr) {
        for (FrequencyType frequencyType : FrequencyType.values()) {
            if (frequencyType.getFrequencyStr().equals(freqStr)) {
                return frequencyType;
            }
        }
        throw new IllegalArgumentException("Frequency type value not found: " + freqStr);
    }
}