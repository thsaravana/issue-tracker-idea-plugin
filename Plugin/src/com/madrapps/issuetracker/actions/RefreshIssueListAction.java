package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.ui.content.Content;
import com.madrapps.issuetracker.listissues.IListIssuesContract;
import com.madrapps.issuetracker.listissues.IssuesToolWindow;

import java.util.List;

import javax.swing.JComponent;

/**
 * Action to reload the issues from the task repository
 * <p>
 * Created by Henry on 10/19/2016.
 */
public class RefreshIssueListAction extends AnAction {

    public static final String ACTION_ID = "IssueTracker.RefreshIssueList";

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project != null) {
            final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(IssuesToolWindow.TOOL_WINDOW_ID);
            final Content content = toolWindow.getContentManager().getContent(0);
            if (content != null) {
                final JComponent issueToolWindow = content.getComponent();
                if (issueToolWindow instanceof IListIssuesContract.IView) {
                    final TaskManager component = project.getComponent(TaskManager.class);
                    if (component != null) {
                        final List<Task> issuesList = component.getIssues(null);
                        if (issuesList != null) {
                            ((IListIssuesContract.IView) issueToolWindow).updateIssueList(issuesList, false);
                        }
                    }
                }
            }
        }
    }
}
