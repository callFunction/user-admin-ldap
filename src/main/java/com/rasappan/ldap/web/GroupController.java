package com.rasappan.ldap.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.rasappan.ldap.domain.GroupRepo;
import com.rasappan.ldap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Api(value = "Group Controller", description = "Group Management APIs")
public class GroupController {

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Get all Groups")
    @RequestMapping(value = "/groups", method = GET)
    public ResponseEntity<List<String>> listAllGroups() {
        return new ResponseEntity<>(groupRepo.getAllGroupNames(), HttpStatus.OK);
    }

    /*
    @RequestMapping(value = "/newGroup", method = GET)
    public String initNewGroup() {
        return "newGroup";
    }

    @RequestMapping(value = "/groups", method = POST)
    public String newGroup(Group group) {
        groupRepo.create(group);

        return "redirect:groups/" + group.getName();
    }

    @RequestMapping(value = "/groups/{name}", method = GET)
    public String editGroup(@PathVariable String name, ModelMap map) {
        Group foundGroup = groupRepo.findByName(name);
        map.put("group", foundGroup);

        final Set<User> groupMembers = userService.findAllMembers(foundGroup.getMembers());
        map.put("members", groupMembers);

        Iterable<User> otherUsers = Iterables.filter(userService.findAll(), new Predicate<User>() {
            @Override
            public boolean apply(User user) {
                return !groupMembers.contains(user);
            }
        });
        map.put("nonMembers", Lists.newLinkedList(otherUsers));

        return "editGroup";
    }

    @RequestMapping(value = "/groups/{name}/members", method = POST)
    public String addUserToGroup(@PathVariable String name, @RequestParam String userId) {
        Group group = groupRepo.findByName(name);
        group.addMember(userService.toAbsoluteDn(LdapUtils.newLdapName(userId)));

        groupRepo.save(group);

        return "redirect:/groups/" + name;
    }

    @RequestMapping(value = "/groups/{name}/members", method = DELETE)
    public String removeUserFromGroup(@PathVariable String name, @RequestParam String userId) {
        Group group = groupRepo.findByName(name);
        group.removeMember(userService.toAbsoluteDn(LdapUtils.newLdapName(userId)));

        groupRepo.save(group);

        return "redirect:/groups/" + name;
    }
    */
}
