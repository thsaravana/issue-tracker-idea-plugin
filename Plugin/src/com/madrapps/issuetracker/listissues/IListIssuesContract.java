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
         * Clears the search field
         */
        void clearSearchField();

        /**
         * Saves the text in search field to the search history
         */
        void saveSearchToHistory();

        /**
         * Updates the Issue table with the issues. This can either add issues to existing ones or perform a
         * force update and add from scratch
         *
         * @param issuesList the list of issues to be added
         */
        void updateIssueList(@NotNull List<Task> issuesList);

        /**
         * Initializes the ToolWindow components and their actions
         *
         * @param project the current project
         */
        void init(@NotNull Project project);

        /**
         * Show the details for the selected issue in the details panel
         *
         * @param description The description of the issue
         * @param issueUrl    the IssueUrl of the issue
         * @param comments    the comments of the issue
         */
        void showDetails(@Nullable String description, @Nullable String issueUrl, @Nullable Comment[] comments);

        /**
         * Decides whether we should show the Loading component or the Issue Table component
         *
         * @param shouldShow true to show the Loading component, false to show the Table component
         */
        void showLoadingScreen(boolean shouldShow);

        /**
         * Show the Empty issues list instead of the Table
         */
        void showEmptyIssueListScreen();

        /**
         * Opens the issue url in the browser
         *
         * @param issueUrl the url of the issue
         */
        void openInBrowser(@NotNull String issueUrl);

        /**
         * Show/Hide the details panel
         *
         * @param shouldShow if true, show the details panel, if false hide it
         */
        void showDetailsPanel(boolean shouldShow);

        /**
         * Get the selected issue in the Issue list table
         *
         * @return the selected issue in the issue list, or null if nothing is selected
         */
        @Nullable
        Task getSelectedIssue();

        /**
         * Determines if the details panel is shown or not
         *
         * @return true if it is shown, false otherwise
         */
        boolean isDetailsPanelShown();
    }

    /**
     * Interface to describe all actions that can be invoked from ToolWindow
     */
    interface IPresenter {

        /**
         * Show all issues in the issues table
         */
        void showAllIssues();

        /**
         * Search for issues with a query string
         *
         * @param query   the text to search
         * @param project the project
         */
        void searchForIssues(@NotNull String query, @NotNull Project project);

        /**
         * Pull issues asynchronously from the server
         *
         * @param project the current project
         * @param query   the search query
         * @param force   if true, force sync all issues, if false adds new issues to existing list
         */
        void pullIssues(@NotNull Project project, @Nullable String query, boolean force);

        /**
         * Pull issues asynchronously from the server
         *
         * @param project the current project
         * @param query   the search query
         * @param offset  the offset (mostly 0)
         * @param limit   the limit
         * @param force   if true, force sync all issues, if false adds new issues to existing list
         */
        void pullIssues(@NotNull Project project, @Nullable String query, int offset, int limit, boolean force);

        /**
         * Show the details of a particular issue
         *
         * @param selectedIssue the issue
         */
        void showDetails(@NotNull Task selectedIssue);

        /**
         * Load the issues when the Tool window is first activated
         *
         * @param project the current project
         */
        void loadInitialIssues(@NotNull Project project);

        /**
         * Sets The ToolWindow view to the presenter, so that the presenter has a reference to it
         *
         * @param project the current project
         * @return the view
         */
        @NotNull
        IView setView(@NotNull Project project);

        /**
         * Sets The ToolWindow view to the presenter, so that the presenter has a reference to it
         *
         * @param view the view
         * @return the view
         */
        @NotNull
        IView setView(@NotNull IView view);

        /**
         * Opens the issue url in the browser
         *
         * @param selectedIssue the issue
         */
        void openUrl(@NotNull Task selectedIssue);

        /**
         * Show/Hide the details panel
         *
         * @param shouldShow if true show the panel, hide otherwise
         */
        void showDetailsPanel(boolean shouldShow);

        /**
         * Determine if the details panel is shown
         *
         * @return true if shown, false otherwise
         */
        boolean isDetailsPanelShown();
    }

    /**
     * Data source for the issues
     */
    interface IDataSource {
        /**
         * Reloads the issues freshly
         *
         * @param issues the list of issues
         */
        void refreshIssues(List<Task> issues);

        /**
         * Adds new issues and updates existing issues
         *
         * @param issues the list of issues
         */
        void updateIssues(List<Task> issues);

        /**
         * Return the list of all issues
         *
         * @return list of issues
         */
        List<Task> getAllIssues();
    }
}
