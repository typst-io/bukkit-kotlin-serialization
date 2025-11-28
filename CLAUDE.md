# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a kotlinx.serialization extension library for Bukkit (Minecraft plugin platform) that enables serialization of Bukkit's ConfigurationSerializable objects to JSON and YAML formats. Published to Maven Central as `io.typst:bukkit-kotlin-serialization`.

## Build Commands

```bash
# Build the project (includes shadowJar)
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "io.typst.bukkit.kotlin.serialization.expression.EvaluatorTest"

# Run a specific test method
./gradlew test --tests "io.typst.bukkit.kotlin.serialization.expression.EvaluatorTest.evaluate"

# Build shadow JAR only
./gradlew shadowJar

# Publish to Maven Central staging (requires credentials)
./gradlew publish
```

## Architecture

### Core Serialization System

The library bridges kotlinx.serialization with Bukkit's ConfigurationSerializable interface through a dual-format approach:

1. **BukkitConfigSerializableSerializer** (`BukkitConfigSerializableSerializer.kt`) - Core serializer that handles all ConfigurationSerializable types
   - Converts between Bukkit's Map-based serialization format and kotlinx.serialization formats
   - Supports both JSON (via JsonEncoder/JsonDecoder) and YAML (via kaml library)
   - Recursively deserializes nested ConfigurationSerializable objects by detecting the `==` type key
   - Provides `serialize()` and `deserializeObject()` methods for converting to/from Bukkit's Map format

2. **Type-specific serializers** - Delegate to BukkitConfigSerializableSerializer:
   - `ItemStackSerializer` - For Bukkit ItemStack objects
   - `VectorSerializer` - For Bukkit Vector objects
   - `UUIDSerializer` - For Java UUID objects

3. **Type aliases for convenience**:
   - `ItemStackSerializable` = `@Serializable(ItemStackSerializer::class) ItemStack`
   - Use these in `@Serializable` data classes to enable automatic serialization

### Plugin Integration Utilities (`plugins.kt`)

Convenience functions for Bukkit plugin developers:

- `JavaPlugin.configJsonFile` / `JavaPlugin.configYamlFile` - Extension properties for config file paths
- `bukkitPluginJson` / `bukkitPluginYaml` - Preconfigured serialization formats
- `readConfigOrCreate<T>()` - Helper to read config or create with defaults

### Mathematical Expression System

A complete expression parser/evaluator in the `expression` package:

- **Lexer** (`Lexer.kt`) - Tokenizes math expressions into tokens (numbers, operators, parens, identifiers)
- **Parser** (`Parser.kt`) - Pratt parser that builds an AST using operator precedence
- **Evaluator** (`Evaluator.kt`) - Evaluates the AST with variable substitution
- **Expression ADT** (`Expression.kt`) - Sealed interface: Literal, Variable, Unary, Binary
- **Operators** (`UnaryOpType.kt`, `BinaryOpType.kt`) - Define operator types and binding powers

This subsystem uses io.vavr's `Either` type for error handling (Either.left for Failure, Either.right for success).

### Additional Serializers

- **Kotlin stdlib**: `IntRangeSerializer`, `LongRangeSerializer`
- **Java time**: `DurationSerializer`, `LocalDateSerializer`, `LocalDateTimeSerializer`, `LocalTimeSerializer`, `PeriodSerializer`
- **Location**: `WorldLocation` - Serializable wrapper for Bukkit Location with world names

## Key Implementation Details

### Serialization Format Detection

The serializers detect the format at runtime:
- `encoder is JsonEncoder` → use JSON conversion methods (`mapToJson`, `jsonToAny`)
- `else` → use YAML conversion methods (`mapToYaml`, `yamlToAny`)

### Bukkit Serialization Requirements

When deserializing ConfigurationSerializable objects, the Map must contain:
- `ConfigurationSerialization.SERIALIZED_TYPE_KEY` (which is `"=="`) with the class alias
- All other keys matching the constructor parameters expected by Bukkit's deserializer

The library automatically adds the `==` key during serialization by calling `ConfigurationSerialization.getAlias()`.

### Recursive Deserialization

BukkitConfigSerializableSerializer recursively processes nested Maps that contain the `==` key, converting them to ConfigurationSerializable objects before passing to Bukkit's deserializer.

## Testing

Tests use JUnit 5 (Jupiter). Key test files:
- `expression/EvaluatorTest.kt` - Tests for math expression evaluation
- `expression/LexerTest.kt` - Tests for tokenization
- `expression/ParserTest.kt` - Tests for parsing
- `ktstd/RangeSerializerTest.kt` - Tests for Kotlin range serializers

## Dependencies

- **Spigot API 1.16.5** (compileOnly) - Bukkit/Spigot server API
- **kotlinx.serialization-json 1.9.0** - JSON serialization
- **kaml-jvm 0.96.0** - YAML serialization for kotlinx.serialization
- **io.vavr 0.10.7** - Functional programming utilities (Either type)

## JDK Version

Project uses JDK 11 (configured via `kotlin.jvmToolchain(11)`).
