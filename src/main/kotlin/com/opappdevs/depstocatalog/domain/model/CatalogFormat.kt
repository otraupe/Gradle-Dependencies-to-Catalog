package com.opappdevs.depstocatalog.domain.model

class CatalogFormat(
    val group: String,
    val artifact: String,
    val version: String,
    val versionLabel: String,
    val definitionLabel: String
) {
    val versionDefinition: String
        get() = "$versionLabel = \"$version\""

    val dependencyDefinition: String
        get() = "$definitionLabel = { group = \"$group\", name = \"$artifact\", version.ref = \"$versionLabel\" }"

    val dependencyDeclaration: String
        get() = "implementation(libs.$definitionLabel)"
}