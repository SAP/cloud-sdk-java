/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

@Deprecated
class MessageTypeTest
{
    @Test
    void testOfIdentifier()
    {
        assertThat(com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("S"))
            .isEqualTo(com.sap.cloud.sdk.s4hana.serialization.MessageType.SUCCESS);
        assertThat(com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("I"))
            .isEqualTo(com.sap.cloud.sdk.s4hana.serialization.MessageType.INFORMATION);
        assertThat(com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("W"))
            .isEqualTo(com.sap.cloud.sdk.s4hana.serialization.MessageType.WARNING);
        assertThat(com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("E"))
            .isEqualTo(com.sap.cloud.sdk.s4hana.serialization.MessageType.ERROR);
        assertThat(com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("A"))
            .isEqualTo(com.sap.cloud.sdk.s4hana.serialization.MessageType.ABORT);
        assertThatThrownBy(() -> com.sap.cloud.sdk.s4hana.serialization.MessageType.ofIdentifier("wrong"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
