package com.gieman.tttracker.service;

import com.gieman.tttracker.dao.CompanyDao;
import com.gieman.tttracker.dao.ProjectDao;
import java.util.List;
import com.gieman.tttracker.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.gieman.tttracker.vo.Result;
import com.gieman.tttracker.vo.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
@Service("projectService")
public class ProjectServiceImpl extends AbstractService implements ProjectService {

    @Autowired
    protected CompanyDao companyDao;    
    @Autowired
    protected ProjectDao projectDao;

    public ProjectServiceImpl() {
        super();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<Project> find(Integer idProject, String actionUsername) {

        if(isValidUser(actionUsername)) {

            Project project = projectDao.find(idProject);
            return ResultFactory.getSuccessResult(project);

        } else {
            return ResultFactory.getFailResult(USER_INVALID);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<Project> store(
        Integer idProject,
        Integer idCompany,
        String projectName,
        String actionUsername) {

        User actionUser = userDao.find(actionUsername);
        
        if (!actionUser.isAdmin()) {

            return ResultFactory.getFailResult(USER_NOT_ADMIN);

        }

        Project project;
        Company company = companyDao.find(idCompany);

        if (idProject == null) {

            // we are adding a NEW project
            project = new Project();

            if(company == null){

                return ResultFactory.getFailResult("Unable to add new project without a valid company [idCompany=" + idCompany + "]");

            } else {
                project.setCompany(company);
                company.getProjects().add(project);
            }

        } else {

            // the project must be valid if idProject is supplied
            project = projectDao.find(idProject);

            if(project == null) {

                return ResultFactory.getFailResult("Unable to find project instance with ID=" + idProject);

            } else {

                if(company != null){
                    // we may be changing the company assigned to the project
                    if( ! project.getCompany().equals(company)){

                        Company currentCompany = project.getCompany();
                        // reassign to new company
                        project.setCompany(company);
                        company.getProjects().add(project);
                        // remove from previous company
                        currentCompany.getProjects().remove(project);
                    }
                }
            }
        }

        project.setProjectName(projectName);

        if(project.getIdProject() == null) {

            projectDao.persist(project);

        } else {

            project = projectDao.merge(project);

        }

        return ResultFactory.getSuccessResult(project);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Result<Project> remove(Integer idProject, String actionUsername){

        User actionUser = userDao.find(actionUsername);
        
        if (!actionUser.isAdmin()) {

            return ResultFactory.getFailResult(USER_NOT_ADMIN);

        }

        if(idProject == null){

            return ResultFactory.getFailResult("Unable to remove Project with a null idProject");

        } 

        Project project = projectDao.find(idProject);

        if(project == null) {

            return ResultFactory.getFailResult("Unable to load Project for removal with idProject=" + idProject);

        } else if (project.getTasks() != null && ! project.getTasks().isEmpty() ) {

            // tasks are assigned: not allowed to delete
            return ResultFactory.getFailResult("Unable to remove Project with idProject=" + idProject + " as valid tasks are assigned");

        } else {

            // find the company that owns the project
            Company company = project.getCompany();

            // removed the project
            projectDao.remove(project);

            // ensure the project is removed from the company
            company.getProjects().remove(project);

            String msg = "Project " + project.getProjectName() + " was deleted by " + actionUsername;
            logger.info(msg);
            return ResultFactory.getSuccessResultMsg(msg);

        }

    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Result<List<Project>> findAll(String actionUsername){

        if(isValidUser(actionUsername)){

            return ResultFactory.getSuccessResult(projectDao.findAll());

        } else {

            return ResultFactory.getFailResult(USER_INVALID);

        }

    }
}
