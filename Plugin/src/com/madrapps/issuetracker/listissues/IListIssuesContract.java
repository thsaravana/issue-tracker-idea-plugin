package com.madrapps.issuetracker.listissues;

import com.intellij.tasks.Task;

import org.jetbrains.annotations.NotNull;

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
    }

    /**
     * Interface to describe all actions that can be invoked from ToolWindow
     */
    interface IPresenter {

    }
}