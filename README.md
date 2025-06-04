# NextBindings

![GitHub Release](https://img.shields.io/github/v/release/NextFTC/NextBindings?sort=semver&label=version)
![GitHub Repo stars](https://img.shields.io/github/stars/NextFTC/NextBindings?style=flat)
![GitHub last commit](https://img.shields.io/github/last-commit/NextFTC/NextBindings)

NextBindings is an open-source bindings library for
the [FIRST Tech Challenge](https://www.firstinspires.org/robotics/ftc). It provides a declarative DSL for binding events
to inputs, such as gamepad buttons, joysticks, and limit switches.

The docs are at [nextftc.dev/bindings](https://nextftc.dev/bindings).

## Getting Started

### Prerequisites

Ensure you have a copy of [FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController).

### Installing

In `build.dependencies.gradle`, add the dependency:

```groovy
implementation 'dev.nextftc:bindings:VERSION'
```

Replace `VERSION` with the latest version (shown at the top of this README).

## Basic Usage

Add the following code to your OpMode:

```java
// in loop, or onUpdate with NextFTC:
BindingManager.update();

// in stop, or onStop with NextFTC:
BindingManager.reset();
```

To bind to a button, do:

*Kotlin:*

```kotlin
whenButton { gamepad1.a } isPressed { runSomeCode() }
```

*Java:*

```java
whenButton(() -> gamepad1.a).isPressed(() -> runSomeCode());
```

For more in-depth usage, read the [docs](https://nextftc.dev/bindings).

## Built With

- [Kotlin](https://kotlinlang.org/) - A modern, expressive, statically typed, general-purpose programming language for
  the JVM developed by JetBrains and sponsored by Google.
- [Gradle](https://gradle.org/) – Powerful build system for automating compilation, testing, and publishing
- [Kotest](https://kotest.io/) – Flexible and expressive testing framework for Kotlin
- [MockK](https://mockk.io/) – Mocking library for Kotlin

[//]: # (Uncomment the following once CONTRIBUTING.md is written.)


[//]: # (## Contributing)

[//]: # (Please read [CONTRIBUTING.md]&#40;CONTRIBUTING.md&#41; for details on our code)

[//]: # (of conduct, and the process for submitting pull requests to us.)

## Versioning

We use [Semantic Versioning](http://semver.org/) for versioning. For the versions available, see the [releases on this
repository](https://github.com/NextFTC/NextBindings/releases).

## Authors

- [Davis Luxenberg](https://github.com/beepbot99)

See also the list of
[contributors](https://github.com/NextFTC/NextBindings/contributors)
who participated in this project.

## License

This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html)

You are free to use, modify, and distribute this software under the terms of the GPLv3. Any derivative work must also be
distributed under the same license.

See the [LICENSE](LICENSE) for more information.
