package com.madrapps.issuetracker.listissues;

import com.intellij.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * This will act as the data source for the Issues Table in the Tool Window
 * <p>
 * Created by Henry on 11/5/2016.
 */
class ListIssuesDataSource implements IListIssuesContract.IDataSource {

    /**
     * This is the list that will act as the data source
     */
    private List<Task> mFullIssuesList;

    /**
     * Constructor
     */
    ListIssuesDataSource() {
        mFullIssuesList = new ArrayList<>();
    }

    @Override
    public void refreshIssues(List<Task> issues) {
        mFullIssuesList.clear();
        mFullIssuesList.addAll(issues);
    }

    @Override
    public void updateIssues(List<Task> issues) {
        issues.forEach(task -> {
            final int index = mFullIssuesList.indexOf(task);
            if (index != -1) {
                mFullIssuesList.set(index, task);
            } else {
                mFullIssuesList.add(task);
            }
        });
    }

    @Override
    public List<Task> getAllIssues() {
        return mFullIssuesList;
    }
}
