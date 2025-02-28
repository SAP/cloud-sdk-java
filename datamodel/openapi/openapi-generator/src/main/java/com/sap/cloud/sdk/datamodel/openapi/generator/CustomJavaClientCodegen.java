package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class CustomJavaClientCodegen extends JavaClientCodegen
{
    private final List<GeneratorCustomization> customizations;

    public CustomJavaClientCodegen( final GenerationConfiguration config )
    {
        this.customizations = GeneratorCustomization.getCustomizations(config);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "ReplaceInefficientStreamCount"})
    @Override
    public void preprocessOpenAPI( OpenAPI openAPI )
    {
        super.preprocessOpenAPI(openAPI);

        // remove selected properties
        final var removeProperties =
            Arrays
                .asList(
                    "chatCompletionResponseMessage.context", // removes azureChatExtensionsMessageContext
                    "createChatCompletionRequest.data_sources" // removes azureChatExtensionConfiguration
                );
        for( final var removeProperty : removeProperties ) {
            final var split = removeProperty.split("\\.", 2);
            final var schema = openAPI.getComponents().getSchemas().get(split[0]);

            boolean removed = false;

            final Predicate<Schema> remove =
                s -> s != null && s.getProperties() != null && s.getProperties().remove(split[1]) != null;
            final var schemasToCheck = new LinkedHashSet<Schema>();
            schemasToCheck.add(schema);
            while( !schemasToCheck.isEmpty() ) {
                final var sit = schemasToCheck.iterator();
                final var s = sit.next();
                sit.remove();
                removed |= remove.test(s);
                if( s.getAllOf() != null ) {
                    removed |= s.getAllOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getAllOf());
                }
                if( s.getAnyOf() != null ) {
                    removed |= s.getAnyOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getAnyOf());
                }
                if( s.getOneOf() != null ) {
                    removed |= s.getOneOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getOneOf());
                }
            }
            if( !removed ) {
                log.error("Could not remove property {}", removeProperty);
            }
        }

        // remove some path
        final var removePaths =
            Arrays
                .asList(
                    "/deployments/{deployment-id}/completions",
                    "/deployments/{deployment-id}/embeddings",
                    "/deployments/{deployment-id}/audio/transcriptions",
                    "/deployments/{deployment-id}/audio/translations",
                    "/deployments/{deployment-id}/images/generations");
        for( final var removePath : removePaths ) {
            if( !openAPI.getPaths().keySet().remove(removePath) ) {
                log.error("Could not remove path {}", removePath);
            }
        }

        // delete redundant components
        final var queue = new LinkedHashSet<Schema>();
        final var schemas = new LinkedHashSet<Schema>();
        final var refs = new LinkedHashSet<String>();
        final var pattern = Pattern.compile("\\$ref: #/components/schemas/(\\w+)");
        for( final var path : openAPI.getPaths().values() ) {
            final var m = pattern.matcher(path.toString());
            while( m.find() ) {
                final var name = m.group(1);
                final var schema = openAPI.getComponents().getSchemas().get(name);
                queue.add(schema);
                refs.add(m.group(0).split(" ")[1]);
            }
        }

        while( !queue.isEmpty() ) {
            final var qit = queue.iterator();
            final var s = qit.next();
            qit.remove();
            if( !schemas.add(s) ) {
                continue;
            }
            final var ref = s.get$ref();
            if( ref != null ) {
                refs.add(ref);
                final var refName = ref.substring(ref.lastIndexOf('/') + 1);
                queue.add(openAPI.getComponents().getSchemas().get(refName));
            }
            if( s.getProperties() != null ) {
                for( final var s1 : s.getProperties().values() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getItems() != null ) {
                queue.add(s.getItems());
            }
            if( s.getAllOf() != null ) {
                for( final var s1 : s.getAllOf() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getAnyOf() != null ) {
                for( final var s1 : s.getAnyOf() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getOneOf() != null ) {
                for( final var s1 : s.getOneOf() ) {
                    queue.add((Schema) s1);
                }
            }
        }

        openAPI.getComponents().getSchemas().keySet().removeIf(schema -> {
            if( !refs.contains("#/components/schemas/" + schema) ) {
                log.error("Removing unused schema {}", schema);
                return true;
            }
            return false;
        });
    }

    @Override
    protected
        void
        updatePropertyForArray( @Nonnull final CodegenProperty property, @Nonnull final CodegenProperty innerProperty )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdatePropertyForArray custom ) {
                custom.updatePropertyForArray(this, property, innerProperty);
            }
        }
        super.updatePropertyForArray(property, innerProperty);
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    @Nullable
    public String toDefaultValue( @Nonnull final CodegenProperty cp, @Nonnull final Schema schema )
    {
        final String superValue = super.toDefaultValue(cp, schema);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.ToDefaultValue custom ) {
                return custom.toDefaultValue(this, superValue, cp, schema);
            }
        }
        return superValue;
    }

    @Override
    @Nullable
    public String toBooleanGetter( @Nullable final String name )
    {
        final String superValue = super.toBooleanGetter(name);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.ToBooleanGetter custom ) {
                return custom.toBooleanGetter(this, superValue, name);
            }
        }
        return superValue;
    }

    @Override
    @Nonnull
    public
        OperationsMap
        postProcessOperationsWithModels( @Nonnull OperationsMap ops, @Nonnull final List<ModelMap> allModels )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.PostProcessOperationsWithModels custom ) {
                ops = custom.postProcessOperationsWithModels(this, ops, allModels);
            }
        }
        return super.postProcessOperationsWithModels(ops, allModels);
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForComposedSchema(
        @Nonnull final CodegenModel m,
        @Nonnull final Schema schema,
        @Nonnull final Map<String, Schema> allDefinitions )
    {
        super.updateModelForComposedSchema(m, schema, allDefinitions);
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdateModelForComposedSchema custom ) {
                custom.updateModelForComposedSchema(this, m, schema, allDefinitions);
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForObject( @Nonnull final CodegenModel m, @Nonnull final Schema schema )
    {
        for( final GeneratorCustomization customization : customizations ) {
            if( customization instanceof final GeneratorCustomization.UpdateModelForObject custom ) {
                custom.updateModelForObject(this, m, schema);
            }
        }
        super.updateModelForObject(m, schema);
    }
}
