package com.gieman.tttracker.service;

import java.util.List;
import com.gieman.tttracker.domain.TaskLog;
import com.gieman.tttracker.vo.Result;
import java.util.Date;

public interface TaskLogService {

    public Result<TaskLog> store(
        Integer idTaskLog,
        Integer idTask,
        String username,
        String taskDescription,
        Date taskLogDate,
        int taskMinutes,
        String actionUsername);

    public Result<TaskLog> remove(Integer idTaskLog, String actionUsername);
    public Result<TaskLog> find(Integer idTaskLog, String actionUsername);
    public Result<List<TaskLog>> findByUser(String username, Date startDate, Date endDate, String actionUsername);

}