# OAS 3.1 Generator Gaps Analysis

This document summarizes the gaps between the current SAP Cloud SDK OpenAPI generator (targeting OAS 3.0)
and full OAS 3.1.x support. It covers every release from 3.1.0-rc0 (June 2020) through 3.1.2 (September 2025).

## Background

The generator is built on top of `openapi-generator` 7.23.0 and `swagger-parser` 2.1.45.
Its custom layers are concentrated in:

- [CustomOpenAPINormalizer.java](openapi-generator/src/main/java/com/sap/cloud/sdk/datamodel/openapi/generator/CustomOpenAPINormalizer.java) — schema normalisation before code generation
- [ValidationKeywordsPreprocessor.java](openapi-generator/src/main/java/com/sap/cloud/sdk/datamodel/openapi/generator/ValidationKeywordsPreprocessor.java) — spec validation on raw `JsonNode`
- [GenerationConfigurationConverter.java](openapi-generator/src/main/java/com/sap/cloud/sdk/datamodel/openapi/generator/GenerationConfigurationConverter.java) — parser invocation and additional properties
- [CustomJavaClientCodegen.java](openapi-generator/src/main/java/com/sap/cloud/sdk/datamodel/openapi/generator/CustomJavaClientCodegen.java) — codegen overrides (composed schemas, array types, etc.)

All test specs use `openapi: 3.0.0` or `openapi: 3.0.3`. No OAS 3.1 test fixtures exist.

---

## Gap 1 — `nullable` Keyword Removed (Breaking)

**Spec change (3.1.0-rc0):** `nullable: true` is entirely removed. The replacement is expressing null as a member of a type union:

```yaml
# OAS 3.0
type: string
nullable: true

# OAS 3.1
type:
  - string
  - "null"
```

**Current code:**
`CustomOpenAPINormalizer.normalizeReferenceSchema()` (line 51) reads `schema.getNullable()` and uses it to decide whether to wrap a `$ref` schema in `allOf`. This logic silently passes when `nullable` is absent (as in a 3.1 spec), leaving nullable intent unexpressed.

`GenerationConfigurationConverter` forces `openApiNullable=false` (line 184) as a workaround for `JsonNullable` issues (BLI CLOUDECOSYSTEM-9843), which further masks any nullable annotation generated from 3.0's `nullable: true`.

**Impact:**
- A 3.1 spec using `type: ["string", "null"]` will be parsed with the upstream `swagger-parser`, but the custom normalizer and codegen have no path to propagate "null-union" types to `@Nullable` Java annotations or `JsonNullable` wrappers.
- Any pre-existing 3.0 spec migrated to 3.1 with `nullable: true` kept will have nullability silently dropped.

**Action required:**
- Replace `getNullable()` checks with inspection of `schema.getTypes()` for the presence of `"null"`.
- Resolve CLOUDECOSYSTEM-9843 to re-enable `openApiNullable` correctly.
- Add test fixtures using `type: ["string", "null"]` and `anyOf: [{$ref: ...}, {type: "null"}]`.

---

## Gap 2 — `exclusiveMinimum` / `exclusiveMaximum` Semantic Inversion (Breaking)

**Spec change (3.1.0-rc0):** These keywords change from boolean modifiers on `minimum`/`maximum` to standalone numeric bounds:

```yaml
# OAS 3.0: boolean modifier — minimum is 7, exclusive
minimum: 7
exclusiveMinimum: true

# OAS 3.1: standalone — exclusive minimum is 7 (minimum keyword unused)
exclusiveMinimum: 7
```

**Current code:**
`CustomOpenAPINormalizer.normalizeReferenceSchema()` (lines 56–57) reads `schema.getExclusiveMaximum()` and `schema.getExclusiveMinimum()` purely to detect the presence of sibling keywords alongside a `$ref`. In `swagger-parser` 2.1.45 the model class `Schema` maps both 3.0 `Boolean` and 3.1 `BigDecimal` forms, but the current custom code does not branch on version.

**Impact:**
- A 3.1 spec with `exclusiveMinimum: 7` (numeric) will be parsed as a BigDecimal by `swagger-parser`, but the normalizer checks `!= null` only to trigger `allOf` wrapping — it does not validate or pass through the exclusive bound value.
- Bean-validation annotations generated for 3.0 schemas (e.g., `@DecimalMin(value="7", inclusive=false)`) will be generated incorrectly for 3.1 specs because the numeric value of `exclusiveMinimum` is conflated with the boolean flag.

