package com.gieman.tttracker.service;

import com.gieman.tttracker.dao.TaskLogDao;
import com.gieman.tttracker.dao.UserDao;
import com.gieman.tttracker.domain.TaskLog;
import com.gieman.tttracker.domain.User;
import com.gieman.tttracker.vo.Result;
import java.util.Calendar;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends AbstractServiceForTesting {

    @Autowired
    protected UserService userService;
    @Autowired
    protected TaskLogDao taskLogDao;
    @Autowired
    protected UserDao userDao;
    private final String TEST_USERNAME = "jsmith";

    @Test
    public void testAddNew() throws Exception {

        String ADMIN_USERNAME = "bjones";

        logger.debug("\nSTARTED testAddNew()\n");

        Result<User> ar = userService.store("nusername", "David", "Francis", "df@tttracker.com", "admpwd", 'N', ADMIN_USERNAME);

        // should succeed
        logger.debug(ar.getMsg());
        assertTrue(ar.isSuccess());

        ar = userService.store(this.TEST_USERNAME, "David", "Francis", "df@tttracker.com", "admpwd", 'Y', ADMIN_USERNAME);

        logger.debug(ar.getMsg());
        assertTrue("Cannot assign email that is currently assigned to other user", !ar.isSuccess());

        ar = userService.store("user100", "David", "Francis", "user100@tttracker.com", "", 'Y', ADMIN_USERNAME);

        logger.debug(ar.getMsg());
        assertTrue("Cannot set empty password for user", !ar.isSuccess());

        ar = userService.store("user101", "David", "Francis", "  ", "validpwd", 'Y', ADMIN_USERNAME);

        logger.debug(ar.getMsg());
        assertTrue("Cannot set empty email for user", !ar.isSuccess());

        ar = userService.store(this.TEST_USERNAME, "David", "Francis", "diff@email.com", "validpwd", 'Y', ADMIN_USERNAME);

        logger.debug(ar.getMsg());
        assertTrue("Assigning new email to user is allowed", ar.isSuccess());

        logger.debug("\nFINISHED testAddNew()\n");
    }

    /**
     * Test case for the remove(Company) method of the CompanyService
     * implementation
     *
     * @throws Exception
     */
    @Test
    public void testRemove() throws Exception {

        String ADMIN_USERNAME = "bjones";
        Calendar DEFAULT_START_DATE = Calendar.getInstance();
        Calendar DEFAULT_END_DATE = Calendar.getInstance();
        DEFAULT_START_DATE.set(Calendar.YEAR, 1900);
        DEFAULT_END_DATE.set(Calendar.YEAR, 3000);
        
        logger.debug("\nSTARTED testRemove()\n");

        User user1 = userDao.find(TEST_USERNAME);

        List<TaskLog> logs = taskLogDao.findByUser(user1, DEFAULT_START_DATE.getTime(), DEFAULT_END_DATE.getTime());
        Result<User> ar;

        if (logs.isEmpty()) {

            ar = userService.remove(TEST_USERNAME, ADMIN_USERNAME);
            logger.debug(ar.getMsg());
            assertTrue("Delete of user should be allowed as no task logs assigned!", ar.isSuccess());

        } else {

            // this user has task log assigned
            ar = userService.remove(TEST_USERNAME, ADMIN_USERNAME);
            logger.debug(ar.getMsg());
            assertTrue("Cascading delete of user to task logs not allowed!", !ar.isSuccess());

        }

        logs = taskLogDao.findByUser(user1, DEFAULT_START_DATE.getTime(), DEFAULT_END_DATE.getTime());
        if (logs.isEmpty()) {

            ar = userService.remove(TEST_USERNAME, ADMIN_USERNAME);
            logger.debug(ar.getMsg());
            assertTrue("Delete of user should be allowed as empty task log list!", ar.isSuccess());

        } else {

            // this user has task log assigned
            ar = userService.remove(TEST_USERNAME, ADMIN_USERNAME);
            logger.debug(ar.getMsg());
            assertTrue("Cascading delete of user to task logs not allowed!", !ar.isSuccess());

        }

        ar = userService.remove(ADMIN_USERNAME, ADMIN_USERNAME);
        logger.debug(ar.getMsg());
        assertTrue("Should not be able to delete yourself", !ar.isSuccess());

        logger.debug("\nFINISHED testRemove()\n");
    }

    @Test
    public void testLogon() {

        Result<User> ar = userService.findByUsernamePassword("jsmith", "admin");

        assertTrue("Valid user could not be found for valid user/pwd", ar.getData() != null);
        assertTrue(ar.isSuccess());

        ar = userService.findByUsernamePassword("jsmith", "ADMIN");

        assertTrue("Invalid logic - valid user found with UPPERCASE password", ar.getData() == null);
        assertTrue(!ar.isSuccess());

        ar = userService.findByUsernamePassword("JS@tttracker.com", "admin");

        assertTrue("Valid user could not be found for valid email/pwd", ar.getData() != null);
        assertTrue(ar.isSuccess());

        ar = userService.findByUsernamePassword("jsmith", "invalidadmin");
        assertTrue("Invalid user verified with wrong password", ar.getData() == null);
        assertTrue(!ar.isSuccess());

        ar = userService.findByUsernamePassword("blah", "blah");
        assertTrue("Invalid user verified with wrong username and password", ar.getData() == null);
        assertTrue(!ar.isSuccess());
    }
}
