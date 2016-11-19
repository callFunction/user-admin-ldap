package com.rasappan.ldap.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rasappan.ldap.domain.DepartmentRepo;
import com.rasappan.ldap.domain.User;
import com.rasappan.ldap.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api(value = "User Controller", description = "User Management APIs")
@RestController
public class UserController {

    private final AtomicInteger nextEmployeeNumber = new AtomicInteger(10);

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepo departmentRepo;

    @ApiOperation(value = "Get all Users")
    @RequestMapping(value = {"/users"}, method = GET)
    public ResponseEntity<List<User>> listAllUsers(@RequestParam(required = false) String name) {
        if(StringUtils.hasText(name)) {
            return new ResponseEntity<>(userService.searchByNameName(name), HttpStatus.OK);
        } else {
            List<User> users = StreamSupport
                    .stream(userService.findAll().spliterator(),false)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get User")
    @RequestMapping(value = "/users/{empnum}", method = GET)
    public ResponseEntity<User> getUser(@PathVariable String empnum) throws JsonProcessingException {
        return new ResponseEntity<>(userService.findByEmployeeNumber(Integer.parseInt(empnum)), HttpStatus.OK);
    }

    @ApiOperation(value = "Create User")
    @RequestMapping(value = "/users", method = POST)
    public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        user.setEmployeeNumber(nextEmployeeNumber.getAndIncrement());
        User savedUser = userService.createUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/users/{uri}").buildAndExpand(savedUser.getEmployeeNumber()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update User")
    @RequestMapping(value = "/users/{empnum}", method = PUT)
    public ResponseEntity<User> updateUser(@PathVariable String empnum, @RequestBody User user) {
        User userIfExists = userService.findByEmployeeNumber(Integer.parseInt(empnum));
        if (userIfExists == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User savedUser = userService.updateUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete User")
    @RequestMapping(value = "/users/{empnum}", method = DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable String empnum) {
        User userIfExists = userService.findByEmployeeNumber(Integer.parseInt(empnum));
        if (userIfExists == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(userIfExists);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}