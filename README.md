# Gradle-Dependencies-to-Catalog
Plugin for IntelliJ Idea and Android Studio that converts direct Gradle dependency declarations into Version Catalog format (TOML).
Can be found as "Gradle Dependencies to Catalog" plugin in the IntelliJ marketplace.

Copy/Paste legacy dependency declarations from Maven Central or tutorials and easily convert them into Version Catalog format.
Simply select/mark the legacy dependencies in your Gradle build file and convert them via the entry in the Edit menu or the default keyboard shortcut: `Ctrl+Alt+Shift+D`

Note: The version label is the last part of the group. The dependency label (second line) is created from the last two parts of the group and the fragment name. This could be configurable in future versions.

## Currently supports the following legacy notations:
- Kotlin: `implementation("com.opappdevs:depstocatalog:1.0.0")`
- Groovy (short): `implementation 'com.opappdevs:depstocatalog:1.0.0'`

## Output
```
opappdevs = "1.0.0"
com-opappdevs-depstocatalog = { group = "com.opappdevs", name = "depstocatalog", version.ref = "opappdevs" }
implementation(libs.com.opappdevs.depstocatalog)
```

## License
Distributed under the MIT license.
