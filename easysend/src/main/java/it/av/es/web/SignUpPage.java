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
package it.av.es.web;



/**
 * The page provides user sign up panel.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SignUpPage extends BasePageSimple {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public SignUpPage() {

        SignUpPanel signUpPanel = new SignUpPanel("userSignUpPanel", getFeedbackPanel());
        add(signUpPanel);
    }

}
