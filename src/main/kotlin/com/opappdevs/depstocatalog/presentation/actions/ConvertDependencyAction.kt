package com.opappdevs.depstocatalog.presentation.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.opappdevs.depstocatalog.domain.converter.DependencyConverter
import com.opappdevs.depstocatalog.infrastructure.editor.EditorOperations

class ConvertDependencyAction: AnAction() {

    // used to enable/disable the menu (upon certain conditions; e.g. text selected)
    override fun update(e: AnActionEvent) {
        //super.update(e)
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
    }

    // called when the menu or toolbar is clicked
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val document = editor.document

        val (startLine, endLine) = EditorOperations.determineOperatingLines(editor)
        val (startOffset, endOffset) = DependencyConverter.findDependencyOccurrences(document, startLine, endLine, project)
        val convertedText = DependencyConverter.convertDependencies(document, startOffset, endOffset)
        EditorOperations.insertAndFormatResult(editor, document, project, startOffset, endOffset, convertedText)
    }

    // override when you target 2022.3 or later
    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}