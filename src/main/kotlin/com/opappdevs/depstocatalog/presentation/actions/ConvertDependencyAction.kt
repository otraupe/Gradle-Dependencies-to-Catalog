package com.opappdevs.depstocatalog.presentation.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.opappdevs.depstocatalog.domain.converter.DependencyConverter
import com.opappdevs.depstocatalog.infrastructure.editor.EditorOperations

class ConvertDependencyAction: AnAction() {

    // disable the menu item if no matches
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor?.let {
            val (startLine, endLine) = EditorOperations.determineOperatingLines(it)
            DependencyConverter.containsDependencyDeclaration(it.document, startLine, endLine)
        } ?: false
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document

        val (startLine, endLine) = EditorOperations.determineOperatingLines(editor)
        val (startOffset, endOffset) = DependencyConverter.findDependencyTextRange(document, startLine, endLine)
        val convertedText = DependencyConverter.convertDependencies(document, startOffset, endOffset)
        EditorOperations.insertAndFormatResult(editor, document, project, startOffset, endOffset, convertedText)
    }

    // override when you target 2022.3 or later
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT // background thread (vs. EDT -> event driven)
    }
}