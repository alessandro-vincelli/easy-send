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
package it.av.es.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Group extends BasicEntity {

    public final static String NAME = "name";
    public final static String DESCRIPTION = "description";
    @Field(store = Store.YES)
    private String name;
    private String description;
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<User> members;
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<User> administrators;

    /**
     * Constructor
     */
    public Group() {
        super();
    }

    /**
     * @param name
     */
    public Group(String name) {
        super();
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<User> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<User> administrators) {
        this.administrators = administrators;
    }

    public void addAdministrator(User user) {
        if (administrators == null) {
            administrators = new ArrayList<User>();
        }
        administrators.add(user);
    }

    public void addMember(User user) {
        if (members == null) {
            members = new ArrayList<User>();
        }
        members.add(user);
    }

    public void removeMember(User user) {
        Iterator<User> it = members.iterator();
        while (it.hasNext()) {
            User item = it.next();
            if (item.equals(user)) {
                it.remove();
            }
        }
    }

    public void removeAdministrator(User user) {
        Iterator<User> it = administrators.iterator();
        while (it.hasNext()) {
            User item = it.next();
            if (item.equals(user)) {
                it.remove();
            }
        }
    }
}