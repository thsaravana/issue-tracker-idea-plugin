package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.madrapps.issuetracker.listissues.IListIssuesContract;
import com.madrapps.issuetracker.listissues.ListIssuesPresenter;

/**
 * Show/Hide the details panel in the Tool Window
 * <p>
 * Created by Henry on 10/22/2016.
 */
public class ShowDetailsPanelAction extends ToggleAction {

    public static final String ACTION_ID = "IssueTracker.ShowDetailsPanel";

    @Override
    public boolean isSelected(AnActionEvent e) {
        final IListIssuesContract.IPresenter presenter = ListIssuesPresenter.getInstance();
        return presenter.isDetailsPanelShown();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        final IListIssuesContract.IPresenter presenter = ListIssuesPresenter.getInstance();
        presenter.showDetailsPanel(state);
    }
}
