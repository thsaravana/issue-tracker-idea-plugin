package com.madrapps.issuetracker.listissues;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.SearchTextFieldWithStoredHistory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.madrapps.issuetracker.actions.CreateTaskAction;
import com.madrapps.issuetracker.actions.OpenIssueInBrowserAction;
import com.madrapps.issuetracker.actions.RefreshIssueListAction;
import com.madrapps.issuetracker.actions.ShowDetailsPanelAction;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.CardLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

import static com.madrapps.issuetracker.listissues.TableColumns.COLUMN_NAMES;
import static com.madrapps.issuetracker.listissues.TableColumns.getValueOfDate;

/**
 * This is responsible for the ToolWindow GUI. The GUI is backed up be a .form file.
 * <p>
 * Created by Henry on 10/19/2016.
 */
public class IssuesToolWindowPanel extends SimpleToolWindowPanel implements IListIssuesContract.IView {

    /** To parse the Markdown text */
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    /** To render the markdown as html */
    private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder().build();
    /** The root component that holds every component */
    private JPanel mContentPanel;
    /** The table that shows the list of tasks/issues */
    private TableView<Task> mIssuesTable;
    /** The Details panel that shows the details of an issue when it's selected from the table */
    private JTextPane mIssueDetailsTextPane;
    /** The toolbar to hold the actions in the ToolWindow */
    private JPanel mToolbar;
    /** The text field that shows the loading/empty issue message */
    private JFormattedTextField mEmptyMessageTextField;
    /** Panel that holds the IssuesTable */
    private JPanel mIssuesListPanel;
    /** The Details panel component */
    private JPanel mDetailsPanel;
    /** The search field */
    private SearchTextFieldWithStoredHistory mSearchField;
    /** The presenter */
    private ListIssuesPresenter mPresenter;

    /**
     * Constructor
     *
     * @param toolWindow the ToolWindow from the Factory
     */
    IssuesToolWindowPanel(ToolWindow toolWindow) {
        super(true, false);
        final Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().addContent(content);
        this.setContent(mContentPanel);
    }

    @Override
    public void clearSearchField() {
        mSearchField.reset();
    }

    @Override
    public void saveSearchToHistory() {
        mSearchField.addCurrentTextToHistory();
    }

    @Override
    public void updateIssueList(@NotNull List<Task> issuesList) {
        final ListTableModel<Task> model = new ListTableModel<>(COLUMN_NAMES, issuesList, 0);
        mIssuesTable.setModelAndUpdateColumns(model);
        final CardLayout layout = (CardLayout) mIssuesListPanel.getLayout();
        layout.show(mIssuesListPanel, "CardTABLE");
    }

    @Override
    public void init(@NotNull Project project) {
        initializeComponents();
        initializeActions();
        initSearchField(project);
        // Load issues for the first time when the ToolWindow is opened
        mPresenter.loadInitialIssues(project);
    }

