package com.madrapps.issuetracker.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Created by Henry on 10/17/2016.
 */
public class IssuesToolWindow implements ToolWindowFactory {
    private JPanel mContentPanel;
    private JTextPane mIssueSummaryTextPane;
    private JBTable mIssuesTable;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle("Issue Tracker");

        final SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        final Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
        panel.setContent(mContentPanel);

        initUI();
    }

    private void initUI() {
        mIssueSummaryTextPane.setText("Just a sample Text yo yo man");

        String[] columnNames = {"Title", "Platform"};
        final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        mIssuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableModel.addRow(new String[]{"One", "This is one"});
        tableModel.addRow(new String[]{"Two", "This is two"});
        tableModel.addRow(new String[]{"Three", "This is three"});
        tableModel.addRow(new String[]{"Four", "This is four"});
        tableModel.addRow(new String[]{"One", "This is one"});
        tableModel.addRow(new String[]{"Two", "This is two"});
        tableModel.addRow(new String[]{"Three", "This is three"});
        tableModel.addRow(new String[]{"Four", "This is four"});
        tableModel.addRow(new String[]{"One", "This is one"});
        tableModel.addRow(new String[]{"Two", "This is two"});
        tableModel.addRow(new String[]{"Three", "This is three"});
        tableModel.addRow(new String[]{"Four", "This is four"});
        tableModel.addRow(new String[]{"One", "This is one"});
        tableModel.addRow(new String[]{"Two", "This is two"});
        tableModel.addRow(new String[]{"Three", "This is three"});
        tableModel.addRow(new String[]{"Four", "This is four"});
        tableModel.addRow(new String[]{"One", "This is one"});
        tableModel.addRow(new String[]{"Two", "This is two"});
        tableModel.addRow(new String[]{"Three", "This is three"});
        tableModel.addRow(new String[]{"Four", "This is four"});

        mIssuesTable.setModel(tableModel);

        mIssuesTable.getSelectionModel().addListSelectionListener(e -> mIssueSummaryTextPane.setText(mIssuesTable.getSelectedRow() + ""));

        // Default selection to first row
        mIssuesTable.setRowSelectionInterval(0, 0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
