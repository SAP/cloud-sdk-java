package com.sap.cloud.sdk.s4hana.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

@Deprecated
public class StringBasedErpTypeTest
{
    static class DummyType extends StringBasedErpType<DummyType>
    {
        private static final long serialVersionUID = -1;

        public DummyType( final String value ) throws IllegalArgumentException
        {
            super(value);
        }

        public DummyType( final String value, final CharCasing charCasing ) throws IllegalArgumentException
        {
            super(value, charCasing);
        }

        @Nonnull
        @Override
        public ErpTypeConverter<DummyType> getTypeConverter()
        {
            return new AbstractErpTypeConverter<DummyType>()
            {
                @Nonnull
                @Override
                public Class<DummyType> getType()
                {
                    return DummyType.class;
                }

                @Nonnull
                @Override
                public ConvertedObject<String> toDomainNonNull( @Nonnull final DummyType object )
                {
                    return ConvertedObject.of(object.toString());
                }

                @Nonnull
                @Override
                public ConvertedObject<DummyType> fromDomainNonNull( @Nonnull final String domainObject )
                {
                    try {
                        return ConvertedObject.of(new DummyType(domainObject));
                    }
                    catch( final IllegalArgumentException e ) {
                        return ConvertedObject.ofNotConvertible();
                    }
                }
            };
        }

        @Nonnull
        @Override
        public Class<DummyType> getType()
        {
            return DummyType.class;
        }

        @Override
        public int getMaxLength()
        {
            return 5;
        }

        @Nonnull
        @Override
        public FillCharStrategy getFillCharStrategy()
        {
            return FillCharStrategy.DO_NOTHING;
        }
    }

    static class DummyTypeFillLeading extends StringBasedErpType<DummyTypeFillLeading>
    {
        private static final long serialVersionUID = -1;

        public DummyTypeFillLeading( final String value ) throws IllegalArgumentException
        {
            super(value);
        }

        @Nonnull
        @Override
        public ErpTypeConverter<DummyTypeFillLeading> getTypeConverter()
        {
            return new AbstractErpTypeConverter<DummyTypeFillLeading>()
            {
                @Nonnull
                @Override
                public Class<DummyTypeFillLeading> getType()
                {
                    return DummyTypeFillLeading.class;
                }

                @Nonnull
                @Override
                public ConvertedObject<String> toDomainNonNull( @Nonnull final DummyTypeFillLeading object )
                {
                    return ConvertedObject.of(object.toString());
                }

                @Nonnull
                @Override
                public ConvertedObject<DummyTypeFillLeading> fromDomainNonNull( @Nonnull final String domainObject )
                {
                    return ConvertedObject.of(new DummyTypeFillLeading(domainObject));
                }
            };
        }

        @Nonnull
        @Override
        public Class<DummyTypeFillLeading> getType()
        {
            return DummyTypeFillLeading.class;
        }

        @Override
        public int getMaxLength()
        {
            return 5;
        }

        @Nonnull
        @Override
        public FillCharStrategy getFillCharStrategy()
        {
            return FillCharStrategy.FILL_LEADING;
        }
    }

    static class DummyTypeStripLeading extends StringBasedErpType<DummyTypeStripLeading>
    {
        private static class DummyTypeStripLeadingErpTypeConverter
            extends
            AbstractErpTypeConverter<DummyTypeStripLeading>
        {
            @Nonnull
            @Override
            public Class<DummyTypeStripLeading> getType()
            {
                return DummyTypeStripLeading.class;
            }

            @Nonnull
            @Override
            public ConvertedObject<String> toDomainNonNull( @Nonnull final DummyTypeStripLeading object )
            {
                return ConvertedObject.of(object.toString());
            }

            @Nonnull
            @Override
            public ConvertedObject<DummyTypeStripLeading> fromDomainNonNull( @Nonnull final String domainObject )
            {
                try {
                    return ConvertedObject.of(new DummyTypeStripLeading(domainObject));
                }
                catch( final IllegalArgumentException e ) {
                    return ConvertedObject.ofNotConvertible();
                }
            }
        }

        private static final long serialVersionUID = -1;

        public DummyTypeStripLeading( final String value ) throws IllegalArgumentException
        {
            super(value);
        }

        @Nonnull
        @Override
        public ErpTypeConverter<DummyTypeStripLeading> getTypeConverter()
        {
            return new DummyTypeStripLeadingErpTypeConverter();
        }

        @Nonnull
        @Override
        public Class<DummyTypeStripLeading> getType()
        {
            return DummyTypeStripLeading.class;
        }

        @Override
        public int getMaxLength()
        {
            return 5;
        }

        @Nonnull
        @Override
        public FillCharStrategy getFillCharStrategy()
        {
            return FillCharStrategy.STRIP_LEADING;
        }
    }

    @Test
    public void testFillChar()
    {
        {
            final DummyType dummy = new DummyType("bla");
            assertThat(dummy.toString()).isEqualTo("bla");
        }

        {
            final DummyType dummy = new DummyType("00bla");
            assertThat(dummy.toString()).isEqualTo("00bla");
        }

        {
            final DummyTypeFillLeading dummy = new DummyTypeFillLeading("bla");
            assertThat(dummy.toString()).isEqualTo("00bla");
        }

        {
            final DummyTypeStripLeading dummy = new DummyTypeStripLeading("00bla");
            assertThat(dummy.toString()).isEqualTo("bla");
        }
    }

    @Test
    public void testEmpty()
    {
        {
            final DummyType dummy = new DummyType("");
            assertThat(dummy.toString().isEmpty()).isTrue();
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNull()
    {
        new DummyType(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testTooLong()
    {
        new DummyType("123456");
    }

    @Test
    public void testConverters()
    {
        assertThat(new InvertedLocalDateConverter().fromDomain("79839488").get().getDate())
            .isEqualTo(LocalDate.of(2016, 5, 11));
        assertThat(new InvertedLocalDateConverter().fromDomain("79839488").get())
            .isEqualTo(new InvertedLocalDate(2016, 5, 11));
        assertThat(new InvertedLocalDateConverter().toDomain(new InvertedLocalDate(2016, 12, 22)).get())
            .isEqualTo("79838777");
    }

    @Test
    public void testCharCasing()
    {
        {
            final String stringWithSomeCasing = "sWsC";
            final DummyType sut = new DummyType(stringWithSomeCasing, StringBasedErpType.CharCasing.DO_NOTHING);
            assertThat(sut.getValue()).isEqualTo(stringWithSomeCasing);
        }

        final Locale oldDefault = Locale.getDefault();
        try {
            final Locale turkishLocale = Locale.forLanguageTag("tr-TR");
            Locale.setDefault(turkishLocale);

            {
                // in the turkish locale the capital letter 'I' is lower-cases as an 'i' without a dot above it
                // Unicode: [capital] LATIN CAPITAL LETTER I -> [small] LATIN SMALL LETTER DOTLESS I
                final DummyType sut = new DummyType("TITLE", StringBasedErpType.CharCasing.LOWER_CASE);
                assertThat(sut.getValue()).isEqualTo("title");
            }

            {
                // in the turkish local the small letter 'i' is upper-cased as an 'I' with a dot above
                // Unicode: [small] LATIN SMALL LETTER I -> [capital] LATIN CAPITAL LETTER I WITH DOT ABOVE
                final DummyType sut = new DummyType("title", StringBasedErpType.CharCasing.UPPER_CASE);
                assertThat(sut.getValue()).isEqualTo("TITLE");
            }
        }
        finally {
            Locale.setDefault(oldDefault);
        }
    }
}