**Action required:**
- Detect OAS version at parse time and branch on `exclusiveMinimum`/`exclusiveMaximum` semantics accordingly.
- For 3.1: treat the keyword value directly as the exclusive bound; do not read `minimum`/`maximum` as the companion inclusive bound.
- Add version-conditional normalizer logic or a dedicated preprocessing step.

---

## Gap 3 — `type` as Array (Breaking)

**Spec change (3.1.0-rc0, JSON Schema 2020-12):** `type` may now be either a string or an array of strings:

```yaml
# Both valid in 3.1
type: string
type: ["string", "integer", "null"]
```

**Current code:**
`CustomOpenAPINormalizer.normalizeReferenceSchema()` (line 42) correctly checks both `schema.getType()` (single string, OAS 3.0) and `schema.getTypes()` (set, OAS 3.1) in the same condition. However it only *clears* both fields; it does not model or propagate multi-type unions through code generation.

Downstream in `CustomJavaClientCodegen`, type resolution is delegated entirely to `JavaClientCodegen` from upstream openapi-generator. The upstream library has partial 3.1 support, but union types beyond null-union are not mapped to Java types (there is no direct Java equivalent of `type: ["string", "integer"]`).

**Impact:**
- Multi-type arrays beyond `["X", "null"]` have no Java representation and will silently fall back to `Object`.
- The `checkForValidatorsInSchemas` in `ValidationKeywordsPreprocessor` does not check for multi-type arrays, so no validation error is raised for unsupported cases.

**Action required:**
- Document supported subset: only `["X", "null"]` (nullable scalar) is mappable to Java.
- Emit a warning or error for multi-value type arrays that are not null-unions.
- Add a test fixture with multi-type schemas to verify fallback behaviour.

---

## Gap 4 — `$ref` Sibling Properties Now Merged in Schema Objects (Breaking)

**Spec change (3.1.0-rc0):** In OAS 3.0 any properties alongside a `$ref` were **ignored**. In OAS 3.1 (following JSON Schema 2020-12) sibling keywords are **combined** with the referenced schema:

```yaml
# 3.0 — description was silently ignored
schema:
  $ref: '#/components/schemas/Pet'
  description: "ignored in 3.0"

# 3.1 — description is combined with the referenced schema
schema:
  $ref: '#/components/schemas/Pet'
  description: "applied in 3.1"
```

**Current code:**
`CustomOpenAPINormalizer.normalizeReferenceSchema()` (lines 84–88) already implements an `allOf` wrapping strategy to handle sibling properties on `$ref` schemas. The workaround exists because `swagger-parser` may copy properties from a referenced schema onto the `$ref` node (lines 71–88 comments). This happens to produce correct results for OAS 3.0 sibling-annotation patterns.

However the logic gates on `schema.getNullable()` (OAS 3.0 only) and several other 3.0-specific fields. A 3.1 schema with a `$ref` + `description` or `$ref` + `const` pair will only trigger wrapping if those properties are among the checked set.

**Impact:**
- New 3.1 sibling keywords (`const`, `$comment`, JSON Schema 2020-12 vocabulary keywords) are not included in the trigger condition. They will be silently dropped.
- Reference Object contexts (parameter `$ref`, response `$ref`) allow **only** `summary` and `description` as overrides in 3.1. The current preprocessor does not validate this restriction.

**Action required:**
- Extend the sibling-property condition in `normalizeReferenceSchema()` to cover `const`, `$comment`, `if`/`then`/`else`, `unevaluatedProperties`, and other 3.1 keywords.
- Add a validation check that non-Schema Reference Objects (parameters, responses, headers) carry at most `summary` and `description` alongside `$ref`.

---

## Gap 5 — `webhooks` Top-Level Field Not Supported

**Spec change (3.1.0-rc0):** A new top-level `webhooks` field maps webhook names to Path Item Objects. The `paths` top-level field is now **optional**.

**Current code:**
`ValidationKeywordsPreprocessor.execute()` (line 23) reads `input.path(PATHS_NODE)` unconditionally. If `paths` is absent (a valid 3.1 document), `pathsNode` will be a `MissingNode`; the `findValue` calls return `null`, so no exception is thrown — but the document is effectively ignored.

`CustomJavaClientCodegen.preprocessRemoveRedundancies()` (line 204) calls `openAPI.getPaths().values()` without a null check; a webhooks-only document would throw a `NullPointerException` here.

No `webhooks` field is traversed anywhere in the custom code.

