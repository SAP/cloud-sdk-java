/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.util.function.Function;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The MessageClass ERP Type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@EqualsAndHashCode
@Deprecated
public class MessageClass implements ErpType<MessageClass>
{
    private static final long serialVersionUID = -1292374106552874815L;

    /**
     * Statically created empty instance of this converter.
     */
    public static final MessageClass EMPTY = new MessageClass("");
    /**
     * "KI"
     */
    public static final MessageClass KI = new MessageClass("KI");
    /**
     * "BK"
     */
    public static final MessageClass BK = new MessageClass("BK");
    /**
     * "F5"
     */
    public static final MessageClass F5 = new MessageClass("F5");
    /**
     * "SG"
     */
    public static final MessageClass SG = new MessageClass("SG");
    /**
     * "RW"
     */
    public static final MessageClass RW = new MessageClass("RW");
    /**
     * "BM"
     */
    public static final MessageClass BM = new MessageClass("BM");
    /**
     * "KW"
     */
    public static final MessageClass KW = new MessageClass("KW");
    /**
     * "B1"
     */
    public static final MessageClass B1 = new MessageClass("B1");
    /**
     * "AA"
     */
    public static final MessageClass AA = new MessageClass("AA");
    /**
     * "KE"
     */
    public static final MessageClass KE = new MessageClass("KE");
    /**
     * "BAPI4499"
     */
    public static final MessageClass BAPI4499 = new MessageClass("BAPI4499");
    /**
     * "BAPI1022"
     */
    public static final MessageClass BAPI1022 = new MessageClass("BAPI1022");
    /**
     * "FDC_POSTING_001"
     */
    public static final MessageClass FDC_POSTING_001 = new MessageClass("FDC_POSTING_001");
    /**
     * "FAGL_LEDGER_CUST"
     */
    public static final MessageClass FAGL_LEDGER_CUST = new MessageClass("FAGL_LEDGER_CUST");
    /**
     * "CBN_ES_MESSAGE"
     */
    public static final MessageClass CBN_ES_MESSAGE = new MessageClass("CBN_ES_MESSAGE");

    @Getter
    @Nonnull
    private final String value;

    @Override
    @Nonnull
    public String toString()
    {
        return value;
    }

    @Nonnull
    @Override
    public ErpTypeConverter<MessageClass> getTypeConverter()
    {
        return MessageClassConverter.INSTANCE;
    }

    /**
     * String aggregator for iterable messages.
     *
     * @param messages
     *            The messages to be transformed.
     * @return An accumulated String representation of multiple MessageClass values.
     */
    @Nonnull
    public static String toString( @Nonnull final Iterable<RemoteFunctionMessage> messages )
    {
        final Function<RemoteFunctionMessage, String> function = ( message ) -> {
            if( message == null ) {
                return null;
            }
            return message.getMessageClass().toString();
        };

        return RemoteFunctionMessage.toString(messages, function);
    }
}
