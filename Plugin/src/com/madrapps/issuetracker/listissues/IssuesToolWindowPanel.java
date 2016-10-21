package com.madrapps.issuetracker.listissues;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepository;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.madrapps.issuetracker.actions.RefreshIssueListAction;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

/**
 * This is repsonsible for the ToolWindow GUI. The GUI is backed up be a .form file.
 * <p>
 * Created by Henry on 10/19/2016.
 */
public class IssuesToolWindowPanel extends SimpleToolWindowPanel implements IListIssuesContract.IView {

    private final static ColumnInfo<Task, String> PRESENTABLE_NAME = new ColumnInfo<Task, String>("Name") {
        public String valueOf(Task object) {
            return object.getPresentableName();
        }

        public Comparator<Task> getComparator() {
            return (o, o1) -> o.getPresentableName().compareTo(o1.getPresentableName());
        }
    };
    private final static ColumnInfo<Task, String> REPOSITORY_TYPE = new ColumnInfo<Task, String>("Repository") {
        public String valueOf(Task object) {
            final TaskRepository repository = object.getRepository();
            if (repository != null) {
                return repository.getRepositoryType().getName();
            }
            return "";
        }

        public Comparator<Task> getComparator() {
            return (o, o1) -> {
                if (o.getRepository() != null && o1.getRepository() != null) {
                    return o.getRepository().getRepositoryType().getName().compareTo(o1.getRepository().getRepositoryType().getName());
                } else {
                    return 0;
                }
            };
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
            // So that the appropriate Renderer is used to render and Icon
            return Icon.class;
        }

        @Override
        public int getWidth(JTable table) {
            // So as to prevent users from resizing this column
            return 50;
        }
    };
    private final static ColumnInfo<Task, String> CREATED_ON = new ColumnInfo<Task, String>("Created On") {
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
    /** The columns of the issues table */
    private static final ColumnInfo[] COLUMN_NAMES = {ICON, PRESENTABLE_NAME, CREATED_ON, LAST_UPDATED, REPOSITORY_TYPE};
    /** To parse the Markdown text */
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    /** To render the markdown as html */
    private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder().build();
    /** The root component that holds every component */
    private JPanel mContentPanel;
    /** The table that shows the list of tasks/issues */
    private TableView<Task> mIssuesTable;
    /** The summary panel that shows the details of an issue when it's selected from the table */
    private JTextPane mIssueSummaryTextPane;
    private JPanel mToolbar;
    private JFormattedTextField mEmptyMessageTextField;
    private JPanel mIssuesListPanel;
    private JPanel mSummaryPanel;
    /** The list that's backing up the {@code mIssuesTable} */
    private List<Task> mIssueList;
    private ListIssuesPresenter mPresenter;


    IssuesToolWindowPanel(ToolWindow toolWindow) {
        super(true, false);
        final Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().addContent(content);
        this.setContent(mContentPanel);
    }

    @Override
    public void updateIssueList(@NotNull List<Task> issuesList, boolean forceUpdate) {
        if (forceUpdate) {
            mIssueList.clear();
        }
        issuesList.forEach(task -> {
            final int index = mIssueList.indexOf(task);
            if (index != -1) {
                mIssueList.set(index, task);
            } else {
                mIssueList.add(task);
            }
        });
        final ListTableModel<Task> model = new ListTableModel<>(COLUMN_NAMES, mIssueList, 0);
        mIssuesTable.setModelAndUpdateColumns(model);
        final CardLayout layout = (CardLayout) mIssuesListPanel.getLayout();
        layout.show(mIssuesListPanel, "CardTABLE");
    }

    @Override
    public void init(@NotNull Project project) {
        initializeComponents();
        initializeActions();

        mPresenter.loadInitialIssues(project);
    }

    @Override
    public void showSummary(@Nullable String description, @Nullable String issueUrl, @Nullable Comment[] comments) {
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
        mIssueSummaryTextPane.setText(stringBuilder.toString());
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
        mIssueSummaryTextPane.setText("");
    }

    /**
     * Get a neat readable format of the date if it's not null, or an empty string
     *
     * @param date the date
     * @return the date in a readable string format
     */
    @NotNull
    private static String getValueOfDate(@Nullable Date date) {
        String formattedDate = "";
        if (date != null) {
            formattedDate = DateFormatUtil.formatPrettyDateTime(date);
        }
        return formattedDate;
    }

    private void initializeActions() {
        final AnAction refreshAction = ActionManager.getInstance().getAction(RefreshIssueListAction.ACTION_ID);

        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(refreshAction);

        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, false);
        actionToolbar.setTargetComponent(mToolbar);
        mToolbar.add(actionToolbar.getComponent());
    }

    /**
     * Initializes, configures, set listeners for the Components
     */
    private void initializeComponents() {
        mPresenter = ListIssuesPresenter.getInstance();
        mPresenter.setView(this);

        mIssueList = new ArrayList<>();

        mIssuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mIssuesTable.setRowSelectionAllowed(true);
        mIssuesTable.getSelectionModel().setSelectionInterval(0, 0);
        mIssuesTable.getSelectionModel().addListSelectionListener(e -> {
            final Task selectedIssue = mIssuesTable.getSelectedObject();
            if (selectedIssue != null) {
                mPresenter.showSummary(selectedIssue);
            }
        });
        mIssueSummaryTextPane.addHyperlinkListener(new BrowserHyperlinkListener());

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
