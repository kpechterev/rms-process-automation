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

package com.pulkovo.rms.processautomation.apollo;

import java.math.BigInteger;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

@UtilityClass
@Slf4j
public class ApolloTracing {

    public static final String HEADER_TRACE_ID = "X-B3-TraceId";
    public static final String HEADER_SPAN_ID = "X-B3-SpanId";
    public static final String HEADER_PARENT_SPAN_ID = "X-B3-ParentSpanId";
    public static final String HEADER_SAMPLED = "X-B3-Sampled";

    public long requiredLong(@NonNull final String s) {
        final var res = optionalLong(s);
        return res == null ? 0 : res;
    }

    @Nullable
    public Long optionalLong(@Nullable final String s) {
        if (StringUtils.isNotBlank(s)) {
            try {
                return new BigInteger(s, 16).longValue();
            } catch (NumberFormatException e) {
                log.error("Bad long value: " + s, e);
            }
        }
        return null;
    }

    @Nullable
    public Boolean optionalBool(@Nullable final String s) {
        if (StringUtils.isNotBlank(s)) {
            try {
                final var res = Integer.parseInt(s);
                return res != 0;
            } catch (NumberFormatException e) {
                log.warn("Bad boolean value: {}", s);
            }
        }
        return null;
    }
}
