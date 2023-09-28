package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;

/**
 * Interface representing a function bound to a specific type.
 *
 * @param <BindingT>
 *            The type the function is bound to.
 * @param <ResultT>
 *            The type this function returns.
 */
public interface BoundFunction<BindingT, ResultT> extends BoundOperation<BindingT, ResultT>
{
    /**
     * The parameters this function is invoked with.
     *
     * @return The parameters this function is invoked with.
     */
    @Nonnull
    ODataFunctionParameters getParameters();

    /*
    we need to differentiate on the type system all following dimensions:
        -> Composable (true/false)
        -> Src Cardinality (single/collection)
        -> Target Cardinality (single/collection)
        -> Target Type (primitive, complex, entity)

    in total 24 combinations == 24 classes with this approach
    */

    /**
     * Interface representing a composable bound function
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    interface Composable<BindingT, ResultT> extends BoundFunction<BindingT, ResultT>
    {
    }

    /*----------------------------------------------------------*/
    /*  1 - 1 Functions                                         */
    /*----------------------------------------------------------*/

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning a primitive.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToSinglePrimitive<BindingT, ResultT> extends SingleToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function argument names and their values
         */
        public SingleToSinglePrimitive(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning a complex type.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToSingleComplex<BindingT, ResultT extends VdmComplex<ResultT>> extends SingleToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToSingleComplex(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning an entity.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToSingleEntity<BindingT, ResultT extends VdmEntity<ResultT>> extends SingleToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToSingleEntity(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }

        public static final class Composable<BindingT, ResultT extends VdmEntity<ResultT>>
            extends
            SingleToSingleEntity<BindingT, ResultT>
            implements
            BoundFunction.Composable<BindingT, ResultT>
        {

            public Composable(
                @Nonnull final Class<BindingT> src,
                @Nonnull final Class<ResultT> target,
                @Nonnull final String name,
                @Nonnull final Map<String, Object> args )
            {
                super(src, target, name, args);
            }
        }
    }

    /*----------------------------------------------------------*/
    /*  1 - N Functions                                         */
    /*----------------------------------------------------------*/

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning a collection of primitives.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToCollectionPrimitive<BindingT, ResultT> extends SingleToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToCollectionPrimitive(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning a collection of complex
     * objects.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToCollectionComplex<BindingT, ResultT extends VdmComplex<ResultT>>
        extends
        SingleToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToCollectionComplex(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a single element and returning a collection of entities.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToCollectionEntity<BindingT, ResultT extends VdmEntity<ResultT>>
        extends
        SingleToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToCollectionEntity(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }

        public static final class Composable<BindingT, ResultT extends VdmEntity<ResultT>>
            extends
            SingleToCollectionEntity<BindingT, ResultT>
        {

            /**
             * Create an instance of a bound function.
             *
             * @param src
             *            The type this function is bound to.
             * @param target
             *            The type this function returns.
             * @param name
             *            The fully qualified name
             * @param args
             *            Key-value-pairs of function arguments names and their values
             */
            public Composable(
                @Nonnull final Class<BindingT> src,
                @Nonnull final Class<ResultT> target,
                @Nonnull final String name,
                @Nonnull final Map<String, Object> args )
            {
                super(src, target, name, args);
            }
        }
    }

    /*----------------------------------------------------------*/
    /*  N - 1 Functions                                         */
    /*----------------------------------------------------------*/
    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning a primitive.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToSinglePrimitive<BindingT, ResultT> extends CollectionToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToSinglePrimitive(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning a complex object.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToSingleComplex<BindingT, ResultT extends VdmComplex<ResultT>>
        extends
        CollectionToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToSingleComplex(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning an entity.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToSingleEntity<BindingT, ResultT extends VdmEntity<ResultT>>
        extends
        CollectionToSingle<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToSingleEntity(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }

        public static final class Composable<BindingT, ResultT extends VdmEntity<ResultT>>
            extends
            CollectionToSingleEntity<BindingT, ResultT>
        {
            /**
             * Create an instance of a bound function.
             *
             * @param src
             *            The type this function is bound to.
             * @param target
             *            The type this function returns.
             * @param name
             *            The fully qualified name
             * @param args
             *            Key-value-pairs of function arguments names and their values
             */
            public Composable(
                @Nonnull final Class<BindingT> src,
                @Nonnull final Class<ResultT> target,
                @Nonnull final String name,
                @Nonnull final Map<String, Object> args )
            {
                super(src, target, name, args);
            }
        }

    }

    /*----------------------------------------------------------*/
    /*  N - N Functions                                         */
    /*----------------------------------------------------------*/
    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning a collection of
     * primitives.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToCollectionPrimitive<BindingT, ResultT> extends CollectionToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToCollectionPrimitive(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning a collection of
     * complex objects.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToCollectionComplex<BindingT, ResultT extends VdmComplex<ResultT>>
        extends
        CollectionToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToCollectionComplex(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements and returning a collection of
     * entities.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToCollectionEntity<BindingT, ResultT extends VdmEntity<ResultT>>
        extends
        CollectionToCollection<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToCollectionEntity(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }

        public static final class Composable<BindingT, ResultT extends VdmEntity<ResultT>>
            extends
            CollectionToCollectionEntity<BindingT, ResultT>
        {
            /**
             * Create an instance of a bound function.
             *
             * @param src
             *            The type this function is bound to.
             * @param target
             *            The type this function returns.
             * @param name
             *            The fully qualified name
             * @param args
             *            Key-value-pairs of function arguments names and their values
             */
            public Composable(
                @Nonnull final Class<BindingT> src,
                @Nonnull final Class<ResultT> target,
                @Nonnull final String name,
                @Nonnull final Map<String, Object> args )
            {
                super(src, target, name, args);
            }
        }
    }

    /*----------------------------------------------------------*/
    /*  Variants without target type info                       */
    /*  used by "applyFunction" which doesn't care about the    */
    /*  target type to be in the type system                    */
    /*----------------------------------------------------------*/

    /**
     * Specific {@link BoundFunction function} operating on a single element returning a single element.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToSingle<BindingT, ResultT> extends AbstractBoundOperation.AbstractBoundFunction<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToSingle(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a single element returning a collection of elements.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class SingleToCollection<BindingT, ResultT> extends AbstractBoundOperation.AbstractBoundFunction<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public SingleToCollection(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements returning a single element.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToSingle<BindingT, ResultT> extends AbstractBoundOperation.AbstractBoundFunction<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToSingle(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }

    /**
     * Specific {@link BoundFunction function} operating on a collection of elements returning a collection of elements.
     *
     * @param <BindingT>
     *            The type the function is bound to.
     * @param <ResultT>
     *            The type this function returns.
     */
    class CollectionToCollection<BindingT, ResultT>
        extends
        AbstractBoundOperation.AbstractBoundFunction<BindingT, ResultT>
    {
        /**
         * Create an instance of a bound function.
         *
         * @param src
         *            The type this function is bound to.
         * @param target
         *            The type this function returns.
         * @param name
         *            The fully qualified name
         * @param args
         *            Key-value-pairs of function arguments names and their values
         */
        public CollectionToCollection(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name, args);
        }
    }
}
