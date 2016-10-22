package com.madrapps.issuetracker.listissues;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.ui.content.Content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.JComponent;

/**
 * This is where we will process all requests coming from various actions and from the Tool window itself
 * <p>
 * Created by Henry on 10/20/2016.
 */
public class ListIssuesPresenter implements IListIssuesContract.IPresenter {
    /** Singleton Instance */
    private static final ListIssuesPresenter mInstance = new ListIssuesPresenter();
    /** the view */
    private IListIssuesContract.IView mView;

    @Override
    public void pullIssues(@NotNull Project project, @Nullable String query, boolean force) {
        pullIssues(project, query, 0, 100, force);
    }

    @Override
    public void pullIssues(@NotNull Project project, @Nullable String query, int offset, int limit, boolean force) {
        final TaskManager taskManager = project.getComponent(TaskManager.class);
        if (taskManager != null) {
            final Backgroundable backgroundableTask = new Backgroundable(project, "Syncing Issues...", true) {

                private List<Task> issuesList;

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    issuesList = taskManager.getIssues(query, offset, limit, false, indicator, true);
                }

                @Override
                public void onSuccess() {
                    if (issuesList != null && !issuesList.isEmpty()) {
                        mView.updateIssueList(issuesList, force);
                    } else {
                        mView.showEmptyIssueListScreen();
                    }
                }
            };
            final ProgressIndicator indicator = new BackgroundableProcessIndicator(backgroundableTask);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(backgroundableTask, indicator);
        }
    }

    @Override
    public void showDetails(@NotNull Task selectedIssue) {
        final String description = selectedIssue.getDescription();
        final String issueUrl = selectedIssue.getIssueUrl();
        // Show the description immediately, lets replace this later with the full summary.
        mView.showDetails(description, issueUrl, null);
        final Backgroundable backgroundableTask = new Backgroundable(null, "Getting Comments...", true) {

            private Comment[] comments;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                comments = selectedIssue.getComments();
            }

            @Override
            public void onSuccess() {
                mView.showDetails(description, issueUrl, comments);
            }
        };
        final ProgressIndicator indicator = new BackgroundableProcessIndicator(backgroundableTask);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(backgroundableTask, indicator);
    }

    @Override
    public void loadInitialIssues(@NotNull Project project) {
        mView.showLoadingScreen(true);
        final TaskManager taskManager = project.getComponent(TaskManager.class);
        if (taskManager != null) {
            final Backgroundable backgroundableTask = new Backgroundable(project, "Syncing Issues...", true) {

                private List<Task> issuesList;

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    issuesList = taskManager.getIssues(null, 0, 100, false, indicator, true);
                }

                @Override
                public void onSuccess() {
                    mView.showLoadingScreen(false);
                    if (issuesList != null && !issuesList.isEmpty()) {
                        mView.updateIssueList(issuesList, false);
                    } else {
                        mView.showEmptyIssueListScreen();
                    }
                }
            };
            final ProgressIndicator indicator = new BackgroundableProcessIndicator(backgroundableTask);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(backgroundableTask, indicator);
        }
    }

    @Override
    @NotNull
    public IListIssuesContract.IView setView(@NotNull Project project) {
        if (mView == null) {
            final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(IssuesToolWindow.TOOL_WINDOW_ID);
            final Content content = toolWindow.getContentManager().getContent(0);
            if (content != null) {
                final JComponent issueToolWindow = content.getComponent();
                if (issueToolWindow instanceof IListIssuesContract.IView) {
                    mView = (IListIssuesContract.IView) issueToolWindow;
                }
            }
        }
        return mView;
    }

    @Override
    @NotNull
    public IListIssuesContract.IView setView(@NotNull IListIssuesContract.IView view) {
        mView = view;
        return mView;
    }

    @Override
    public void openUrl(@NotNull Task selectedIssue) {
        final String issueUrl = selectedIssue.getIssueUrl();
        if (issueUrl != null) {
            mView.openInBrowser(issueUrl);
        }
    }

    @Override
    public void showDetailsPanel(boolean shouldShow) {
        mView.showDetailsPanel(shouldShow);
    }

    @Override
    public boolean isDetailsPanelShown() {
        return mView.isDetailsPanelShown();
    }

    /**
     * Get an instance of the Presenter
     *
     * @return the singleton instance
     */
    public static ListIssuesPresenter getInstance() {
        return mInstance;
    }
}
