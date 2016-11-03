package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.intellij.tasks.actions.OpenTaskDialog;
import com.madrapps.issuetracker.listissues.IListIssuesContract;
import com.madrapps.issuetracker.listissues.ListIssuesPresenter;

/**
 * This will show the Create Task dialog to create a new task for the selected Issue. If the Version Control
 * is already enabled, then we would get the option to create a changelist.
 * <p>
 * Created by Henry on 10/25/2016.
 */
public class CreateTaskAction extends AnAction {

    public static final String ACTION_ID = "IssueTracker.CreateTask";

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project != null) {
            final ListIssuesPresenter presenter = ListIssuesPresenter.getInstance();
            final IListIssuesContract.IView view = presenter.setView(project);

            final Task selectedIssue = view.getSelectedIssue();
            if (selectedIssue != null) {
                final OpenTaskDialog dialog = new OpenTaskDialog(project, selectedIssue);
                dialog.show();
            }
        }
    }
}
