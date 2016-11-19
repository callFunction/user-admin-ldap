package com.rasappan.ldap.domain;

import org.springframework.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends LdapRepository<User> {
    User findByEmployeeNumber(int employeeNumber);
    List<User> findByFullNameContains(String name);
}
