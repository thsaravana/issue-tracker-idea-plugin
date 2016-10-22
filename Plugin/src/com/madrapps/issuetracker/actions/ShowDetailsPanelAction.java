package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.madrapps.issuetracker.listissues.ListIssuesPresenter;

/**
 * Show/Hide the details panel in the Tool Window
 * <p>
 * Created by Henry on 10/22/2016.
 */
public class ShowDetailsPanelAction extends ToggleAction {

    /** Action ID. This should be the same as in the Plugin xml */
    public static final String ACTION_ID = "IssueTracker.ShowDetailsPanel";

    @Override
    public boolean isSelected(AnActionEvent e) {
        return ListIssuesPresenter.getInstance().isDetailsPanelShown();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        ListIssuesPresenter.getInstance().showDetailsPanel(state);
    }
}
