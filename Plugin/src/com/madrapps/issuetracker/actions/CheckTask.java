package com.madrapps.issuetracker.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;

import java.util.List;

/**
 * Created by Henry on 10/17/2016.
 */
public class CheckTask extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            final TaskManager taskManager = project.getComponent(TaskManager.class);
            if (taskManager != null) {
                List<Task> issues = taskManager.getIssues(null);
                for (Task issue : issues) {
                    System.out.println(issue.getCreated());
                    System.out.println(issue.getDescription());
                    System.out.println(issue.getIssueUrl());
                    System.out.println(issue.getPresentableName());
                    Comment[] comments = issue.getComments();
                    for (Comment comment : comments) {
                        System.out.print("Comment : ");
                        System.out.println(comment.getText() + " : " + comment.getAuthor());
                    }
                    System.out.println(issue.getCustomIcon());
                    System.out.println(issue.getId());
                    System.out.println(issue.getNumber());
                    System.out.println(issue.getPresentableId());
                    System.out.println(issue.getProject());
                    System.out.println(issue.getSummary());
                    System.out.println(issue.getType());
                    System.out.println(issue.getRepository());
                    System.out.println(issue.getIcon());
                    System.out.println(issue.getState());
                    System.out.println(issue.isIssue());
                    System.out.println(issue.isClosed());
                }
            } else {
                System.out.println("TaskManager is NULL");
            }
        } else {
            System.out.println("Project is NULL");
        }
    }
}