**Impact:**
- Webhooks-only or paths-absent 3.1 documents cannot be processed.
- NPE risk in `preprocessRemoveRedundancies` when `paths` is null.

**Action required:**
- Add null-check guards for `openAPI.getPaths()` wherever it is called.
- Evaluate whether SAP Cloud SDK should support generating webhook subscriber clients and decide on scope.
- At minimum, emit a clear error message when a document contains `webhooks` but no `paths`.

---

## Gap 6 — `components.pathItems` Not Handled

**Spec change (3.1.0-rc0):** The `components` object gains a `pathItems` map for reusable Path Item Objects.

**Current code:**
`GenerationConfigurationConverter.preprocessSpecification()` (lines 139–158) and the remove-redundancies logic in `CustomJavaClientCodegen` only walk `components.schemas`, `components.responses`, and paths. No code traverses `components.pathItems`.

**Impact:**
- `$ref` references pointing into `#/components/pathItems/...` will not be resolved correctly during preprocessing.
- The remove-unused-components feature (`FIX_REMOVE_UNUSED_COMPONENTS`) will not count path item references and may spuriously remove schemas referenced only from a reusable path item.

**Action required:**
- Extend schema/ref traversal in `preprocessRemoveRedundancies` to descend into `components.pathItems`.
- Verify that `swagger-parser` 2.1.45 correctly resolves `#/components/pathItems/` references (upgrade may be needed).

---

## Gap 7 — `schema.example` Deprecated in Favour of `examples` Array

**Spec change (3.1.0):** Inside Schema Objects, the singular `example` keyword is deprecated. The JSON Schema 2020-12 standard uses `examples` (an array):

```yaml
# 3.0 / deprecated 3.1
example: "Berlin"

# 3.1 preferred
examples: ["Berlin"]
```

**Current code:**
`CustomOpenAPINormalizer.normalizeReferenceSchema()` (line 66) checks `schema.getExample() != null` as a trigger for `allOf` wrapping. This means a 3.1 schema that uses `examples` (array) alongside a `$ref` will not be wrapped, potentially losing those examples.

Line 67 checks `schema.getExamples() != null` as well, so the array form is covered in the wrap condition. However the two are treated identically — no deprecation warning for singular `example` in 3.1 documents is emitted.

**Impact:**
- No generation failure, but no deprecation guidance for spec authors using 3.0's `example` in a 3.1 document.
- Example values from the `examples` array are not surfaced differently from the singular `example` in generated code or API documentation.

**Action required:**
- Emit a deprecation warning when `schema.getExample()` is non-null in a 3.1 document.
- Ensure generated Javadoc/swagger annotations use the `examples` array form when targeting 3.1.

---

## Gap 8 — File Upload / Binary Encoding Pattern Changed (Breaking)

**Spec change (3.1.0-rc0):** Binary file descriptions change from format-based to JSON Schema content keywords:

```yaml
# OAS 3.0 (still parseable but deprecated in 3.1)
type: string
format: binary

# OAS 3.1 (correct)
type: string
contentEncoding: base64
contentMediaType: image/png
```

**Current code:**
`GenerationConfigurationConverter` contains a type mapping for `File -> byte[]` passed from `GenerationConfiguration.typeMappings`. The underlying `JavaClientCodegen` maps `format: binary` to a byte-array or `File` Java type. Neither the custom normalizer nor the preprocessing steps handle `contentEncoding` or `contentMediaType`.

**Impact:**
- A 3.1 spec using `contentEncoding`/`contentMediaType` for file uploads will not generate a `byte[]` or `InputStream` Java type; it will likely fall back to `Object` or `String`.
- The `format: binary` pattern still works via the upstream library for 3.0-style specs even when served under `openapi: 3.1.0`.

**Action required:**
- Add a preprocessing step or normalizer hook that maps `contentEncoding: base64` → `format: byte` and `contentMediaType: application/octet-stream` → `format: binary` for compatibility with downstream type mapping.
- Add a test fixture for file upload with `contentEncoding`.

---

## Gap 9 — JSON Schema 2020-12 Vocabulary Keywords Unhandled

**Spec change (3.1.0):** Full JSON Schema 2020-12 alignment introduces keywords the OAS 3.0-era generator has never seen:

