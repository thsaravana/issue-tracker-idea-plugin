package com.madrapps.issuetracker.listissues;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface to establish a MVP pattern
 * <p>
 * Created by Henry on 10/18/2016.
 */
public interface IListIssuesContract {

    /**
     * Interface to describe all actions that can be performed on the ToolWindow
     */
    interface IView {
        /**
         * Updates the Issue table with the issues. This can either add issues to existing ones or perform a
         * force update and add from scratch
         *
         * @param issuesList  the list of issues to be added
         * @param forceUpdate if true will clear the table and add the issues, if false will add the issues to
         *                    the existing issues in the table
         */
        void updateIssueList(@NotNull List<Task> issuesList, boolean forceUpdate);

        void init(@NotNull Project project);

        /**
         * Show the summary for the selected issue in the summary panel
         */
        void showSummary(@Nullable String description, @Nullable String issueUrl, @Nullable Comment[] comments);

        void showLoadingScreen(boolean shouldShow);

        void showEmptyIssueListScreen();

        void openInBrowser(@NotNull String issueUrl);

        void showDetailsPanel(boolean shouldShow);

        @Nullable
        Task getSelectedIssue();

        boolean isDetailsPanelShown();
    }

    /**
     * Interface to describe all actions that can be invoked from ToolWindow
     */
    interface IPresenter {

        void pullIssues(@NotNull Project project, @Nullable String query);

        void pullIssues(@NotNull Project project, @Nullable String query, int offset, int limit);

        void showSummary(@NotNull Task selectedIssue);

        void loadInitialIssues(@NotNull Project project);

        @NotNull
        IView setView(@NotNull Project project);

        @NotNull
        IView setView(@NotNull IView view);

        void openUrl(@NotNull Task selectedIssue);

        void showDetailsPanel(boolean shouldShow);

        boolean isDetailsPanelShown();
    }
}
