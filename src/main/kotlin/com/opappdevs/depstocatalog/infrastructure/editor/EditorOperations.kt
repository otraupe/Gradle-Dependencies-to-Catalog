package com.opappdevs.depstocatalog.infrastructure.editor

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager

class EditorOperations {
    companion object {
        fun determineOperatingLines(editor: Editor): Pair<Int, Int> {
            val selectionModel = editor.selectionModel
            val caretModel = editor.caretModel
            val document = editor.document

            //return Pair(startLine, endLine)
            return if (selectionModel.hasSelection()) {
                Pair(
                    document.getLineNumber(selectionModel.selectionStart),
                    document.getLineNumber(selectionModel.selectionEnd)
                )
            } else {
                val line = document.getLineNumber(caretModel.offset)
                Pair(line, line)
            }
        }

        fun insertAndFormatResult(
            editor: Editor,
            document: Document,
            project: Project,
            startOffset: Int,
            endOffset: Int,
            convertedText: String
        ) {
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(startOffset, endOffset, convertedText)
            }
            val newEndOffset = startOffset + convertedText.length
            editor.selectionModel.setSelection(startOffset, newEndOffset)

            // Commit document before adjusting indentation
            PsiDocumentManager.getInstance(project).commitDocument(document)

            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
            if (psiFile != null) {
                WriteCommandAction.runWriteCommandAction(project) {
                    val codeStyleManager = CodeStyleManager.getInstance(project)
                    codeStyleManager.adjustLineIndent(psiFile, TextRange(startOffset, newEndOffset))
                }
            }
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }
}

