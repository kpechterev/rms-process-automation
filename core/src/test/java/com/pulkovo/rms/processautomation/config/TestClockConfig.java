/*
 *  =======================================================================
 *
 *  Copyright (c) 2021 Northern Capital Gateway, LLC. All rights reserved.
 *
 *  This software is the confidential and proprietary information of
 *  Northern Capital Gateway, LLC.
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with
 *  Northern Capital Gateway, LLC
 *
 *  =======================================================================
 */

package com.pulkovo.rms.processautomation.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.NonNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestClockConfig {

    private static volatile Clock instance = Clock.systemDefaultZone();

    /**
     * Return a proxy to this.instant. This allows an early injection.
     */
    @Primary
    @Bean
    public Clock testClock() {
        return new Clock() {
            @Override
            public ZoneId getZone() {
                return instance.getZone();
            }

            @Override
            public Clock withZone(ZoneId zoneId) {
                return instance.withZone(zoneId);
            }

            @Override
            public Instant instant() {
                return instance.instant();
            }

            @Override
            public long millis() {
                return instance.millis();
            }

            @Override
            public boolean equals(Object obj) {
                return obj != null && instance.getClass().equals(obj.getClass()) && instance.equals(obj);
            }

            @Override
            public int hashCode() {
                return instance.hashCode();
            }
        };
    }

    public static void setClock(@NonNull final Clock c) {
        TestClockConfig.instance = c;
    }
}
