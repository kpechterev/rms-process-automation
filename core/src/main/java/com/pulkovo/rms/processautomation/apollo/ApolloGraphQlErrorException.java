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

import com.apollographql.apollo.api.Error;
import com.pulkovo.rms.processautomation.exception.AbstractException;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ApolloGraphQlErrorException extends AbstractException {

   private final transient List<Error> errors;

    public ApolloGraphQlErrorException(@Nullable final List<Error> errors) {
        super("GraphQL request returned error(s): %s", errors);
        this.errors = errors;
    }

    public ApolloGraphQlErrorException(@NonNull final String message) {
        super(message);
        this.errors = Collections.emptyList();
    }
}
