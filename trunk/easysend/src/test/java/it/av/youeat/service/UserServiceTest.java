/**
 * Copyright 2012 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.es.EasySendException;
import it.av.es.model.User;
import it.av.es.service.UserService;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserServiceTest extends EasySendTest {

    @Autowired
    private UserService userService;
    
    @Before
    @Transactional
    public void setUp() {
        super.setUp();
    }

    @After
    @Transactional
    public void tearDown() {
    }


    @Test
    public void testUserService_generic() throws EasySendException {

        // Basic Test
        User a = new User();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setLanguage(getLanguage());

        userService.add(a);

        // a = eaterService.getByPath(a.getPath());
        assertNotNull("A is null", a);
        assertNotNull("Profile is null", a.getUserProfile());
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());

        Collection<User> all = userService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        a.setLastname("Modified");
        userService.update(a);
        assertEquals("Invalid value for test", "Modified", a.getLastname());
        a = userService.getByEmail("userServiceTest@test");
        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());


        /*List<User> found = eaterService.freeTextSearch("Ale*");
        assertNotNull(found);
        assertTrue(found.size() > 0);
        
        found = eaterService.freeTextSearch("vinc*");
        assertNotNull(found);
        assertTrue(found.size() > 0);
        */

        userService.remove(a);

    }
    
    @Test
    @Ignore("page needed")
    public void testUserService_remove() throws EasySendException {

        User a = new User();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setLanguage(getLanguage());

        userService.add(a);
        
        User b = new User();
        b.setFirstname("Alessandro");
        b.setLastname("Vincelli");
        b.setPassword("secret");
        b.setEmail("userServiceTest@test.com");
        b.setUserProfile(getProfile());
        b.setLanguage(getLanguage());
        b = userService.add(b);
        assertNotNull("B is null", b);

        userService.add(b);
        
        
        userService.remove(a);
        userService.remove(b);
        
    }
}
