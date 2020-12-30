package com.gieman.tttracker.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.json.JsonObjectBuilder;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ttt_task_log")
@NamedQueries({
    @NamedQuery(name = "TaskLog.findByUser", query = "SELECT tl FROM TaskLog tl WHERE tl.user = :user AND tl.taskLogDate BETWEEN :startDate AND :endDate order by tl.taskLogDate ASC"),
    @NamedQuery(name = "TaskLog.findTaskLogCountByTask", query = "SELECT count(tl) FROM TaskLog tl WHERE tl.task = :task "),
    @NamedQuery(name = "TaskLog.findTaskLogCountByUser", query = "SELECT count(tl) FROM TaskLog tl WHERE tl.user = :user ")
})
public class TaskLog extends AbstractEntity implements EntityItem<Integer> {

    static final SimpleDateFormat DATE_FORMAT_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_task_log")
    private Integer idTaskLog;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "task_description")
    private String taskDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "task_log_date")
    @Temporal(TemporalType.DATE)
    private Date taskLogDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "task_minutes")
    private int taskMinutes;
    @JoinColumn(name = "username", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "id_task", referencedColumnName = "id_task")
    @ManyToOne(optional = false)
    private Task task;

    public TaskLog() {
    }

    public TaskLog(Integer idTaskLog) {
        this.idTaskLog = idTaskLog;
    }

    public TaskLog(Integer idTaskLog, String taskDescription, Date taskLogDate, int taskMinutes) {
        this.idTaskLog = idTaskLog;
        this.taskDescription = taskDescription;
        this.taskLogDate = taskLogDate;
        this.taskMinutes = taskMinutes;
    }

    public Integer getIdTaskLog() {
        return idTaskLog;
    }

    public void setIdTaskLog(Integer idTaskLog) {
        this.idTaskLog = idTaskLog;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Date getTaskLogDate() {
        return taskLogDate;
    }

    public void setTaskLogDate(Date taskLogDate) {
        this.taskLogDate = taskLogDate;
    }

    public int getTaskMinutes() {
        return taskMinutes;
    }

    public void setTaskMinutes(int taskMinutes) {
        this.taskMinutes = taskMinutes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTaskLog != null ? idTaskLog.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaskLog other = (TaskLog) obj;
        return Objects.equals(this.idTaskLog, other.idTaskLog);
    }

    @Override
    public String toString() {
        return "com.gieman.tttracker.domain.TaskLog[ idTaskLog=" + idTaskLog + " ]";
    }

    @Override
    public Integer getId() {
        return idTaskLog;
    }

    @Override
    public void addJson(JsonObjectBuilder builder) {

        builder.add("idTaskLog", idTaskLog)
                .add("taskDescription", taskDescription)
                .add("taskLogDate", taskLogDate == null ? "" : DATE_FORMAT_yyyyMMdd.format(taskLogDate))
                .add("taskMinutes", taskMinutes);

        if (user != null) {
            user.addJson(builder);
        }
        if (task != null) {
            task.addJson(builder);            
        }
    }

}
