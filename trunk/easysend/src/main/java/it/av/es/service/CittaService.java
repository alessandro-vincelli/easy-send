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
package it.av.es.service;

import it.av.es.model.Citta;

import java.util.List;

/**
 * Services on {@Link Citta}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface CittaService  {
    
    /**
     * Finds the citta using the given pattern on comune
     * 
     * @param comune
     * @param maxResults
     * @return found cities
     */
    List<Citta> findByComune(String comune, int maxResults);
    
    /**
     * Finds cap on using the given pattern on comune
     * 
     * @param comune
     * @param maxResults
     * @return found cities
     */
    List<String> findCapByComune(String comune, int maxResults);
    
    /**
     * 
     * @param pattern
     * @param maxResults
     * @return
     */
    List<String> findCap(String pattern, int maxResults);
    
    /**
     * Finds provincia on using the given pattern on comune
     * 
     * @param comune
     * @param maxResults
     * @return found cities
     */
    List<String> findProvinciaByComune(String comune, int maxResults);
    
    /**
     * Finds the citta using the given pattern on provincia
     * 
     * @param provincia
     * @param maxResults
     * @return found cities
     */
    List<Citta> findByProvincia(String provincia, int maxResults);

}