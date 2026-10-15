package com.sap.cloud.sdk.datamodel.odatav4.expression;

import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

import lombok.Getter;

class FilterableOperandsTest
{
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        private static final SimpleProperty.Boolean<TestEntity> ALIVE =
            new SimpleProperty.Boolean<>(TestEntity.class, "Alive");
        private static final SimpleProperty.Enum<TestEntity, Language> PROGRAMMING_LANGUAGE =
            new SimpleProperty.Enum<>(TestEntity.class, "ProgrammingLanguage", "SoftwareEngineer.Programming.Language");

        @Getter
        private final String entityCollection = "TestEntities";
        @Getter
        private final String odataType = "Odata.TestEntity";
        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        enum Language implements VdmEnum
        {
            Java;
        }
    }

    private static class TestComputer extends VdmComplex<TestComputer>
    {
        @Getter
        private final String odataType = "Odata.TestComputer";
        @Getter
        private final Class<TestComputer> type = TestComputer.class;
    }

    @Test
    void testFilterableBoolean()
    {
        final SimpleProperty.Boolean<TestEntity> f1 = new SimpleProperty.Boolean<>(TestEntity.class, "Field1");
        final String expression = f1.and(true).and(f1).not().or(true).or(f1).getExpression(V4);
        assertThat(expression).isEqualTo("(((not ((Field1 and true) and Field1)) or true) or Field1)");
    }

    @Test
    void testFilterableCollection()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.Collection<TestEntity, Integer> telephone =
            new SimpleProperty.Collection<>(TestEntity.class, "TelephoneNumber", Integer.class);
        final SimpleProperty.Collection<TestEntity, Integer> areaCode =
            new SimpleProperty.Collection<>(TestEntity.class, "AreaCode", Integer.class);
        final NavigationProperty.Collection<TestEntity, TestEntity> friends =
            new NavigationProperty.Collection<>(TestEntity.class, "Friends", TestEntity.class);

        {
            final String expression = friends.all(TestEntity.ALIVE.equalTo(true)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("Friends/all(a:(a/Alive eq true))");
        }
        {
            final String expression = friends.any(TestEntity.ALIVE.equalTo(true)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("Friends/any(a:(a/Alive eq true))");
        }
        {
            final String expression = areaCode.concat(telephone).concat(Arrays.asList(4, 2)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("concat(concat(AreaCode,TelephoneNumber),[4,2])");
        }
        {
            final String expression = areaCode.substring(1).substring(2, 3).getExpression(V4);
            softly.assertThat(expression).isEqualTo("substring(substring(AreaCode,1),2,3)");
        }
        {
            final String expression = telephone.contains(areaCode).getExpression(V4);
            softly.assertThat(expression).isEqualTo("contains(TelephoneNumber,AreaCode)");
        }
        {
            final String expression = telephone.contains(Arrays.asList(0, 3, 0)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("contains(TelephoneNumber,[0,3,0])");
        }
        {
            final String expression = telephone.endsWith(Arrays.asList(4, 2)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("endswith(TelephoneNumber,[4,2])");
        }
        {
            final String expression = telephone.endsWith(telephone).getExpression(V4);
            softly.assertThat(expression).isEqualTo("endswith(TelephoneNumber,TelephoneNumber)");
        }
        {
            final String expression = telephone.startsWith(Arrays.asList(0, 3, 0)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("startswith(TelephoneNumber,[0,3,0])");
        }
        {
            final String expression = telephone.startsWith(telephone).getExpression(V4);
            softly.assertThat(expression).isEqualTo("startswith(TelephoneNumber,TelephoneNumber)");
        }
        {
            final String expression = telephone.hasSubSequence(areaCode).getExpression(V4);
            softly.assertThat(expression).isEqualTo("hassubsequence(TelephoneNumber,AreaCode)");
        }
        {
            final String expression = telephone.hasSubSequence(Arrays.asList(0, 3, 0)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("hassubsequence(TelephoneNumber,[0,3,0])");
        }
        {
            final String expression = telephone.hasSubset(areaCode).getExpression(V4);
            softly.assertThat(expression).isEqualTo("hassubset(TelephoneNumber,AreaCode)");
        }
        {
            final String expression = telephone.hasSubset(Arrays.asList(0, 3, 0)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("hassubset(TelephoneNumber,[0,3,0])");
        }
        {
            final String expression = telephone.indexOf(areaCode).getExpression(V4);
            softly.assertThat(expression).isEqualTo("indexof(TelephoneNumber,AreaCode)");
        }
        {
            final String expression = telephone.indexOf(Arrays.asList(0, 3, 0)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("indexof(TelephoneNumber,[0,3,0])");
        }
        {
            final String expression = telephone.length().getExpression(V4);
            softly.assertThat(expression).isEqualTo("length(TelephoneNumber)");
        }
        softly.assertAll();
    }

    @Test
    void testFilterableComplex()
    {
        final ComplexProperty.Single<TestEntity, TestComputer> f1 =
            new ComplexProperty.Single<>(TestEntity.class, "OperatingSystem", TestComputer.class);
        final String expression1 = f1.has("Debian").getExpression(V4);
        assertThat(expression1).isEqualTo("(OperatingSystem has 'Debian')");

        final ComplexProperty.Single<TestEntity, TestComputer> f2 =
            new ComplexProperty.Single<>(TestEntity.class, "Apartment", TestComputer.class);
        final SimpleProperty.Enum<TestEntity, TestEntity.Language> enumValue =
            new SimpleProperty.Enum<>(TestEntity.class, "Animal", "OData.Test.AnimalType");
        final String expression2 = f2.has(enumValue).getExpression(V4);
        assertThat(expression2).isEqualTo("(Apartment has Animal)");

        assertThat(f1.equalToNull().getExpression(V4)).isEqualTo("(OperatingSystem eq null)");
        assertThat(f1.notEqualToNull().getExpression(V4)).isEqualTo("(OperatingSystem ne null)");
    }

    @Test
    void testFilterableTime()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.Time<TestEntity> time = new SimpleProperty.Time<>(TestEntity.class, "Registration");
        softly.assertThat(time.timeFractionalSeconds().getExpression(V4)).isEqualTo("fractionalseconds(Registration)");
        softly.assertThat(time.timeSecond().getExpression(V4)).isEqualTo("second(Registration)");
        softly.assertThat(time.timeMinute().getExpression(V4)).isEqualTo("minute(Registration)");
        softly.assertThat(time.timeHour().getExpression(V4)).isEqualTo("hour(Registration)");
        softly.assertAll();
    }

    @Test
    void testFilterableDate()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.Date<TestEntity> date = new SimpleProperty.Date<>(TestEntity.class, "Registration");
        final SimpleProperty.Duration<TestEntity> duration =
            new SimpleProperty.Duration<>(TestEntity.class, "ResponseTime");
        {
            final String expression =
                date
                    .add(Duration.ofDays(1))
                    .subtract(Duration.ofDays(2))
                    .difference(LocalDate.of(2001, 1, 1))
                    .getExpression(V4);
            softly
                .assertThat(expression)
                .isEqualTo("(((Registration add duration'PT24H') sub duration'PT48H') sub 2001-01-01)");
        }
        {
            final String expression = date.add(duration).subtract(duration).difference(date).getExpression(V4);
            softly
                .assertThat(expression)
                .isEqualTo("(((Registration add ResponseTime) sub ResponseTime) sub Registration)");
        }
        softly.assertThat(date.dateDay().getExpression(V4)).isEqualTo("day(Registration)");
        softly.assertThat(date.dateMonth().getExpression(V4)).isEqualTo("month(Registration)");
        softly.assertThat(date.dateYear().getExpression(V4)).isEqualTo("year(Registration)");
        softly.assertAll();
    }

    @Test
    void testFilterableDateTime()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.DateTime<TestEntity> dt = new SimpleProperty.DateTime<>(TestEntity.class, "Registration");
        final SimpleProperty.Duration<TestEntity> duration =
            new SimpleProperty.Duration<>(TestEntity.class, "ResponseTime");

        {
            final String expression = dt.add(Duration.ofDays(1)).subtract(Duration.ofDays(2)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Registration add duration'PT24H') sub duration'PT48H')");
        }
        {
            final String expression = dt.add(duration).subtract(duration).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Registration add ResponseTime) sub ResponseTime)");
        }
        softly.assertThat(dt.date().getExpression(V4)).isEqualTo("date(Registration)");
        softly.assertThat(dt.time().getExpression(V4)).isEqualTo("time(Registration)");
        softly.assertThat(dt.timeFractionalSeconds().getExpression(V4)).isEqualTo("fractionalseconds(Registration)");
        softly.assertThat(dt.timeHour().getExpression(V4)).isEqualTo("hour(Registration)");
        softly.assertThat(dt.timeMinute().getExpression(V4)).isEqualTo("minute(Registration)");
        softly.assertThat(dt.timeSecond().getExpression(V4)).isEqualTo("second(Registration)");
        softly.assertThat(dt.dateDay().getExpression(V4)).isEqualTo("day(Registration)");
        softly.assertThat(dt.dateMonth().getExpression(V4)).isEqualTo("month(Registration)");
        softly.assertThat(dt.dateYear().getExpression(V4)).isEqualTo("year(Registration)");
        softly.assertThat(dt.offsetMinutes().getExpression(V4)).isEqualTo("totaloffsetminutes(Registration)");
        softly.assertAll();
    }

    @Test
    void testFilterableDuration()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.Duration<TestEntity> duration =
            new SimpleProperty.Duration<>(TestEntity.class, "ResponseTime");
        final SimpleProperty.NumericDecimal<TestEntity> number =
            new SimpleProperty.NumericDecimal<>(TestEntity.class, "Size");
        {
            final String expression = duration.add(duration).subtract(duration).negate().getExpression(V4);
            softly.assertThat(expression).isEqualTo("-(((ResponseTime add ResponseTime) sub ResponseTime))");
        }
        {
            final String expression = duration.add(Duration.ofDays(1)).subtract(Duration.ofDays(2)).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((ResponseTime add duration'PT24H') sub duration'PT48H')");
        }
        {
            final String expression = duration.divide(2).multiply(3).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((ResponseTime div 2) mul 3)");
        }
        {
            final String expression = duration.divide(number).multiply(number).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((ResponseTime div Size) mul Size)");
        }
        softly.assertThat(duration.offsetSeconds().getExpression(V4)).isEqualTo("totaloffsetseconds(ResponseTime)");
        softly.assertAll();
    }

    @Test
    void testFilterableNumberApiInteger()
    {
        final SimpleProperty.NumericInteger<TestEntity> number =
            new SimpleProperty.NumericInteger<>(TestEntity.class, "Age");
        final SoftAssertions softly = new SoftAssertions();

        // internal API tests
        {
            final FilterExpression delegate = mock(FilterExpression.class);
            final FilterableNumericInteger.Expression<Object> expression =
                new FilterableNumericInteger.Expression<>(delegate, Object.class);
            softly.assertThat(expression.getDelegate()).isEqualTo(delegate);
            softly.assertThat(expression.getEntityType()).isEqualTo(Object.class);
        }

        // type safety for NumericInteger functions
        Lists
            .<Function<FilterableNumericInteger<?>, FilterableNumeric<?>>> newArrayList(FilterableNumericInteger::negate

            )
            .forEach(f -> softly.assertThat(f.apply(number)).isInstanceOf(FilterableNumericInteger.class));

        // type safety for NumericInteger functions and Long argument
        Lists
            .<BiFunction<FilterableNumericInteger<?>, Long, FilterableNumeric<?>>> newArrayList(
                FilterableNumericInteger::add,
                FilterableNumericInteger::subtract,
                FilterableNumericInteger::multiply,
                FilterableNumericInteger::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, 42L))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericInteger.class));

        // type safety for NumericInteger functions and Integer argument
        Lists
            .<BiFunction<FilterableNumericInteger<?>, Integer, FilterableNumeric<?>>> newArrayList(
                FilterableNumericInteger::add,
                FilterableNumericInteger::subtract,
                FilterableNumericInteger::multiply,
                FilterableNumericInteger::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, 42))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericInteger.class));

        // type safety for NumericInteger functions and Decimal argument
        Lists
            .<BiFunction<FilterableNumericInteger<?>, Number, FilterableNumeric<?>>> newArrayList(
                FilterableNumericInteger::add,
                FilterableNumericInteger::subtract,
                FilterableNumericInteger::multiply,
                FilterableNumericInteger::divide,
                FilterableNumericInteger::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, Math.PI))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericDecimal.class));

        softly.assertAll();
    }

    @Test
    void testFilterableNumberApiDecimal()
    {
        final SimpleProperty.NumericDecimal<TestEntity> number =
            new SimpleProperty.NumericDecimal<>(TestEntity.class, "Size");
        final SoftAssertions softly = new SoftAssertions();

        // internal API tests
        {
            final FilterExpression delegate = mock(FilterExpression.class);
            final FilterableNumericDecimal.Expression<Object> expression =
                new FilterableNumericDecimal.Expression<>(delegate, Object.class);
            softly.assertThat(expression.getDelegate()).isEqualTo(delegate);
            softly.assertThat(expression.getEntityType()).isEqualTo(Object.class);
        }

        // type safety for NumericDecimal functions
        Lists
            .<Function<FilterableNumericDecimal<?>, FilterableNumeric<?>>> newArrayList(FilterableNumericDecimal::negate

            )
            .forEach(f -> softly.assertThat(f.apply(number)).isInstanceOf(FilterableNumericDecimal.class));

        // type safety for NumericDecimal functions
        Lists
            .<Function<FilterableNumericDecimal<?>, FilterableNumeric<?>>> newArrayList(
                FilterableNumericDecimal::ceil,
                FilterableNumericDecimal::floor,
                FilterableNumericDecimal::round

            )
            .forEach(f -> softly.assertThat(f.apply(number)).isInstanceOf(FilterableNumericInteger.class));

        // type safety for NumericDecimal functions and Long argument
        Lists
            .<BiFunction<FilterableNumericDecimal<?>, Long, FilterableNumeric<?>>> newArrayList(
                FilterableNumericDecimal::add,
                FilterableNumericDecimal::subtract,
                FilterableNumericDecimal::multiply,
                FilterableNumericDecimal::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, 42L))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericDecimal.class));

        // type safety for NumericDecimal functions and Integer argument
        Lists
            .<BiFunction<FilterableNumericDecimal<?>, Integer, FilterableNumeric<?>>> newArrayList(
                FilterableNumericDecimal::add,
                FilterableNumericDecimal::subtract,
                FilterableNumericDecimal::multiply,
                FilterableNumericDecimal::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, 42))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericDecimal.class));

        // type safety for NumericDecimal functions and Decimal argument
        Lists
            .<BiFunction<FilterableNumericDecimal<?>, Number, FilterableNumeric<?>>> newArrayList(
                FilterableNumericDecimal::add,
                FilterableNumericDecimal::subtract,
                FilterableNumericDecimal::multiply,
                FilterableNumericDecimal::divide,
                FilterableNumericDecimal::modulo)
            .forEach(
                f -> softly
                    .assertThat(f.apply(number, Math.PI))
                    .describedAs(f.toString())
                    .isInstanceOf(FilterableNumericDecimal.class));

        softly.assertAll();
    }

    @Test
    void testFilterableNumber()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.NumericInteger<TestEntity> number =
            new SimpleProperty.NumericInteger<>(TestEntity.class, "Age");
        final SimpleProperty.NumericDecimal<TestEntity> size =
            new SimpleProperty.NumericDecimal<>(TestEntity.class, "Size");
        {
            final String expression = number.add(number).subtract(number).negate().getExpression(V4);
            softly.assertThat(expression).isEqualTo("-(((Age add Age) sub Age))");
        }
        {
            final String expression = size.add(size).subtract(size).negate().getExpression(V4);
            softly.assertThat(expression).isEqualTo("-(((Size add Size) sub Size))");
        }
        {
            final String expression = number.add(1).subtract(2).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Age add 1) sub 2)");
        }
        {
            final String expression = number.multiply(number).divide(number).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Age mul Age) divby Age)");
        }
        {
            final String expression = size.multiply(size).divide(size).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Size mul Size) divby Size)");
        }
        {
            final String expression = number.multiply(1).divide(2).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Age mul 1) divby 2)");
        }
        {
            final String expression = number.modulo(number).modulo(3).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Age mod Age) mod 3)");
        }
        {
            final String expression = size.modulo(size).modulo(3.0).getExpression(V4);
            softly.assertThat(expression).isEqualTo("((Size mod Size) mod 3.0)");
        }
        {
            final String expression = size.in(42, 13.37, 9000f).getExpression(V4);
            softly.assertThat(expression).isEqualTo("(Size in (42,13.37,9000.0))");
        }
        {
            softly.assertThat(size.ceil().getExpression(V4)).isEqualTo("ceiling(Size)");
            softly.assertThat(size.floor().getExpression(V4)).isEqualTo("floor(Size)");
            softly.assertThat(size.round().getExpression(V4)).isEqualTo("round(Size)");
        }
        {
            final String expression =
                number
                    .equalTo(number)
                    .and(number.equalTo(42))
                    .and(number.notEqualTo(number))
                    .and(number.notEqualTo(42))
                    .getExpression(V4);
            softly
                .assertThat(expression)
                .isEqualTo("((((Age eq Age) and (Age eq 42)) and (Age ne Age)) and (Age ne 42))");
        }
        {
            final String expression =
                number
                    .greaterThan(number)
                    .and(number.greaterThan(42))
                    .and(number.greaterThanEqual(number))
                    .and(number.greaterThanEqual(42))
                    .getExpression(V4);
            softly
                .assertThat(expression)
                .isEqualTo("((((Age gt Age) and (Age gt 42)) and (Age ge Age)) and (Age ge 42))");
        }
        {
            final String expression =
                number
                    .lessThan(number)
                    .and(number.lessThan(42))
                    .and(number.lessThanEqual(number))
                    .and(number.lessThanEqual(42))
                    .getExpression(V4);
            softly
                .assertThat(expression)
                .isEqualTo("((((Age lt Age) and (Age lt 42)) and (Age le Age)) and (Age le 42))");
        }
        softly.assertAll();
    }

    @Test
    void testGuid()
    {
        final SimpleProperty.Guid<TestEntity> id = new SimpleProperty.Guid<>(TestEntity.class, "Id");
        assertThat(id.equalTo(UUID.fromString("b3e130fe-d72c-4a5b-8dcf-463b497f985c")).getExpression(V4))
            .isEqualTo("(Id eq b3e130fe-d72c-4a5b-8dcf-463b497f985c)");
    }

    @Test
    void testFilterableString()
    {
        final SoftAssertions softly = new SoftAssertions();
        final SimpleProperty.String<TestEntity> name = new SimpleProperty.String<>(TestEntity.class, "Name");

        softly.assertThat(name.equalTo((String) null).getExpression(V4)).isEqualTo("(Name eq null)");
        softly.assertThat(name.equalToNull().getExpression(V4)).isEqualTo("(Name eq null)");
        softly.assertThat(name.equalToNull().not().getExpression(V4)).isEqualTo("(not (Name eq null))");
        softly.assertThat(name.notEqualToNull().getExpression(V4)).isEqualTo("(Name ne null)");
        softly.assertThat(name.indexOf(name).getExpression(V4)).isEqualTo("indexof(Name,Name)");
        softly.assertThat(name.indexOf("Foo").getExpression(V4)).isEqualTo("indexof(Name,'Foo')");
        softly.assertThat(name.length().getExpression(V4)).isEqualTo("length(Name)");
        softly.assertThat(name.matches("Foo").getExpression(V4)).isEqualTo("matchesPattern(Name,'Foo')");
        softly.assertThat(name.toUpper().toLower().getExpression(V4)).isEqualTo("tolower(toupper(Name))");

        softly
            .assertThat(name.concat(name).concat("Foo").trim().getExpression(V4))
            .isEqualTo("trim(concat(concat(Name,Name),'Foo'))");

        softly
            .assertThat(name.substring(1).substring(2, 3).getExpression(V4))
            .isEqualTo("substring(substring(Name,1),2,3)");

        softly
            .assertThat(name.startsWith(name).and(name.startsWith("Foo")).getExpression(V4))
            .isEqualTo("(startswith(Name,Name) and startswith(Name,'Foo'))");

        softly
            .assertThat(name.endsWith(name).and(name.endsWith("Foo")).getExpression(V4))
            .isEqualTo("(endswith(Name,Name) and endswith(Name,'Foo'))");

        softly
            .assertThat(name.contains("Foo").and(name.contains(name)).getExpression(V4))
            .isEqualTo("(contains(Name,'Foo') and contains(Name,Name))");

        softly.assertThat(name.equalTo("Qu'te").getExpression(V4)).isEqualTo("(Name eq 'Qu''te')");

        softly.assertAll();
    }

    @Test
    void testFilterableEnum()
    {
        final SoftAssertions softly = new SoftAssertions();
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.equalToNull().getExpression(V4))
            .isEqualTo("(ProgrammingLanguage eq null)");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.notEqualToNull().getExpression(V4))
            .isEqualTo("(ProgrammingLanguage ne null)");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.equalTo((TestEntity.Language) null).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage eq null)");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.notEqualTo((TestEntity.Language) null).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage ne null)");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.equalTo(TestEntity.Language.Java).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage eq SoftwareEngineer.Programming.Language'Java')");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.notEqualTo(TestEntity.Language.Java).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage ne SoftwareEngineer.Programming.Language'Java')");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.equalTo(TestEntity.PROGRAMMING_LANGUAGE).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage eq ProgrammingLanguage)");
        softly
            .assertThat(TestEntity.PROGRAMMING_LANGUAGE.notEqualTo(TestEntity.PROGRAMMING_LANGUAGE).getExpression(V4))
            .isEqualTo("(ProgrammingLanguage ne ProgrammingLanguage)");
        softly.assertAll();
    }
}
