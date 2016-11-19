package com.rasappan.ldap.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.Name;
import java.io.Serializable;

@ApiModel(value = "User")
@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" }, base = "ou=Departments")
public final class User implements Serializable {
    @Id
    @JsonIgnore
    private Name id;

    @Attribute(name = "cn")
    @DnAttribute(value="cn", index=3)
    private String fullName;

    @Attribute(name = "employeeNumber")
    private int employeeNumber;

    @Attribute(name = "givenName")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "title")
    private String title;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "telephoneNumber")
    private String phone;

    @DnAttribute(value="ou", index=2)
    @Transient
    private String unit;

    @DnAttribute(value="ou", index=1)
    @Transient
    private String department;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Name getId() {
        return id;
    }

    public void setId(Name id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = LdapUtils.newLdapName(id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
