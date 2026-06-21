# Walkthrough: Fix Kotlin Extension Conflict

I have resolved the Gradle sync error by migrating the project to use the **built-in Kotlin support** provided by Android Gradle Plugin (AGP) 9.2.1.

## Changes Made

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///D:/Programming/bcn_transport/app/build.gradle.kts)
- Removed `id("org.jetbrains.kotlin.android")` from the `plugins` block.
- Removed the `tasks.withType<KotlinCompile>` block as it's no longer necessary with built-in Kotlin (AGP handles `jvmTarget` automatically).

#### [MODIFY] [build.gradle.kts](file:///D:/Programming/bcn_transport/build.gradle.kts)
- Removed the top-level declaration of the `org.jetbrains.kotlin.android` plugin.

## Verification Results

### Automated Tests
- **Gradle Sync:** Successfully completed without errors.
- **Build:** Ran `./gradlew :app:assembleDebug` and the build finished successfully, confirming that Kotlin compilation is working correctly through AGP's built-in support.

> [!NOTE]
> AGP 9.0+ includes a runtime dependency on Kotlin Gradle Plugin (KGP) 2.2.10 by default, so you no longer need to manage the Kotlin plugin version manually unless you want to upgrade to a newer version than what AGP provides.
