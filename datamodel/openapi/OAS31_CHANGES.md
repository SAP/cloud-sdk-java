# OAS 3.1 Support — Change Summary

This document describes all code changes made to add OAS 3.1.x support to the SAP Cloud SDK OpenAPI
generator. The full gap analysis is in [OAS31_GAPS.md](OAS31_GAPS.md). Gaps 9, 12, 14, and 15 are
out of scope and remain documented there for future work.

---

## New File: `OasVersionUtil.java`

**Path:** `openapi-generator/src/main/java/.../generator/OasVersionUtil.java`

A small utility class with three static `isOas31(...)` overloads accepting a `String`, an `OpenAPI`
object, or a `JsonNode`. Every version-conditional code path in the other changed files delegates to
this class so the version check is defined in one place.

---

## Changed: `CustomOpenAPINormalizer.java`

**Gaps addressed:** 1, 2, 4, 7, 8

### What changed

**`isOas31` field** — Set in the constructor via `OasVersionUtil.isOas31(openAPI)`. All new
behaviour in this class is gated on this flag, so OAS 3.0 specs are processed identically to before.

**`normalizeReferenceSchema` — Gap 1 (nullable deprecation warning)**  
When processing an OAS 3.1 spec and encountering a `$ref` schema that still carries `nullable: true`
(which is not valid in 3.1), the normalizer now emits a `WARN` log telling the spec author to use
`anyOf: [{$ref: "..."}, {type: "null"}]` instead. This does not block generation.

**`normalizeReferenceSchema` — Gap 2 (numeric exclusiveMinimum/Maximum)**  
In OAS 3.0 `exclusiveMinimum`/`exclusiveMaximum` are booleans. In OAS 3.1 they are independent
numeric values (`BigDecimal`). The swagger-core model exposes these as separate fields:
`getExclusiveMinimumValue()` / `getExclusiveMaximumValue()`. Both are now included in the
sibling-property condition that triggers `allOf` wrapping, so a `$ref` schema with a numeric
exclusive bound is handled correctly.

**`normalizeReferenceSchema` — Gap 4 (`const` as $ref sibling)**  
The OAS 3.1 `const` keyword (from JSON Schema 2020-12) is now included in the sibling-property
condition, so a `$ref` schema with a `const` sibling is wrapped in `allOf` and the constant
constraint is preserved in generated code.

**New `normalizeSchema` override — Gap 7 (example deprecation warning)**  
In OAS 3.1 the singular `example` keyword inside Schema Objects is deprecated in favour of the
array form `examples: [...]`. When `isOas31` and a schema carries `example`, a `WARN` log is
emitted. Generation is not blocked.

**New `normalizeSchema` override — Gap 8 (binary file upload encoding)**  
OAS 3.1 replaces `format: binary` / `format: byte` with JSON Schema keywords `contentEncoding` and
`contentMediaType`. This normalizer override maps those back to the legacy `format` values before
the upstream code generator sees the schema, preserving the existing `File → byte[]` type mapping:

| OAS 3.1 input | Mapped to |
|---|---|
| `contentEncoding: base64` | `format: byte` |
| `contentEncoding: <other>` | `format: binary` |
| `contentMediaType: <any>` (no encoding) | `format: binary` |

---

## Changed: `ValidationKeywordsPreprocessor.java`

**Gaps addressed:** 5, 10

### What changed

**Gap 5 — Webhooks-only documents**  
OAS 3.1 makes the top-level `paths` field optional. A document with only `webhooks` (and no `paths`)
is now detected early and a clear `OpenApiGeneratorException` is thrown explaining that webhook
client generation is not yet supported. Previously, such a document would silently produce no output
or cause a `NullPointerException` downstream.

A document with only `components` and neither `paths` nor `webhooks` is allowed to continue —
the upstream generator handles this case gracefully.

**Gap 10 — Canonical OAS 3.1 nullable-ref pattern**  
The preprocessor used to block *any* occurrence of `anyOf` or `oneOf` when
`oneOfAnyOfGenerationEnabled=false`. In OAS 3.1 the standard way to express a nullable `$ref` is:

```yaml
anyOf:
  - $ref: '#/components/schemas/Foo'
  - type: "null"
```