    @Override
    public void showDetails(@Nullable String description, @Nullable String issueUrl, @Nullable Comment[] comments) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<style>")
                .append(".comment {color: #000; background-color: #ddffff; padding: 5px 10px; border-left: 6px solid #ccc; display:inline}")
                .append("p {padding-left:10px;}")
                .append("</style>");
        final String formattedDescription = fromMarkDownToHtml(description);
        stringBuilder.append(formattedDescription)
                .append("<br/>");
        if (comments != null) {
            for (Comment comment : comments) {
                stringBuilder.append("<div class=\"comment\">")
                        .append("<div class=\"comment_author_date\"><strong>")
                        .append(comment.getAuthor()).append("&nbsp;")
                        .append("(")
                        .append(getValueOfDate(comment.getDate()))
                        .append(")")
                        .append("</strong></div>")
                        .append(fromMarkDownToHtml(comment.getText()))
                        .append("</div><br/>");
            }
        }
        if (issueUrl != null) {
            stringBuilder.append(String.format(Locale.US, "<a href=%s>%s</a>", issueUrl, issueUrl))
                    .append("<br/>");
        }
        mIssueDetailsTextPane.setText(stringBuilder.toString());
    }

    @Override
    public void showLoadingScreen(boolean shouldShow) {
        final CardLayout layout = (CardLayout) mIssuesListPanel.getLayout();
        if (shouldShow) {
            mEmptyMessageTextField.setText("Loading...");
            layout.show(mIssuesListPanel, "CardEMPTY");
        } else {
            layout.show(mIssuesListPanel, "CardTABLE");
        }
    }

    @Override
    public void showEmptyIssueListScreen() {
        // Show the Empty message instead of the issues table
        final CardLayout layout = (CardLayout) mIssuesListPanel.getLayout();
        mEmptyMessageTextField.setText("You have no Issues in repository");
        layout.show(mIssuesListPanel, "CardEMPTY");

        // Clear the summary
        mIssueDetailsTextPane.setText("");
    }

    @Override
    public void openInBrowser(@NotNull String issueUrl) {
        BrowserUtil.browse(issueUrl);
    }

    @Override
    public void showDetailsPanel(boolean shouldShow) {
        mDetailsPanel.setVisible(shouldShow);
    }

    @Nullable
    @Override
    public Task getSelectedIssue() {
        return mIssuesTable.getSelectedObject();
    }

    @Override
    public boolean isDetailsPanelShown() {
        return mDetailsPanel.isVisible();
    }

    /**
     * This method is invoked by the GUI-FORM to create components.
     * Do not rename.
     */
    public void createUIComponents() {
        mSearchField = new SearchTextFieldWithStoredHistory("IssueTracker.SearchField") {
            @Override
            protected void onFieldCleared() {
                super.onFieldCleared();
                mPresenter.showAllIssues();
            }
        };
        mSearchField.getTextEditor().setColumns(15);
        mSearchField.setHistorySize(5);
    }

    private void initSearchField(@NotNull Project project) {
        mSearchField.getTextEditor().addKeyListener(new KeyAdapter() {
            //to consume enter in combo box - do not process this event by default button from DialogWrapper
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    mPresenter.searchForIssues(mSearchField.getText(), project);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    e.consume();
                    mPresenter.showAllIssues();
                }
            }
        });
    }

    /**
     * Initializes all actions
     */
    private void initializeActions() {
        final AnAction refreshAction = ActionManager.getInstance().getAction(RefreshIssueListAction.ACTION_ID);
        final AnAction openIssueInBrowserAction = ActionManager.getInstance().getAction(OpenIssueInBrowserAction.ACTION_ID);
        final AnAction showDetailsPanelAction = ActionManager.getInstance().getAction(ShowDetailsPanelAction.ACTION_ID);
        final AnAction createTaskAction = ActionManager.getInstance().getAction(CreateTaskAction.ACTION_ID);

        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(openIssueInBrowserAction);
        actionGroup.add(createTaskAction);
        actionGroup.add(refreshAction);
        actionGroup.add(showDetailsPanelAction);

        // Show actions in the toolbar
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true);
        actionToolbar.setTargetComponent(mToolbar);
        mToolbar.add(actionToolbar.getComponent());

        // Show actions when selected issue is right clicked
        PopupHandler.installPopupHandler(mIssuesTable, actionGroup, ActionPlaces.UPDATE_POPUP, ActionManager.getInstance());
    }

    /**
     * Initializes, configures, set listeners for the Components
     */
    private void initializeComponents() {
        mPresenter = ListIssuesPresenter.getInstance();
        mPresenter.setView(this);

        mIssuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mIssuesTable.setRowSelectionAllowed(true);
        mIssuesTable.getSelectionModel().setSelectionInterval(0, 0);
        mIssuesTable.getSelectionModel().addListSelectionListener(e -> {
            final Task selectedIssue = mIssuesTable.getSelectedObject();
            if (selectedIssue != null) {
                mPresenter.showDetails(selectedIssue);
            }
        });
        mIssueDetailsTextPane.addHyperlinkListener(new BrowserHyperlinkListener());

    }

    /**
     * Converts from markdown format to html format, or simply returns empty string if null
     *
     * @param markdown the text in markdown, non markdown format or simply null
     * @return an html string or an empty string based on the input
     */
    private String fromMarkDownToHtml(@Nullable String markdown) {
        if (markdown != null) {
            final Node document = MARKDOWN_PARSER.parse(markdown);
            return MARKDOWN_RENDERER.render(document);
        } else {
            return "";
        }
    }
}
