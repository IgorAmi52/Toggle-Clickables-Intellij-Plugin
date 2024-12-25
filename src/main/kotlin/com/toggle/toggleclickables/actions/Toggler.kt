package com.toggle.toggleclickables.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.Font
import javax.swing.*

class Toggler : AnAction() {
    private val originalStyles = mutableMapOf<JComponent, Pair<Font?, Color?>>()
    private var isHighlighted = false

    override fun actionPerformed(e: AnActionEvent) {
        if (isHighlighted) {
            resetHighlightedComponents()
        } else {
            val viewFrame = WindowManager.getInstance().getFrame(e.project) ?: return
            highlightMatchingComponents(viewFrame)
        }
        isHighlighted = !isHighlighted
    }

    private fun highlightMatchingComponents(container: Component) {
        if (container is JComponent) {
            if (container is AbstractButton || container is JComboBox<*>
                || container is JList<*> || container is JLabel
                || container is JTree
            ) {
                originalStyles[container] = container.font to container.foreground

                container.isOpaque = true
                container.font = container.font.deriveFont(Font.BOLD, 14f)
                container.foreground = JBColor.GREEN

                container.revalidate()
                container.repaint()
            }
        }

        if (container is Container) {
            for (child in container.components) {
                highlightMatchingComponents(child)
            }
        }
    }

    private fun resetHighlightedComponents() {
        for ((component, style) in originalStyles) {
            component.font = style.first
            component.foreground = style.second
            component.isOpaque = false
            component.revalidate()
            component.repaint()
        }
        originalStyles.clear()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}