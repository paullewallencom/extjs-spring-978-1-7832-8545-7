package com.gieman.tttracker.dao;

import com.gieman.tttracker.domain.Task;
import java.util.List;

public interface TaskDao extends GenericDao<Task, Integer>{
   
    public List<Task> findAll();    
}
