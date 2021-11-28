# Redux-Kotlin - Select

![badge][badge-android]
![badge][badge-native]
![badge][badge-js]
![badge][badge-jvm]
![badge][badge-linux]
![badge][badge-windows]
![badge][badge-mac]
![badge][badge-wasm]

A Redux library for memoized state selectors.
Forked from [Reselect](https://github.com/reduxkotlin/Reselect) implementation.
This repo deviates from the "original" repo considerably so please keep reeading.

Example usage:

Subscribe to a single sub state:

```
// Selecting to sub state subscribes to changes in that sub state.
// The Lambda will be executed when there isLoading` is changing.
val subscription: StoreSubscriber = store.select({ it.isLoading }) { isLoading ->
    loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
}

// Invoking the subcription does unsubscribe. Do this when appropriate for component lifecycle
subscription()  
```

Subscribe to multiple sub states:
```
val multiSubscription = store.selectors {
    select({ it.isLoading }) { isLoading ->
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    select({ it.name }) { name ->
        nameTextView.text = name
    }
}

// Unsubscribe when appropriate
multiSubscription()
```
__How to add to project:__

Requires Kotlin 1.4.0.
Artifacts are hosted on maven central.  For multiplatform, add the following to your shared module:
```
kotlin {
    sourceSets {
       commonMain { //   <---  name may vary on your project
           dependencies {
               implementation("com.1gravitytlin:redux-kotlin-select:0.6.0-SNAPSHOT"
           }
       }
   }
```

For JVM only:
```
    implementation("com.1gravitytlin:redux-kotlin-select-JVM:0.6.0-SNAPSHOT"
```

[badge-android]: http://img.shields.io/badge/platform-android-brightgreen.svg?style=flat
[badge-native]: http://img.shields.io/badge/platform-native-lightgrey.svg?style=flat	
[badge-native]: http://img.shields.io/badge/platform-native-lightgrey.svg?style=flat
[badge-js]: http://img.shields.io/badge/platform-js-yellow.svg?style=flat
[badge-js]: http://img.shields.io/badge/platform-js-yellow.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/platform-jvm-orange.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/platform-jvm-orange.svg?style=flat
[badge-linux]: http://img.shields.io/badge/platform-linux-important.svg?style=flat
[badge-linux]: http://img.shields.io/badge/platform-linux-important.svg?style=flat 
[badge-windows]: http://img.shields.io/badge/platform-windows-informational.svg?style=flat
[badge-windows]: http://img.shields.io/badge/platform-windows-informational.svg?style=flat
[badge-mac]: http://img.shields.io/badge/platform-macos-lightgrey.svg?style=flat
[badge-mac]: http://img.shields.io/badge/platform-macos-lightgrey.svg?style=flat
[badge-wasm]: https://img.shields.io/badge/platform-wasm-darkblue.svg?style=flat
