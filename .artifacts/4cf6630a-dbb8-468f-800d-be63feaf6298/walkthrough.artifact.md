# Walkthrough - Fix Unresolved Reference 'padding'

I have fixed the compilation error in `ArrivalRow.kt` where the `padding` modifier was being used without the necessary import.

## Changes Made

### UI Components

#### [ArrivalRow.kt](file:///D:/Programming/bcn_transport/app/src/main/java/com/example/wearosbarcelona/ui/components/ArrivalRow.kt)

- Added the missing import for `androidx.compose.foundation.layout.padding`.

## Verification Results

### Automated Tests
- Ran `:app:compileDebugKotlin` and it finished successfully.

```
$ ./gradlew :app:compileDebugKotlin
BUILD SUCCESSFUL in 2s
```
