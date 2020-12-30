package com.gieman.tttracker.service;

import com.gieman.tttracker.dao.TaskDao;
import com.gieman.tttracker.dao.TaskLogDao;
import java.util.List;
import com.gieman.tttracker.domain.*;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.gieman.tttracker.vo.Result;
import com.gieman.tttracker.vo.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
@Service("taskLogService")
public class TaskLogServiceImpl extends AbstractService implements TaskLogService {

    @Autowired
    protected TaskLogDao taskLogDao;    
    @Autowired
    protected TaskDao taskDao;   
    
    public TaskLogServiceImpl() {
        super();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<TaskLog> find(Integer idTaskLog, String actionUsername) {

        User actionUser = userDao.find(actionUsername);

        if(actionUser == null) {
            return ResultFactory.getFailResult(USER_INVALID);
        }

        TaskLog taskLog = taskLogDao.find(idTaskLog);

        if(taskLog == null){
            return ResultFactory.getFailResult("Task log not found with idTaskLog=" + idTaskLog);
        } else if( actionUser.isAdmin() || taskLog.getUser().equals(actionUser)){
            return ResultFactory.getSuccessResult(taskLog);
        } else {
            return ResultFactory.getFailResult("User does not have permission to view this task log");
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<TaskLog> store(
        Integer idTaskLog,
        Integer idTask,
        String username,
        String taskDescription,
        Date taskLogDate,
        int taskMinutes,
        String actionUsername) {

        User actionUser = userDao.find(actionUsername);
        User taskUser = userDao.find(username);

        if(actionUser == null || taskUser == null) {
            return ResultFactory.getFailResult(USER_INVALID);
        }

        Task task = taskDao.find(idTask);

        if(task == null) {
            return ResultFactory.getFailResult("Unable to store task log with null task");
        }

        if( !actionUser.isAdmin() && ! taskUser.equals(actionUser) ){
            return ResultFactory.getFailResult("User performing save must be an admin user or saving their own record");
        }

        TaskLog taskLog;

        if (idTaskLog == null) {
            taskLog = new TaskLog();
        } else {
            taskLog = taskLogDao.find(idTaskLog);
            if(taskLog == null) {
                return ResultFactory.getFailResult("Unable to find taskLog instance with ID=" + idTaskLog);
            }
        }

        taskLog.setTaskDescription(taskDescription);
        taskLog.setTaskLogDate(taskLogDate);
        taskLog.setTaskMinutes(taskMinutes);
        taskLog.setTask(task);
        taskLog.setUser(taskUser);

        if(taskLog.getId() == null) {
            taskLogDao.persist(taskLog);
        } else {
            taskLog = taskLogDao.merge(taskLog);
        }

        return ResultFactory.getSuccessResult(taskLog);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<TaskLog> remove(Integer idTaskLog, String actionUsername){

        User actionUser = userDao.find(actionUsername);

        if(actionUser == null) {
            return ResultFactory.getFailResult(USER_INVALID);
        }

        if(idTaskLog == null){
            return ResultFactory.getFailResult("Unable to remove TaskLog [null idTaskLog]");
        } 

        TaskLog taskLog = taskLogDao.find(idTaskLog);

        if(taskLog == null) {
            return ResultFactory.getFailResult("Unable to load TaskLog for removal with idTaskLog=" + idTaskLog);
        } 

        // only the user that owns the task log may remove it
        // OR an admin user
        if(actionUser.isAdmin() || taskLog.getUser().equals(actionUser)){
            taskLogDao.remove(taskLog);
            return ResultFactory.getSuccessResultMsg("taskLog removed successfully");
        } else {
            return ResultFactory.getFailResult("Only an admin user or task log owner can delete a task log");
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<List<TaskLog>> findByUser(String username, Date startDate, Date endDate, String actionUsername){

        User taskUser = userDao.findByUsername(username);
        User actionUser = userDao.find(actionUsername);

        if(taskUser == null || actionUser == null) {
            return ResultFactory.getFailResult(USER_INVALID);
        }

        if(startDate == null || endDate == null){
            return ResultFactory.getFailResult("Start and end date are required for findByUser ");
        }
        
        if(actionUser.isAdmin() || taskUser.equals(actionUser)){
            return ResultFactory.getSuccessResult(taskLogDao.findByUser(taskUser, startDate, endDate));
        } else {
            return ResultFactory.getFailResult("Unable to find task logs. User does not have permission with username=" + username);
        }
    }
}