A new private `isNullUnionPattern(JsonNode)` method recognises this exact two-element array
(one `$ref`-only object, one `{type: "null"}`-only object) and exempts it from the block. All other
multi-element or non-null-union `anyOf`/`oneOf` occurrences are still blocked unless the
`oneOfAnyOfGenerationEnabled` flag is set.

---

## Changed: `CustomJavaClientCodegen.java`

**Gaps addressed:** 5, 6

### What changed

**Gap 5 — Null-safe `paths` access**  
Two places called `openAPI.getPaths()` without guarding against `null`:

- `preprocessOpenAPI`: the `USE_EXCLUDE_PATHS` loop now checks `openAPI.getPaths() != null` before
  calling `.keySet().remove(...)`.
- `preprocessRemoveRedundancies`: an early-return guard is added at the top. If `paths` is null or
  empty a warning is logged and the method returns immediately, avoiding a `NullPointerException` on
  webhooks-only or components-only documents.

**Gap 6 — `components/pathItems` traversal**  
The `preprocessRemoveRedundancies` method discovers which `components/schemas` are in use by
scanning path definitions for `$ref` strings. In OAS 3.1 reusable path items can be stored in
`components/pathItems` and referenced from both `paths` and `webhooks`. The method now also scans
`openAPI.getComponents().getPathItems()` using the same regex-based approach, so schemas referenced
only via a reusable path item are not wrongly pruned by the remove-unused-components feature.

---

## Changed: `GenerationConfigurationConverter.java`

**Gap addressed:** 11

### What changed

After the spec is parsed, `OasVersionUtil.isOas31(result)` is checked. If true, an `INFO` log is
emitted stating the detected version and pointing to `OAS31_GAPS.md` for known limitations. This
gives users a clear signal that OAS 3.1 mode is active without blocking generation.

---

## New Test Fixtures

### `DataModelGeneratorUnitTest/sodastore-31.yaml`

An OAS 3.1.0 version of the standard sodastore spec that exercises the following 3.1 features:

- `info.summary` and `info.license.identifier` (SPDX)
- `type: ["string", "null"]` (Gap 1 / Gap 3 — type as array)
- `exclusiveMinimum: 0` / `exclusiveMaximum: 5` as numeric values (Gap 2)
- `$ref` with a sibling `description` (Gap 4)
- `anyOf: [{$ref: ...}, {type: "null"}]` on a schema property (Gap 10)

### `DataModelGeneratorUnitTest/sodastore-31-mutual-tls.yaml`

An OAS 3.1.0 spec with a `type: mutualTLS` security scheme in `components/securitySchemes` (Gap 13).
Verifies the generator does not crash on the new security scheme type.

### `ValidationKeywordsPreprocessorTest/sodastore-31-nullable.json`

An OAS 3.1.0 spec with null-union `anyOf` patterns in both a path request body and a component
schema. Used by the new preprocessor tests to verify Gap 10.

---

## New Tests

### `DataModelGeneratorUnitTest`

| Test | What it verifies |
|---|---|
| `testSuccessfulGenerationWithOas31Spec` | Full generation from `sodastore-31.yaml` succeeds and produces files |
| `testSuccessfulGenerationWithMutualTlsSecurityScheme` | Generation with `mutualTLS` security scheme does not throw |

### `ValidationKeywordsPreprocessorTest`

| Test | What it verifies |
|---|---|
| `testOas31NullUnionAnyOfInPaths_isAllowed` | Null-union `anyOf` in a path request body is not blocked (Gap 10) |
| `testOas31NullUnionAnyOfInSchemas_isAllowed` | Null-union `anyOf` in a component schema is not blocked (Gap 10) |
| `testNonNullUnionOneOfInPaths_isBlocked` | A `oneOf` with two `$ref` items (no null type) in a path is still blocked without the `oneOfAnyOfGenerationEnabled` flag |

---

## What Is Not Changed (Out of Scope)

| Gap | Reason deferred |
|---|---|
| Gap 9 — `if/then/else`, `prefixItems`, `$dynamicRef` | No Java equivalent; left to upstream `openapi-generator` |
| Gap 12 — `info.summary` / `license.identifier` in metadata | No code generation impact; metadata output not changed |
| Gap 13 — `mutualTLS` test coverage | Covered by smoke test above; no custom codegen needed |
| Gap 14 — Server `summary` field | No code generation impact |
| Gap 15 — `jsonSchemaDialect` field | Uncommon; handled by upstream parser |
