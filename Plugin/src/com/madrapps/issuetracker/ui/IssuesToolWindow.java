package com.madrapps.issuetracker.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Henry on 10/17/2016.
 */
public class IssuesToolWindow implements ToolWindowFactory {
    private final static ColumnInfo<Task, String> PRESENTABLE_NAME = new ColumnInfo<Task, String>("Name") {
        public String valueOf(Task object) {
            return object.getPresentableName();
        }

        public Comparator<Task> getComparator() {
            return (o, o1) -> o.getPresentableName().compareTo(o1.getPresentableName());
        }
    };
    private final static ColumnInfo<Task, Icon> ICON = new ColumnInfo<Task, Icon>("") {
        @Nullable
        @Override
        public Icon valueOf(Task task) {
            return task.getIcon();
        }

        @Nullable
        @Override
        public Comparator<Task> getComparator() {
            return (o, o1) -> {
                if (o != null && o1 != null) {
                    return o.getIcon().toString().compareTo(o1.getIcon().toString());
                } else {
                    return 0;
                }
            };
        }

        @Override
        public Class<?> getColumnClass() {
            return Icon.class;
        }

        @Override
        public int getWidth(JTable table) {
            return 50;
        }
    };
    private final static ColumnInfo<Task, String> CREATED_AT = new ColumnInfo<Task, String>("Created On") {
        public String valueOf(Task object) {
            return getValueOfDate(object.getCreated());
        }

        public Comparator<Task> getComparator() {
            return (o, o1) -> Comparing.compare(o.getCreated(), o1.getCreated());
        }
    };
    private final static ColumnInfo<Task, String> LAST_UPDATED = new ColumnInfo<Task, String>("Last Updated On") {
        public String valueOf(Task object) {
            return getValueOfDate(object.getUpdated());
        }

        public Comparator<Task> getComparator() {
            return (o, o1) -> Comparing.compare(o.getUpdated(), o1.getUpdated());
        }
    };
    private JPanel mContentPanel;
    private TableView<Task> mIssuesTable;
    private JTextPane mIssueSummaryTextPane;

    @NotNull
    private static String getValueOfDate(@Nullable Date date) {
        String formattedDate;
        if (date != null) {
            formattedDate = DateFormatUtil.formatPrettyDateTime(date);
        } else {
            formattedDate = "---";
        }
        return formattedDate;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle("Issue Tracker");

        final SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        final Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
        panel.setContent(mContentPanel);
        initUI(project);
    }

    private void initUI(Project project) {
        final ColumnInfo[] columnsNames = {ICON, PRESENTABLE_NAME, CREATED_AT, LAST_UPDATED};
        List<Task> items = new ArrayList<>();

        if (project != null) {
            final TaskManager taskManager = project.getComponent(TaskManager.class);
            if (taskManager != null) {
                List<Task> issues = taskManager.getIssues(null);
                for (Task issue : issues) {
                    items.add(issue);
                }
            }
        }

        mIssuesTable.setModelAndUpdateColumns(new ListTableModel<>(columnsNames, items, 0));
        mIssuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mIssuesTable.setRowSelectionAllowed(true);
        mIssuesTable.getSelectionModel().setSelectionInterval(0, 0);
        showSummary();
        mIssuesTable.getSelectionModel().addListSelectionListener(e -> {
            showSummary();
        });
    }

    private void showSummary() {
        Task selectedIssue = mIssuesTable.getSelectedObject();
        if (selectedIssue != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<style>")
                    .append(".comment {color: #000; background-color: #ddffff; padding: 5px 10px; border-left: 6px solid #ccc; display:inline}")
                    .append("p {padding-left:10px;}")
                    .append("</style>");
            String description = selectedIssue.getDescription();
            stringBuilder.append("<p>").append(description).append("</p><br/>");
            final Comment[] comments = selectedIssue.getComments();
            for (Comment comment : comments) {
                stringBuilder.append("<div class=\"comment\">")
                        .append("<div class=\"comment_author_date\"><strong>")
                        .append(comment.getAuthor()).append(" ")
                        .append(getHtmlDate(comment))
                        .append("</strong></div>")
                        .append(comment.getText())
                        .append(" ")
                        .append("</div><br/>");
            }
            mIssueSummaryTextPane.setText(stringBuilder.toString());
        }
    }

    @NotNull
    private String getHtmlDate(Comment comment) {
        String formattedDate = "";
        final Date date = comment.getDate();
        if (date != null) {
            formattedDate = "(" + DateFormatUtil.formatPrettyDateTime(date) + ")";
        }
        return formattedDate;
    }
}
