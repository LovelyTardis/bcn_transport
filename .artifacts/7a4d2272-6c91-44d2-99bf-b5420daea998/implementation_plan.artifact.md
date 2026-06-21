# Fix Gradle Sync Error: Kotlin Extension Conflict

The project is using **Android Gradle Plugin (AGP) 9.2.1**, which includes **built-in Kotlin support** enabled by default. This built-in support automatically registers the `kotlin` extension. The error `Cannot add extension with name 'kotlin'` occurs because the `org.jetbrains.kotlin.android` plugin is also being applied manually, leading to a registration conflict.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///D:/Programming/bcn_transport/app/build.gradle.kts)
- Remove `id("org.jetbrains.kotlin.android")` from the `plugins` block.
- Remove the redundant `tasks.withType<KotlinCompile>` configuration, as AGP 9.0+ automatically aligns Kotlin `jvmTarget` with Android `targetCompatibility`.

#### [MODIFY] [build.gradle.kts](file:///D:/Programming/bcn_transport/build.gradle.kts)
- Remove `id("org.jetbrains.kotlin.android")` from the top-level `plugins` block.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify the issue is resolved.
- Run `./gradlew :app:assembleDebug` to ensure the project still builds and Kotlin files are compiled correctly using the built-in support.

### Manual Verification
- Verify that the IDE no longer shows sync errors.
