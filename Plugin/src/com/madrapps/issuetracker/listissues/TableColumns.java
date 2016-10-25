package com.madrapps.issuetracker.listissues;

import com.intellij.openapi.util.Comparing;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.ui.ColumnInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JTable;

/**
 * This defines all the ColumnsInfos used in the Issue List Table
 * <p>
 * Created by Henry on 10/23/2016.
 */
final class TableColumns {
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
    static final ColumnInfo[] COLUMN_NAMES = {ICON, PRESENTABLE_NAME, CREATED_ON, LAST_UPDATED, REPOSITORY_TYPE};

    /**
     * Get a neat readable format of the date if it's not null, or an empty string
     *
     * @param date the date
     * @return the date in a readable string format
     */
    @NotNull
    static String getValueOfDate(@Nullable Date date) {
        String formattedDate = "";
        if (date != null) {
            formattedDate = DateFormatUtil.formatPrettyDateTime(date);
        }
        return formattedDate;
    }
}