| Keyword | Description |
|---|---|
| `$defs` | Inline reusable schemas (replaces `definitions`) |
| `$comment` | Non-validating developer annotation |
| `const` | Single-value constraint (cleaner than single-item `enum`) |
| `prefixItems` | Positional array item schemas (replaces array form of `items`) |
| `unevaluatedProperties` | Stricter `additionalProperties` that sees through `$ref`/combinators |
| `unevaluatedItems` | Same as above for array items |
| `if` / `then` / `else` | Conditional schema application |
| `$dynamicRef` / `$dynamicAnchor` | Dynamic references for recursive schemas |

**Current code:**
None of these keywords are referenced anywhere in the custom generator code. They will either be passed through silently to the upstream `JavaClientCodegen` (which has partial support) or be ignored.

`CustomJavaClientCodegen.updateModelForObject()` (line 264) unconditionally sets `additionalProperties` to `Boolean.FALSE`. In 3.1 `unevaluatedProperties` is the correct mechanism for sealing an object that uses `allOf`/`anyOf`/`oneOf` — `additionalProperties: false` does not see through those combinators. This means any 3.1 schema that uses `unevaluatedProperties: false` instead of `additionalProperties: false` will NOT be sealed by the codegen.

**Impact:**
- `const` schemas will generate as single-value `enum` — workable but different from the intended model.
- `$defs` references will not be resolved by the preprocessing traversal in `preprocessRemoveRedundancies`, potentially causing schemas that are only referenced via `$defs` to be pruned.
- `unevaluatedProperties` is silently ignored; generated model classes will have an `additionalProperties` map even when the spec author intended to seal the object.
- `prefixItems` / tuple arrays are not mapped — they will likely generate as `List<Object>`.
- `if`/`then`/`else` schemas are not mapped — they will generate as `Object`.

**Action required:**
- `$defs`: extend `preprocessRemoveRedundancies` traversal to follow `$defs` references.
- `const`: map to a single-element enum or a `@JsonProperty` constant in generated code; add a preprocessing step to normalise `const` to a single-item `enum` if the upstream generator does not handle it.
- `unevaluatedProperties`: do not force-set `additionalProperties: false` in `updateModelForObject`; instead inspect whether `unevaluatedProperties` is present.
- `if`/`then`/`else`, `prefixItems`, `$dynamicRef`: emit unsupported-feature warnings.

---

## Gap 10 — `ValidationKeywordsPreprocessor` Assumes `paths` is Present

**Current code:**
`ValidationKeywordsPreprocessor.execute()` (line 23) always walks `input.path("paths")`. In OAS 3.1 a document may omit `paths` entirely and provide only `webhooks` or `components`.

When `paths` is missing, `pathsNode` is a `MissingNode`; `findValue()` returns `null` and no exception is raised — but it also means the validator silently skips validation entirely, allowing invalid `anyOf`/`oneOf` placements in a spec that does have `paths` nested inside `webhooks`.

**Action required:**
- Guard on `pathsNode.isMissingNode()` and emit a warning when `paths` is absent.
- Extend validation to traverse `webhooks` operations for the same `anyOf`/`oneOf` placement rules.
- Consider whether the `oneOfAnyOfGenerationEnabled=false` default restriction makes sense for 3.1 specs, where `anyOf: [{$ref:...}, {type:"null"}]` is the canonical nullable pattern and must be supported.

---

## Gap 11 — No OAS Version Detection or Version-Specific Routing

**Current state:**
There is no code in the custom layers that reads `openAPI.getOpenapi()` (the version string) to branch behaviour. All processing assumes OAS 3.0.x semantics. `swagger-parser` 2.1.45 transparently parses both versions into the same `io.swagger.v3.oas.models.OpenAPI` object model, hiding the version distinction from consumers.

**Impact:**
- The same preprocessing logic is applied regardless of whether the document declares `openapi: 3.0.3` or `openapi: 3.1.0`, leading to incorrect behaviour for `exclusiveMinimum`/`exclusiveMaximum`, `nullable`, and `$ref` sibling semantics.
- No validation that the spec version matches expected conventions.

**Action required:**
- Read the version from `openAPI.getOpenapi()` (or from the raw `JsonNode` in preprocessing steps) and gate version-specific logic behind it.
- Add an explicit unsupported-version warning (or error) when the generator encounters a version it is not yet fully tested against.

---

## Gap 12 — `info.summary` and `info.license.identifier` Not Surfaced

**Spec change (3.1.0-rc0):** The Info Object gains a `summary` field (plain string) and the License Object gains an `identifier` field (SPDX expression):

```yaml
info:
  title: My API
  summary: Short one-liner for catalog views
  license:
    name: Apache 2.0
    identifier: Apache-2.0
```

