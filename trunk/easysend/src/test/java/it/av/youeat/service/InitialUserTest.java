/**
 * Copyright 2009 the original author or authors
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

import it.av.es.model.User;
import it.av.es.model.UserProfile;
import it.av.es.service.LanguageService;
import it.av.es.service.UserProfileService;
import it.av.es.service.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class InitialUserTest extends EasySendTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private LanguageService languageService;
    
    @Transactional
    @Test
    public void testInitialData(){
        if(userService.getByEmail("a.vincelli@gmail.com") == null ){
            setUp();
            UserProfile adminProfile = userProfileService.getByName(UserProfile.VENDOR);
            User a = new User();
            a.setFirstname("Alessandro");
            a.setLastname("Vincelli");
            a.setPassword("123456");
            a.setEmail("a.vincelli@gmail.com");
            a.setUserProfile(adminProfile);
            a.setLanguage(languageService.getAll().get(0));
            userService.add(a);
        }
    }

}