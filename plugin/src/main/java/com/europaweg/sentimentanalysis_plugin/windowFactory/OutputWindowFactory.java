package com.europaweg.sentimentanalysis_plugin.windowFactory;

import com.europaweg.sentimentanalysis_plugin.window.OutputWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class OutputWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        OutputWindow outputWindow = new OutputWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(outputWindow.getOutputPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
