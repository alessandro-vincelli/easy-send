package it.av.es.web.manager;

import it.av.es.model.Group;
import it.av.es.model.User;
import it.av.es.service.GroupService;
import it.av.es.service.UserService;
import it.av.es.web.BasePageSimple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

@AuthorizeInstantiation({ "ADMIN" })
public class GroupMembersManagerPage extends BasePageSimple {

    @SpringBean
    private GroupService groupService;
    @SpringBean
    private UserService userService;
    private ListMultipleChoice<User> users;
    private DropDownChoice<Group> groups;
    private ListMultipleChoice<User> membersOf;

    public GroupMembersManagerPage() {
        super();

        final Form<Bean> form = new Form<Bean>("form", new CompoundPropertyModel<Bean>(new Bean()));
        add(form);

        form.add(groups = new DropDownChoice<Group>("group", groupService.getAll()));
        form.add(groups);
        groups.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateUsersList();
                membersOf.setChoices(groups.getModelObject().getMembers());
                target.add(form);
            }
        });
        
        
        users = new ListMultipleChoice<User>("users", userService.getAll());
        form.add(users);
        AjaxSubmitLink addUser = new AjaxSubmitLink("addUser") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                groups.getModelObject().addAllMember(new ArrayList<User>(users.getModelObject()));
                groups.setModelObject(groupService.save(groups.getModelObject()));
                membersOf.setChoices(groups.getModelObject().getMembers());
                updateUsersList();
                target.add(form);
            }
        };
        form.add(addUser);
        
        AjaxSubmitLink removeUser = new AjaxSubmitLink("removeUser") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                groups.getModelObject().removeAllMember(new ArrayList<User>(membersOf.getModelObject()));
                groups.setModelObject(groupService.save(groups.getModelObject()));
                membersOf.setChoices(groups.getModelObject().getMembers());
                updateUsersList();
                target.add(form);
            }
        };
        form.add(removeUser);
        membersOf = new ListMultipleChoice<User>("usersMemberOf");
        form.add(membersOf);
      
    }

    /**
     * 
     */
    private void updateUsersList() {
        List<User> members = groups.getModelObject().getMembers();
        List<User> all = userService.getAll();
        all.removeAll(members);
        users.setChoices(all);
    }
    
    class Bean implements Serializable{
        private List<User> usersMemberOf;
        private List<User> users;
        private Group group;

        public Bean() {

        }

        public List<User> getUsersMemberOf() {
            return usersMemberOf;
        }

        public void setUsersMemberOf(List<User> usersMemberOf) {
            this.usersMemberOf = usersMemberOf;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }


    }
}
