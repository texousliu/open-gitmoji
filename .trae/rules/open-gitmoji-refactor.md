# Open Gitmoji Refactor Rules

These rules enforce architectural consistency, code quality, and backward compatibility during the Open Gitmoji plugin refactoring.

## Architecture Rules

### Model Layer Independence
- `model` package classes MUST NOT import from `utils`, `dialog`, `config`, or `contributor` packages.
- Entity classes MUST NOT perform I/O (file read, network, icon loading).
- Icon loading MUST be handled by a dedicated `IconLoaderService` outside the model layer.

### Repository Pattern
- All emoji sources MUST implement `EmojiRepository` interface.
- `OpenEmojiUtils.defaultEmojis()` and `customEmojis()` MUST be replaced by `BuiltInEmojiRepository` and `LocalEmojiRepository`.
- Network emoji loading MUST use `HttpRequests` (IntelliJ platform) or `java.net.http.HttpClient`, never blocking calls on EDT.

### Data Immutability
- `OpenEmojiData` MUST be a Kotlin `data class` with read-only properties (`val`).
- Mutable state (enabled, dirty) MUST be stored in `EmojiConfigState`, not in the domain object.

## Code Quality Rules

### Testing
- Every new class in `model` and `repository` packages MUST have corresponding JUnit 4 test class.
- Test methods MUST use descriptive names: `shouldXxxWhenYyy` pattern preferred.
- `./gradlew test` MUST pass with zero failures before committing any phase.

### Null Safety
- Prefer non-nullable types. Use `?` only when null is semantically meaningful.
- Collections MUST NOT be null; use empty lists as defaults.

### String Handling
- All user-visible strings MUST go through `OpenEmojiBundle.message()`.
- No hardcoded Chinese or English strings in Kotlin source files.

## Compatibility Rules

### Persistent State Migration
- `OpenEmojiPersistent.loadState()` MUST detect legacy format (pre-refactor) and auto-migrate.
- Migration path: `isCustom=true` -> `EmojiSource.CUSTOM`; `OpenEmojiInfoType.OVERRIDE` -> `EmojiSource.OVERRIDE`.
- If migration fails, log the error and fall back to built-in defaults; never crash.

### Deprecation Window
- Old model classes (`OpenEmojiBase`, `OpenEmojiInfoType`) MUST be kept one release cycle with `@Deprecated`.
- Deprecated classes MUST delegate to new implementations, not duplicate logic.

## UI Rules

### IntelliJ Platform Conventions
- Configuration panels MUST extend `SearchableConfigurable` or use `DialogPanel` DSL.
- Long-running operations (network sync) MUST use `Task.Backgroundable` with progress indicator.
- Tables MUST use `TableView` + `ListTableModel` when sorting is needed.

### EDT Safety
- Network I/O and file system operations MUST NOT run on EDT.
- UI updates after background tasks MUST use `ApplicationManager.getApplication().invokeLater()`.

## Phase Transition Gates

Before moving to the next phase, ALL of the following MUST be true:

1. Current phase code compiles without warnings.
2. `./gradlew test` passes.
3. New classes have >= 60% line coverage.
4. Old model classes still compile (backward compatibility).
5. Manual smoke test passes: open settings, add custom emoji, verify completion works.

## Prohibited Patterns

- ❌ Entity classes with `getIcon()` that calls utility methods.
- ❌ `equals()` based on display fields (`emoji`) instead of identity (`code`).
- ❌ Direct `File` I/O in entity constructors.
- ❌ Blocking network calls on main thread.
- ❌ Hardcoded strings in source code.
- ❌ Mutable state in data classes intended to be immutable.
