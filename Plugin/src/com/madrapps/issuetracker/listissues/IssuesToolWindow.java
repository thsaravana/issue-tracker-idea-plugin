package com.madrapps.issuetracker.listissues;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import org.jetbrains.annotations.NotNull;

/**
 * This is the Tool Window Factory to create the ToolWindow of the Issue Tracker plugin.
 * <p>
 * Created by Henry on 10/17/2016.
 */
public class IssuesToolWindow implements ToolWindowFactory {
    public static final String TOOL_WINDOW_ID = "Issue Tracker";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle(TOOL_WINDOW_ID);

        final IssuesToolWindowPanel panel = new IssuesToolWindowPanel(toolWindow);
        panel.init();
    }

}
