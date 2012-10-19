/**
 * Copyright 2012 the original author or authors Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package it.av.youeat.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.es.EasySendException;
import it.av.es.model.Order;
import it.av.es.model.Product;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.service.UserService;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
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
public class ProjectServiceTest extends EasySendTest {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    
    
    @Before
    @Transactional
    public void setUp() {
        super.setUp();
    }

    @After
    @Transactional
    public void tearDown() {
        super.tearDown();
    }
    

    @Test
    public void testProjectBasic() throws EasySendException {
        Project a = new Project();
        a.setName("ProfileTest");

        projectService.save(a);

        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "ProfileTest", a.getName());

        Collection<Project> all = projectService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        projectService.remove(a);
    }
    
    @Test
    public void testProjectWithUser() throws EasySendException {
        Project p = new Project();
        p.setName("ProfileTest");

        projectService.save(p);

        assertNotNull("A is null", p);
        assertEquals("Invalid value for test", "ProfileTest", p.getName());

        Collection<Project> all = projectService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        // Basic Test
        User a = new User();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setLanguage(getLanguage());

        userService.add(a);
        
        p.addUser(a);
        a.addProject(p);
        projectService.save(p);
        userService.update(a);
        
        assertEquals("user not added to project", 1, p.getUsers().size());
        
        a = userService.getByID(a.getId());
        assertEquals("user not added to project", 1, a.getProjects().size());
        //assertEquals("Invalid value for test", "ProfileTest", a.getProjects().getName());
       
        p.getUsers().remove(a);
        userService.update(a);

        userService.remove(a);
        projectService.remove(p);

    }
    
    
    @Test
    public void testProjectWithUser2() throws EasySendException {
        Project p = new Project();
        p.setName("ProfileTest");

        projectService.save(p);

        assertNotNull("A is null", p);
        assertEquals("Invalid value for test", "ProfileTest", p.getName());

        Collection<Project> all = projectService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        // Basic Test
        User a = new User();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setLanguage(getLanguage());

        userService.add(a);
        
        
        userService.assignUserToProject(a, p);
        
        assertEquals("user not added to project", 1, p.getUsers().size());
        
        a = userService.getByID(a.getId());
        assertEquals("user not added to project", 1, a.getProjects().size());
        //assertEquals("Invalid value for test", "ProfileTest", a.getProjects().getName());
       
        userService.removeUserFromProject(a, p);
        
        assertEquals("user not added to project", 0, p.getUsers().size());
        
        a = userService.getByID(a.getId());
        assertEquals("user not added to project", 0, a.getProjects().size());

        userService.remove(a);
        projectService.remove(p);

    }
    
    
    @Test
    public void testProjectwithProduct() throws EasySendException {
        Project a = new Project();
        a.setName("ProfileTest");

        projectService.save(a);

        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "ProfileTest", a.getName());

        Collection<Project> all = projectService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);
        
        Product product = new Product();
        product.setName("nome prodotto");
        productService.save(product);
        a.addProduct(product);
        a = projectService.save(a);
        assertEquals( product.getName(), a.getProducts().iterator().next().getName());
        projectService.remove(a);

    }
    
    @Test
    public void testProjectwithOrders() throws EasySendException {
        // Basic Test
        User u = new User();
        u.setFirstname("Alessandro");
        u.setLastname("Vincelli");
        u.setPassword("secret");
        u.setEmail("userServiceTest@test");
        u.setUserProfile(getProfile());
        u.setLanguage(getLanguage());

        userService.add(u);
        
        Project a = new Project();
        a.setName("ProfileTest");

        projectService.save(a);

        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "ProfileTest", a.getName());

        Collection<Project> all = projectService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);
        
        Product product = new Product();
        product.setName("nome prodotto");
        productService.save(product);
        a.addProduct(product);
        a = projectService.save(a);
        assertEquals(product.getName(), a.getProducts().iterator().next().getName());
        
        Order order = new Order();
//        order.setName("nome prodotto");
//        order.setProduct(product);
        order.setProject(a);
        
        order = orderService.placeNewOrder(order, a, u);
        
        assertEquals(product.getName(), a.getProducts().iterator().next().getName());
//        assertEquals(order.getName(), a.getOrders().iterator().next().getName());
//        assertEquals(order.getName(), u.getOrders().iterator().next().getName());
        assertNotNull(order.getCreationTime());
        
        a.getOrders().remove(order);
        u.getOrders().remove(order);
        projectService.save(a);
        userService.update(u);
        orderService.remove(order);
        projectService.remove(a);
        userService.remove(u);

    }

}