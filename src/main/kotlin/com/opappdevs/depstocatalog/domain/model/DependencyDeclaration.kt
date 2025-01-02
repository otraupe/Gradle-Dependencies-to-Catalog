package com.opappdevs.depstocatalog.domain.model

data class DependencyDeclaration(
    val group: String,
    val artifactName: String,
    val version: String,
    val versionVariable: String,
    val dependencyLabel: String
) {
    val versionDefinition: String
        get() = "$versionVariable = \"$version\""

    val dependencyDefinition: String
        get() = "$dependencyLabel = { group = \"$group\", name = \"$artifactName\", version.ref = \"$versionVariable\" }"

    val implementationStatement: String
        get() = "implementation(libs.${dependencyLabel.replace('-', '.')})"
}