**Current code:**
The generator does not read or surface `info.summary` or `license.identifier` anywhere. These fields exist only in documentation metadata and are not directly relevant to Java code generation.

**Impact:** Low. These fields affect tooling that reads the spec for catalog/discovery purposes, not the generated Java client.

**Action required:** No code change needed. Note that `DatamodelMetadataGeneratorAdapter` may wish to persist `info.summary` as part of the generated `.json` metadata file.

---

## Gap 13 — `mutualTLS` Security Scheme Type Not Tested

**Spec change (3.1.0-rc0):** A new `mutualTLS` security scheme type is added. It has no extra fields beyond `description`.

**Current code:**
Security scheme types are not handled in the custom code; upstream `JavaClientCodegen` manages them. The upstream library maps security schemes to authentication annotations and configuration classes.

**Impact:** Low for code generation (no extra fields to generate). Generated client code does not require special handling for `mutualTLS` at the source level — it is a transport-layer concern.

**Action required:**
- Verify that `swagger-parser` 2.1.45 correctly parses `type: mutualTLS` without throwing an unknown-type exception.
- Add a test fixture with a `mutualTLS` security scheme.

---

## Gap 14 — Server Object `summary` Field Not Surfaced

**Spec change (3.1.0-rc0):** Server Objects gain a `summary` field (plain text label for tooling).

**Current code:** Not used in code generation. No impact on generated Java clients.

**Action required:** None for code generation. May be relevant for generated documentation or `DatamodelMetadataGeneratorAdapter`.

---

## Gap 15 — `jsonSchemaDialect` Top-Level Field Not Handled

**Spec change (3.1.0):** A new top-level `jsonSchemaDialect` URI field declares the default JSON Schema dialect for all Schema Objects. The OAS dialect URI is `https://spec.openapis.org/oas/3.1/dialect/base`.

**Current code:** `swagger-parser` accepts this field without error, but neither the custom normalizer nor the preprocessing steps read or act on it.

**Impact:** Low for the common case (spec authors rarely override the dialect). Non-standard dialects (e.g., pure JSON Schema 2020-12 without OAS extensions) may cause the upstream `JavaClientCodegen` to mishandle discriminator or `xml` objects.

**Action required:**
- Emit a warning when `jsonSchemaDialect` is set to a non-OAS URI.
- Consider passing the dialect URI through to `swagger-parser` parse options.

---

## Dependency Versions to Verify

The following library versions determine how much OAS 3.1 support is available out-of-the-box:

| Library | Current version | Notes |
|---|---|---|
| `openapi-generator` | 7.23.0 | 3.1 support present but has known bugs (nullable+allOf+$ref, unevaluatedProperties) |
| `io-swagger-parser-v3` | 2.1.45 | Parses 3.1 but full 2020-12 JSON Schema validation is incomplete |
| `io-swagger-core-v3` | 2.2.52 | Model classes expose both `getType()` and `getTypes()` — sufficient for dual-version handling |

Recommend checking the upstream changelogs for any 3.1-related fixes released after these versions before implementing the gaps above.

---

## Priority Summary

| Priority | Gap | Effort |
|---|---|---|
| P0 — Breaking | Gap 1: `nullable` removed | Medium |
| P0 — Breaking | Gap 2: `exclusiveMinimum/Maximum` semantic change | Low |
| P0 — Breaking | Gap 3: `type` as array | Medium |
| P0 — Breaking | Gap 4: `$ref` sibling merging | Medium |
| P1 — Functional | Gap 5: `webhooks` / optional `paths` (NPE risk) | Medium |
| P1 — Functional | Gap 8: Binary/file upload encoding | Low |
| P1 — Functional | Gap 9: JSON Schema 2020-12 keywords (`$defs`, `const`, `unevaluatedProperties`) | High |
| P1 — Functional | Gap 10: `ValidationKeywordsPreprocessor` blocks canonical nullable pattern | Low |
| P1 — Functional | Gap 11: No OAS version detection | Low |
| P2 — Quality | Gap 6: `components.pathItems` traversal | Low |
| P2 — Quality | Gap 7: `example` deprecation warning | Low |
| P3 — Informational | Gap 12: `info.summary` / `license.identifier` | Trivial |
| P3 — Informational | Gap 13: `mutualTLS` test coverage | Trivial |
| P3 — Informational | Gap 14: Server `summary` | Trivial |
| P3 — Informational | Gap 15: `jsonSchemaDialect` field | Trivial |
