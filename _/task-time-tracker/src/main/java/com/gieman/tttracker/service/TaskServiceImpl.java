package com.gieman.tttracker.service;

import com.gieman.tttracker.dao.ProjectDao;
import com.gieman.tttracker.dao.TaskDao;
import com.gieman.tttracker.dao.TaskLogDao;
import java.util.List;
import com.gieman.tttracker.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.gieman.tttracker.vo.Result;
import com.gieman.tttracker.vo.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
@Service("taskService")
public class TaskServiceImpl extends AbstractService implements TaskService {

    @Autowired
    protected TaskDao taskDao;
    @Autowired
    protected TaskLogDao taskLogDao;     
    @Autowired
    protected ProjectDao projectDao;    
    
    public TaskServiceImpl() {
        super();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<Task> find(Integer idTask, String actionUsername) {

        if(isValidUser(actionUsername)) {
            return ResultFactory.getSuccessResult(taskDao.find(idTask));
        } else {
            return ResultFactory.getFailResult(USER_INVALID);
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<Task> store(
        Integer idTask,
        Integer idProject,
        String taskName,
        String actionUsername) {

        User actionUser = userDao.find(actionUsername);
        
        if (!actionUser.isAdmin()) {

            return ResultFactory.getFailResult(USER_NOT_ADMIN);
        }

        Project project = projectDao.find(idProject);
        
        if(project == null){
            return ResultFactory.getFailResult("Unable to store task without a valid project [idProject=" + idProject + "]");
        }

        Task task;

        if (idTask == null) {

            task = new Task();
            task.setProject(project);
            project.getTasks().add(task);

        } else {

            task = taskDao.find(idTask);

            if(task == null) {
                
                return ResultFactory.getFailResult("Unable to find task instance with idTask=" + idTask);
                
            } else {

                if(! task.getProject().equals(project)){

                    Project currentProject = task.getProject();
                    // reassign to new project
                    task.setProject(project);
                    project.getTasks().add(task);
                    // remove from previous project
                    currentProject.getTasks().remove(task);
                }
            }
        }

        task.setTaskName(taskName);

        if(task.getId() == null) {
            taskDao.persist(task);
        } else {
            task = taskDao.merge(task);
        }

        return ResultFactory.getSuccessResult(task);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<Task> remove(Integer idTask, String actionUsername){

        User actionUser = userDao.find(actionUsername);
        
        if (!actionUser.isAdmin()) {

            return ResultFactory.getFailResult(USER_NOT_ADMIN);
        }

        if(idTask == null){

            return ResultFactory.getFailResult("Unable to remove Task [null idTask]");

        } else {

            Task task = taskDao.find(idTask);
            long taskLogCount = taskLogDao.findTaskLogCountByTask(task);

            if(task == null) {

                return ResultFactory.getFailResult("Unable to load Task for removal with idTask=" + idTask);

            } else if(taskLogCount > 0) {

                return ResultFactory.getFailResult("Unable to remove Task with idTask=" + idTask + " as valid task logs are assigned");

            } else {

                Project project = task.getProject();

                taskDao.remove(task);

                project.getTasks().remove(task);

                String msg = "Task " + task.getTaskName() + " was deleted by " + actionUsername;
                logger.info(msg);
                return ResultFactory.getSuccessResultMsg(msg);
            }
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<List<Task>> findAll(String actionUsername){

        if(isValidUser(actionUsername)){
            return ResultFactory.getSuccessResult(taskDao.findAll());
        } else {
            return ResultFactory.getFailResult(USER_INVALID);
        }
    }
}
