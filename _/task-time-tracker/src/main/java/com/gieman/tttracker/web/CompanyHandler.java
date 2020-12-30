package com.gieman.tttracker.web;

import com.gieman.tttracker.domain.*;
import com.gieman.tttracker.service.CompanyService;
import com.gieman.tttracker.service.ProjectService;

import com.gieman.tttracker.vo.Result;
import static com.gieman.tttracker.web.SecurityHelper.getSessionUser;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/company")
public class CompanyHandler extends AbstractHandler {

    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected ProjectService projectService;
    
    @RequestMapping(value = "/find", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public String find(
            @RequestParam(value = "idCompany", required = true) Integer idCompany,
            HttpServletRequest request) {

        User sessionUser = getSessionUser(request);
        
        Result<Company> ar = companyService.find(idCompany, sessionUser.getUsername());

        if (ar.isSuccess()) {

            return getJsonSuccessData(ar.getData());

        } else {

            return getJsonErrorMsg(ar.getMsg());

        }
    }

    @RequestMapping(value = "/store", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public String store(
            @RequestParam(value = "data", required = true) String jsonData,
            HttpServletRequest request) {

        User sessionUser = getSessionUser(request);

        JsonObject jsonObj = parseJsonObject(jsonData);
                
        Result<Company> ar = companyService.store(
                getIntegerValue(jsonObj.get("idCompany")), 
                jsonObj.getString("companyName"), 
                sessionUser.getUsername());

        if (ar.isSuccess()) {

            return getJsonSuccessData(ar.getData());

        } else {

            return getJsonErrorMsg(ar.getMsg());

        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public String findAll(HttpServletRequest request) {

        User sessionUser = getSessionUser(request);

        Result<List<Company>> ar = companyService.findAll(sessionUser.getUsername());

        if (ar.isSuccess()) {

            return getJsonSuccessData(ar.getData());

        } else {

            return getJsonErrorMsg(ar.getMsg());

        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public String remove(
            @RequestParam(value = "data", required = true) String jsonData,
            HttpServletRequest request) {

        User sessionUser = getSessionUser(request);

        JsonObject jsonObj = parseJsonObject(jsonData);

        Result<Company> ar = companyService.remove(
                getIntegerValue(jsonObj.get("idCompany")), 
                sessionUser.getUsername());

        if (ar.isSuccess()) {

            return getJsonSuccessMsg(ar.getMsg());

        } else {

            return getJsonErrorMsg(ar.getMsg());

        }
    }
    
    @RequestMapping(value = "/tree", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public String getCompanyTreeJson(HttpServletRequest request) {

        User sessionUser = getSessionUser(request);

        Result<List<Company>> ar = companyService.findAll(sessionUser.getUsername());

        if (ar.isSuccess()) {

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("success", true);
            JsonArrayBuilder companyChildrenArrayBuilder = Json.createArrayBuilder();
            
            for(Company company : ar.getData()){

                List<Project> projects = company.getProjects();
                    
                JsonArrayBuilder projectChildrenArrayBuilder = Json.createArrayBuilder();
                
                for(Project project : projects){

                    List<Task> tasks = project.getTasks();
                                        
                    JsonArrayBuilder taskChildrenArrayBuilder = Json.createArrayBuilder();
                    
                    for(Task task : tasks){

                        taskChildrenArrayBuilder.add(
                           Json.createObjectBuilder()
                               .add("id", getTreeNodeId(task))
                               .add("text", task.getTaskName())
                               .add("leaf", true)
                       );                        
                    }

                    projectChildrenArrayBuilder.add(
                        Json.createObjectBuilder()
                            .add("id", getTreeNodeId(project))
                            .add("text", project.getProjectName())
                            .add("leaf", tasks.isEmpty())
                            .add("expanded", tasks.size() > 0)
                            .add("children", taskChildrenArrayBuilder)
                    );                    
                    
                }

                companyChildrenArrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", getTreeNodeId(company))
                        .add("text", company.getCompanyName())
                        .add("leaf", projects.isEmpty())
                        .add("expanded", projects.size() > 0)
                        .add("children", projectChildrenArrayBuilder)
                );
            }

            builder.add("children", companyChildrenArrayBuilder);

            return toJsonString(builder.build());

        } else {

            return getJsonErrorMsg(ar.getMsg());

        }
    }

    private String getTreeNodeId(EntityItem obj){
        String id = null;

        if(obj instanceof Company){

            id = "C_" + obj.getId();

        } else if(obj instanceof Project){

            id = "P_" + obj.getId();

        } else if(obj instanceof Task){

            id = "T_" + obj.getId();

        }
        return id;
    }

    @RequestMapping(value = "/treenode", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public String getCompanyTreeNode(
            @RequestParam(value = "node", required = true) String node,
            HttpServletRequest request) {

        User sessionUser = getSessionUser(request);

        logger.info(node);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("success", true);
        JsonArrayBuilder childrenArrayBuilder = Json.createArrayBuilder();
        
        if(node.equals("root")){

            Result<List<Company>> ar = companyService.findAll(sessionUser.getUsername());
            if (ar.isSuccess()) {                                
                
                for(Company company : ar.getData()){                   
                    childrenArrayBuilder.add(
                        Json.createObjectBuilder()
                            .add("id", getTreeNodeId(company))
                            .add("text", company.getCompanyName())
                            .add("leaf", company.getProjects().isEmpty())
                    );
                }
            } else {

                return getJsonErrorMsg(ar.getMsg());
            }
        } else if (node.startsWith("C")){

            String[] idSplit = node.split("_");
            int idCompany = Integer.parseInt(idSplit[1]);
            Result<Company> ar = companyService.find(idCompany, sessionUser.getUsername());

            for(Project project : ar.getData().getProjects()){

                childrenArrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", getTreeNodeId(project))
                        .add("text", project.getProjectName())
                        .add("leaf", project.getTasks().isEmpty())
                );
            }
                
         } else if (node.startsWith("P")){

            String[] idSplit = node.split("_");
            int idProject = Integer.parseInt(idSplit[1]);
            Result<Project> ar = projectService.find(idProject, sessionUser.getUsername());

            for(Task task : ar.getData().getTasks()){

                 childrenArrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", getTreeNodeId(task))
                        .add("text", task.getTaskName())
                        .add("leaf", true)
                );
            }
        }
        
        builder.add("children", childrenArrayBuilder);

        return toJsonString(builder.build());
    }
}
