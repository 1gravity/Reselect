# Redux-Kotlin - Select

![CI](https://github.com/reduxkotlin/Reselect/workflows/PR/badge.svg)

![badge][badge-android]
![badge][badge-native]
![badge][badge-js]
![badge][badge-jvm]
![badge][badge-linux]
![badge][badge-windows]
![badge][badge-mac]
![badge][badge-wasm]

A Redux Reselect implementation for memoized dispatch on state selectors.  Forked from [Reduks](https://github.com/beyondeye/Reduks) implementation.

Documentation to come.

Example usage:

Subscribe to a single substate:

```
//selecting a substate subscribes to changes.  Lambda block will be executed when there is 
//a change in value of `isLoading`
val subscriber: StoreSubscriber = store.select({ it.isLoading }) {
        loadingIndicator.visibility = if (store.state.isLoading) View.VISIBLE else View.GONE
    }

//invoking subcription unsubscribes.  Do this when appropriate for component lifecycle
subscriber()  
```

Subscribe to multiple substates:
```
val multiSubscription = store.selectors {
            select({ it.isLoading }) {
                loadingIndicator.visibility = if (store.state.isLoading) View.VISIBLE else View.GONE
            }
            select({ it.name }) {
                nameTextView.text = store.state.name
            }
        }

//unsubscribe when appropriate
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
                implementation "org.reduxkotlin:reselect:0.5.5"
            }
        }
 }
```

For JVM only:
```
  implementation "org.reduxkotlin:reselect-jvm:0.5.5"
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
