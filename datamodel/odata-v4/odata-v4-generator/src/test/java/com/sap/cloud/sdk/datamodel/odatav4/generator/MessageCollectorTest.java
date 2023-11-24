/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class MessageCollectorTest
{
    @Test
    void testMessageCollector()
    {
        final Logger logger = MessageCollector.getLogger(MessageCollectorTest.class);
        logger.error("err without argument, without throwable");
        logger.error("err without argument, with throwable", new RuntimeException());
        logger.error("err with one argument {}, without throwable", 1);
        logger.error("err with two arguments {} {}, without throwable", 1, 2);
        logger.error("err with one argument {}, with throwable", 1, new RuntimeException());
        logger.error("err with array of arguments {} {} {}, with throwable", 1, 2, 3, new RuntimeException());
        logger.error("err with array of arguments {} {} {}, without throwable", 1, 2, 3);

        final List<String> errorMessages = MessageCollector.getErrorMessages();
        assertThat(errorMessages)
            .containsOnly(
                "err without argument, without throwable",
                "err without argument, with throwable",
                "err with one argument 1, without throwable",
                "err with two arguments 1 2, without throwable",
                "err with one argument 1, with throwable",
                "err with array of arguments 1 2 3, with throwable",
                "err with array of arguments 1 2 3, without throwable");
    }
}
