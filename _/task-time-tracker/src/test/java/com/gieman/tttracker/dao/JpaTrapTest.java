package com.gieman.tttracker.dao;

import com.gieman.tttracker.domain.Company;
import com.gieman.tttracker.domain.Project;
import com.gieman.tttracker.domain.User;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JpaTrapTest extends AbstractDaoForTesting {

    @Test
    public void testManyToOne() throws Exception {

        logger.debug("\nSTARTED testManyToOne()\n");

        Company c = companyDao.findAll().get(0);
        Company c2 = companyDao.findAll().get(1);

        Project p = c.getProjects().get(0);

        p.setCompany(c2);
        p = projectDao.merge(p);

        c.getProjects().remove(p);
        c2.getProjects().add(p);

        assertTrue("Original company still has project in its collection!",
                !c.getProjects().contains(p));
        assertTrue("Newly assigned company does not have project in its collection",
                c2.getProjects().contains(p));

        logger.debug("\nFINISHED testManyToOne()\n");

    }

    @Test
    public void testFindByUsernamePassword() throws Exception {

        logger.debug("\nSTARTED testFindByUsernamePassword()\n");

        // find by username/password combination
        User user = userDao.findByUsernamePassword("bjones", "admin");

        assertTrue("Unable to find valid user with correct username/password combination", 
                user != null);

        user = userDao.findByUsernamePassword("bjones", "ADMIN");

        assertTrue("User found with invalid password", 
                user == null); 
        
        logger.debug("\nFINISHED testFindByUsernamePassword()\n");
    }

}
