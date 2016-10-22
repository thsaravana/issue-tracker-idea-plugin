package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.madrapps.issuetracker.listissues.IListIssuesContract.IPresenter;
import com.madrapps.issuetracker.listissues.ListIssuesPresenter;

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
            final IPresenter presenter = ListIssuesPresenter.getInstance();
            presenter.setView(project);
            presenter.pullIssues(project, null, true);
        }
    }
}
