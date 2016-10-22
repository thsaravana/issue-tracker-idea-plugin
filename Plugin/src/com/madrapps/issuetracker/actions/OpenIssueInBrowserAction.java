package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.madrapps.issuetracker.listissues.IListIssuesContract;
import com.madrapps.issuetracker.listissues.ListIssuesPresenter;

import org.jetbrains.annotations.Nullable;

/**
 * This will open the IssueUrl of the issue on the Browser
 * <p>
 * Created by Henry on 10/22/2016.
 */
public class OpenIssueInBrowserAction extends AnAction {

    public static final String ACTION_ID = "IssueTracker.OpenIssueInBrowser";

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        final Task selectedIssue = getSelectedIssue(e);
        presentation.setEnabled(selectedIssue != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Task selectedIssue = getSelectedIssue(e);
        if (selectedIssue != null) {
            ListIssuesPresenter.getInstance().openUrl(selectedIssue);
        }
    }

    /**
     * Get the issue currently selected, or null if nothing is selected
     *
     * @param e the actionEvent to get the Project
     * @return selected issue or null
     */
    @Nullable
    private Task getSelectedIssue(AnActionEvent e) {
        final Project project = e.getProject();
        if (project != null) {
            final IListIssuesContract.IView view = ListIssuesPresenter.getInstance().setView(project);
            return view.getSelectedIssue();
        }
        return null;
    }
}

