package com.opappdevs.depstocatalog.domain.converter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.opappdevs.depstocatalog.domain.model.CatalogFormat

class DependencyConverter {
    companion object {
        private val DEPENDENCY_REGEX =
            """implementation\s*(?:\(?\s*['"]|['"])([\w.-]+):([\w.-]+):([\w.-]+)(?:['"]?\s*\)?|['"])"""
                .toRegex()

        fun containsDependencyDeclaration(document: Document, startLine: Int, endLine: Int): Boolean {
            val selectionStart = document.getLineStartOffset(startLine)
            val selectionEnd = document.getLineEndOffset(endLine)
            val selectionText = document.getText(TextRange(selectionStart, selectionEnd))
            return DEPENDENCY_REGEX.findAll(selectionText).any()
        }

        fun findDependencyTextRange(
            document: Document,
            startLine: Int,
            endLine: Int
        ): Pair<Int, Int> {
            var firstOccurrence: MatchResult? = null
            var lastOccurrence: MatchResult? = null

            for (lineNumber in startLine..endLine) {    // TODO: remove line breaks, don't iterate
                val lineStart = document.getLineStartOffset(lineNumber)
                val lineEnd = document.getLineEndOffset(lineNumber)
                val lineText = document.getText(TextRange(lineStart, lineEnd))
                val matches = DEPENDENCY_REGEX.findAll(lineText)
                matches.forEach { match ->
                    if (firstOccurrence == null) {
                        firstOccurrence = match
                    }
                    lastOccurrence = match
                }
            }
            if (firstOccurrence == null) {
                //Messages.showErrorDialog(project, "No valid dependency declarations found", "Error")
                throw IllegalStateException("No valid dependency declarations found")
            }
            // TODO: BUG: startLine and endLine must be lines with matches
            val startOffset = document.getLineStartOffset(startLine) + (firstOccurrence!!.range.first)
            val endOffset = document.getLineStartOffset(endLine) + (lastOccurrence!!.range.last) + 1
            return Pair(startOffset, endOffset)
        }

        fun convertDependencies(
            document: Document,
            startOffset: Int,
            endOffset: Int
        ): String {
            val selectedText = document.getText(TextRange(startOffset, endOffset))
            val textWithoutLineBreaks = selectedText.replace(Regex("\\R"), "")

            val declarations = DEPENDENCY_REGEX.findAll(textWithoutLineBreaks)
                .map { createCatalogFormat(it) }
                .toList()

            return buildString {
                // version definitions
                append(declarations.map { it.versionDefinition }.distinct().joinToString("\n"))
                appendLine()
                // dependency definitions
                append(declarations.map { it.dependencyDefinition }.distinct().joinToString("\n"))
                appendLine()
                // dependency declaration
                append(declarations.map { it.dependencyDeclaration }.distinct().joinToString("\n"))
            }
        }

        private fun createCatalogFormat(matchResult: MatchResult): CatalogFormat {
            val (group, artifact, version) = matchResult.destructured
            val versionLabel = group.substringAfterLast('.').lowercase()
            val groupParts = group.split('.')

            val labelBase = "${groupParts.last()}-$artifact"
            val definitionLabel = if (groupParts.size >= 2) {
                "${groupParts[groupParts.size - 2]}-$labelBase"
            } else {
                labelBase
            }.lowercase()

            return CatalogFormat(
                group = group,
                artifact = artifact,
                version = version,
                versionLabel = versionLabel,
                definitionLabel = definitionLabel
            )
        }
    }
}
