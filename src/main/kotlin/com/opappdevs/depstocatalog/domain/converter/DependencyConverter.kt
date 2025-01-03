package com.opappdevs.depstocatalog.domain.converter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.TextRange
import com.opappdevs.depstocatalog.domain.model.DependencyDeclaration

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

        fun findDependencyOccurrences(
            document: Document,
            startLine: Int,
            endLine: Int,
            project: Project
        ): Pair<Int, Int> {
            var firstOccurrence: MatchResult? = null
            var lastOccurrence: MatchResult? = null

            for (lineNumber in startLine..endLine) {
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
                Messages.showErrorDialog(project, "No valid dependency declarations found", "Error")
                throw IllegalStateException("No valid dependency declarations found")
            }

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
                .map { createDependencyDeclaration(it) }
                .toList()

            return buildString {
                // Version definitions
                append(declarations.map { it.versionDefinition }.distinct().joinToString("\n"))
                appendLine()

                // Dependency definitions
                append(declarations.map { it.dependencyDefinition }.distinct().joinToString("\n"))
                appendLine()

                // Implementation statements
                append(declarations.map { it.implementationStatement }.distinct().joinToString("\n"))
            }
        }

        private fun createDependencyDeclaration(matchResult: MatchResult): DependencyDeclaration {
            val (group, artifactName, version) = matchResult.destructured
            val versionVariable = group.substringAfterLast('.').lowercase()
            val groupParts = group.split('.')
//            val dependencyLabel = if (groupParts.size >= 2) {
//                "${groupParts[groupParts.size - 2]}${"-"}${groupParts.last()}${"-"}$artifactName".lowercase()
//            } else {
//                "${groupParts.last()}${"-"}$artifactName".lowercase()
//            }
            val dependencyLabel = if (groupParts.size >= 2) {
                groupParts[groupParts.size - 2].trim() + "-" + groupParts.last().trim() + "-" + artifactName.trim()
            } else {
                groupParts.last().trim() + "-" + artifactName.trim()
            }.lowercase()


            return DependencyDeclaration(
                group = group,
                artifactName = artifactName,
                version = version,
                versionVariable = versionVariable,
                dependencyLabel = dependencyLabel.replace('.', '-')
            )
        }
    }
}
