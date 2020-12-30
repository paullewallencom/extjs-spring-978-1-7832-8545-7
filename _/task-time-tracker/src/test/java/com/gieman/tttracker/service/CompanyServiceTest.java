package com.gieman.tttracker.service;

import com.gieman.tttracker.dao.ProjectDao;
import com.gieman.tttracker.domain.Company;
import com.gieman.tttracker.domain.Project;
import com.gieman.tttracker.vo.Result;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CompanyServiceTest extends AbstractServiceForTesting {

    protected final String TEST_USERNAME = "bjones";
    @Autowired
    protected CompanyService companyService;    
    @Autowired
    protected ProjectDao projectDao; 

    @Test
    public void testFind() throws Exception {
        
        logger.debug("\nSTARTED testFind()\n");
        Result<List<Company>> allItems = companyService.findAll(TEST_USERNAME);
        
        assertTrue(allItems.getData().size() > 0);

        // get the first item in the list
        Company c1 = allItems.getData().get(0);

        int id = c1.getId();

        Result<Company> c2= companyService.find(id, TEST_USERNAME);

        assertTrue(c1.equals(c2.getData()));
        logger.debug("\nFINISHED testFind()\n");
    }    
    
    /**
     * Test case for the findAll() method of the CompanyService implementation
     * @throws Exception 
     */
    @Test
    public void testFindAll() throws Exception {
        
        logger.debug("\nSTARTED testFindAll()\n");
        int rowCount = countRowsInTable("ttt_company");
        
        if(rowCount > 0){                       
            
            Result<List<Company>> allItems = companyService.findAll(TEST_USERNAME);
            assertTrue("Company.findAll list not equal to row count of table ttt_company", rowCount == allItems.getData().size());
            
        } else {
            throw new IllegalStateException("INVALID TESTING SCENARIO: Company table is empty");
        }
        logger.debug("\nFINISHED testFindAll()\n");
    }    

    /**
     * Test case adding a new company of the CompanyService implementation
     * @throws Exception 
     */    
    @Test
    public void testAddNew() throws Exception {
        
        logger.debug("\nSTARTED testAddNew()\n");
        //Company c = new Company();
        final String NEW_NAME = "New Test Company name";
        //c.setCompanyName(NEW_NAME);
        
        Result<Company> c2 = companyService.store(null, NEW_NAME, TEST_USERNAME);
        
        assertTrue(c2.getData().getId() != null);
        assertTrue(c2.getData().getCompanyName().equals(NEW_NAME));
        
        logger.debug("\nFINISHED testAddNew()\n");
    }
    
    /**
     * Test case for updating a company of the CompanyService implementation
     * @throws Exception 
     */    
    @Test
    public void testUpdate() throws Exception {
        
        logger.debug("\nSTARTED testUpdate()\n");
        final String NEW_NAME = "Update Test Company New Name";
        
        Result<List<Company>> ar1 = companyService.findAll(TEST_USERNAME);
        Company c = ar1.getData().get(0);
        //c.setCompanyName(NEW_NAME);
        
        companyService.store(c.getIdCompany(), NEW_NAME, TEST_USERNAME);
        
        Result<Company> ar2 = companyService.find(c.getIdCompany(), TEST_USERNAME);
        
        assertTrue(ar2.getData().getCompanyName().equals(NEW_NAME));
        
        logger.debug("\nFINISHED testMerge()\n");
        
    }    
    
    /**
     * Test case for the remove(Company) method of the CompanyService implementation
     * @throws Exception 
     */    
    @Test
    public void testRemove() throws Exception {
        
        logger.debug("\nSTARTED testRemove()\n");
        Result<List<Company>> ar1 = companyService.findAll(TEST_USERNAME);
        Company c = ar1.getData().get(0);
        
        Result<Company> ar = companyService.remove(c.getIdCompany(), TEST_USERNAME);        
        Result<Company> ar2 = companyService.find(c.getIdCompany(), TEST_USERNAME);
                
        // should fail as projects assigned
        assertTrue(! ar.isSuccess());
        // finder still works
        assertTrue(ar2.getData() != null);

        logger.debug("\ntestRemove() - UNABLE TO DELETE TESTS PASSED\n");
        // remove all the projects
        c = ar2.getData();
        
        for(Project p : c.getProjects()){
            projectDao.remove(p);
            
        }
        c.getProjects().clear();

        logger.debug("\ntestRemove() - removed all projects\n");
        
        ar = companyService.remove(c.getIdCompany(), TEST_USERNAME);
        // remove should have succeeded
        assertTrue(ar.isSuccess());
        
        ar2 = companyService.find(c.getIdCompany(), TEST_USERNAME);
        // should not have been found
        assertTrue(ar2.getData() == null);
        assertTrue(ar2.isSuccess());
        
        logger.debug("\nFINISHED testRemove()\n");
    }     
}