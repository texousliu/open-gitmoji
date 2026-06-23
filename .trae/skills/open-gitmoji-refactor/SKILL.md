---
name: "open-gitmoji-refactor"
description: "Guides the phased refactoring of the Open Gitmoji IntelliJ plugin. Invoke when working on model restructuring, Repository pattern, network emoji sources, sync crawler, config panel optimization, or feature completion. Follows the REFACTOR_PLAN.md five-stage plan."
---

# Open Gitmoji Refactor Skill

This skill governs all refactoring work on the Open Gitmoji IntelliJ IDEA plugin. It ensures architectural consistency, testability, and backward compatibility across five phases.

## When to Invoke

- Refactoring model/entity classes (Phase 1)
- Implementing Repository pattern or network emoji sources (Phase 2)
- Building emoji sync/crawler from gitmoji official (Phase 3)
- Optimizing configuration UI panels (Phase 4)
- Completing pending features: i18n, multi-emoji selection, copy templates (Phase 5)

## Core Principles

1. **No breaking changes without migration path.** Old `openEmoji.xml` configs must auto-migrate.
2. **Model layer must not depend on UI or Utils.** Use dependency inversion (interfaces, listeners).
3. **All new code must have unit tests.** Target coverage >= 60% for new modules.
4. **Follow IntelliJ Platform conventions.** Use `SearchableConfigurable`, `DialogPanel`, `JBTable`, `ToolbarDecorator` correctly.
5. **Network operations are async.** Use `Task.Backgroundable` or coroutines; never block EDT.

## Phase Guide

### Phase 1: Entity Refactoring

**Goal:** Replace `OpenEmojiBase -> OpenEmoji -> OpenEmojiInfo` inheritance with flat composition.

**New structure:**
- `OpenEmojiData` (pure data class, immutable)
- `OpenEmoji` (domain object with `source: EmojiSource`, no Icon loading)
- `EmojiConfigState` (UI state wrapper: enabled, dirty)

**Rules:**
- Remove `getIcon()` from `OpenEmoji`. Use `IconLoaderService` instead.
- `equals()` / `hashCode()` based on `code` field, not `emoji`.
- `EmojiSource` enum replaces `isCustom` + `OpenEmojiInfoType`: `DEFAULT, CUSTOM, NETWORK, OVERRIDE`.
- Move `modified()` / `change()` logic out of entity into `EmojiConfigState` or comparator.

**Files to modify:**
- `model/*` — rewrite all
- `utils/OpenEmojiUtils` — update `convert()`, `emojiInfoList()`
- `persistence/OpenEmojiPersistent` — add migration logic
- `dialog/*` — adapt table models
- `contributor/*` — adapt completion logic

### Phase 2: Repository Pattern + Network Sources

**Goal:** Abstract all emoji sources behind `EmojiRepository` interface.

**Interfaces:**
```kotlin
interface EmojiRepository {
    val name: String
    val priority: Int
    fun load(): List<OpenEmoji>
    fun isAvailable(): Boolean
}
```

**Implementations:**
- `BuiltInEmojiRepository` — loads `/emojis.json` from resources
- `LocalEmojiRepository(directory: String)` — loads from user folder
- `NetworkEmojiRepository(url: String)` — downloads via `HttpRequests`, caches to plugin config dir
- `CompositeEmojiRepository` — merges multiple sources by priority

**Merge rules:**
- Same `code` from higher priority source wins: CUSTOM > NETWORK > DEFAULT
- Duplicates within same source: keep first

**Config additions in `OpenEmojiPersistent`:**
```kotlin
var customEmojiUrl: String = ""
var networkEmojiEnabled: Boolean = false
var networkEmojiCacheDurationMinutes: Int = 1440
```

### Phase 3: Emoji Sync / Crawler

**Goal:** Sync emoji definitions from gitmoji official GitHub repo.

**Source URL:**
```
https://raw.githubusercontent.com/carloscuesta/gitmoji/main/packages/gitmojis/src/gitmojis.json
```

**Fallback mirrors:** jsDelivr, Gitee mirror (configurable).

**Service interface:**
```kotlin
interface EmojiSyncService {
    fun checkForUpdate(): SyncResult
    fun sync(): SyncResult
    fun lastSyncTime(): Long?
}
```

**Behavior:**
- Manual sync button in settings + optional auto-check on startup
- Download -> diff against current DEFAULT emojis -> show notification with added/modified/removed counts
- User confirms before applying; custom emojis never overwritten
- Use `Task.Backgroundable` with `ProgressIndicator`

### Phase 4: Config Panel Optimization

**Goal:** Merge `OpenEmojiConfiguration` and `OpenEmojiInfoConfiguration` into a single tabbed panel.

**Tabs:**
- General — trigger with colon, editor support
- Emoji List — search box, type filter, sortable table, batch enable/disable
- Patterns — live preview, drag reorder, import/export JSON
- Sync — auto-check toggle, frequency, manual sync, last sync time

**Emoji List improvements:**
- `TableView` + `ListTableModel` for sorting
- Search filters: code, name, description, cnDescription
- Filter dropdown: ALL / DEFAULT / CUSTOM / OVERRIDE / NETWORK
- Batch actions via toolbar buttons

**Add/Edit Emoji dialog:**
- Two-step wizard: Step 1 (emoji + code), Step 2 (details + icon)
- Code field auto-validates `:xxx:` format

### Phase 5: Feature Completion

**i18n:**
- Extract all Chinese strings to `OpenEmojiBundle.properties`
- Create `OpenEmojiBundle_zh.properties` and `OpenEmojiBundle_en.properties`
- Plugin follows IDE locale automatically

**Multi-emoji selection:**
- In commit completion: allow selecting multiple emojis in one go
- Configurable separator (default space)
- Preset combos user can define

**Custom example copy:**
- Right-click in emoji list: copy Code / Unicode / Full template
- Ctrl+C copies default template

## Testing Requirements

- Every new class in `model` and `repository` must have JUnit 4 tests
- Use `assertEquals`, `assertTrue`, `assertNull` from JUnit
- Mock external dependencies (network, file system) where possible
- Run `./gradlew test` after each phase; all tests must pass before proceeding

## Migration & Compatibility

- `OpenEmojiPersistent.loadState()` must detect old format and auto-convert
- Old `isCustom` + `OpenEmojiInfoType` values map to new `EmojiSource`
- Keep deprecated fields one version with `@Deprecated` annotation
- If migration fails, log error and fall back to built-in defaults

## Code Style

- Kotlin: use `val` by default, `var` only when mutable state is required
- Prefer `data class` for pure data
- Use Kotlin stdlib functions (`map`, `filter`, `groupBy`) over manual loops
- Follow existing package naming: `com.github.texousliu.open.emoji.*`
