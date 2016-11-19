package com.rasappan.ldap.service;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.rasappan.ldap.domain.DirectoryType;
import com.rasappan.ldap.domain.Group;
import com.rasappan.ldap.domain.User;
import com.rasappan.ldap.domain.UserRepo;
import com.rasappan.ldap.domain.GroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service("userService")
@Transactional
@PropertySource(value = "classpath:ldap.properties")
public class UserService implements BaseLdapNameAware {

    private final UserRepo userRepo;
    private final GroupRepo groupRepo;
    private LdapName baseLdapPath;

    @Value("${ldap.directory.type}")
    DirectoryType ldapDirectoryType;

    @Autowired
    public UserService(UserRepo userRepo, GroupRepo groupRepo) {
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
    }

    public Group getUserGroup() {
        return groupRepo.findByName(GroupRepo.USER_GROUP);
    }

    @Override
    public void setBaseLdapPath(LdapName baseLdapPath) {
        this.baseLdapPath = baseLdapPath;
    }

    public Iterable<User> findAll() {
        return userRepo.findAll();
    }

    public User findUser(String userId) {
        return userRepo.findOne(LdapUtils.newLdapName(userId));
    }

    public User createUser(User user) {
        User savedUser = userRepo.save(user);

        Group userGroup = getUserGroup();

        // The DN the member attribute must be absolute
        userGroup.addMember(toAbsoluteDn(savedUser.getId()));
        groupRepo.save(userGroup);

        return savedUser;
    }

    public LdapName toAbsoluteDn(Name relativeName) {
        return LdapNameBuilder.newInstance(baseLdapPath)
                .add(relativeName)
                .build();
    }

    /**
     * This method expects absolute DNs of group members. In order to find the actual users
     * the DNs need to have the base LDAP path removed.
     *
     * @param absoluteIds
     * @return
     */
    public Set<User> findAllMembers(Iterable<Name> absoluteIds) {
        return Sets.newLinkedHashSet(userRepo.findAll(toRelativeIds(absoluteIds)));
    }

    public Iterable<Name> toRelativeIds(Iterable<Name> absoluteIds) {
        return Iterables.transform(absoluteIds, new Function<Name, Name>() {
            @Override
            public Name apply(Name input) {
                return LdapUtils.removeFirst(input, baseLdapPath);
            }
        });
    }
    public User findByEmployeeNumber(int employeeNumber) {
        return userRepo.findByEmployeeNumber(employeeNumber);
    }

    public User updateUser(User user) {
        User existingUser = userRepo.findByEmployeeNumber(user.getEmployeeNumber());
        LdapName originalId = LdapUtils.newLdapName(existingUser.getId());

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setTitle(user.getTitle());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setUnit(user.getUnit());

        if (ldapDirectoryType == DirectoryType.AD) {
            return updateUserAd(originalId, existingUser);
        } else {
            return updateUserStandard(originalId, existingUser);
        }
    }

    public void deleteUser(User user) {
        User existingUser = userRepo.findByEmployeeNumber(user.getEmployeeNumber());
        LdapName originalId = LdapUtils.newLdapName(existingUser.getId());

        userRepo.delete(originalId);
    }

    /**
     * Update the user and - if its id changed - update all group references to the user.
     *
     * @param originalId the original id of the user.
     * @param existingUser the user, populated with new data
     *
     * @return the updated entry
     */
    private User updateUserStandard(LdapName originalId, User existingUser) {
        User savedUser = userRepo.save(existingUser);

        if(!originalId.equals(savedUser.getId())) {
            // The user has moved - we need to update group references.
            LdapName oldMemberDn = toAbsoluteDn(originalId);
            LdapName newMemberDn = toAbsoluteDn(savedUser.getId());

            Collection<Group> groups = groupRepo.findByMember(oldMemberDn);
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    /**
     * Special behaviour in AD forces us to get the group membership before the user is updated,
     * because AD clears group membership for removed entries, which means that once the user is
     * update we've lost track of which groups the user was originally member of, preventing us to
     * update the membership references so that they point to the new DN of the user.
     *
     * This is slightly less efficient, since we need to get the group membership for all updates
     * even though the user may not have been moved. Using our knowledge of which attributes are
     * part of the distinguished name we can do this more efficiently if we are implementing specifically
     * for Active Directory - this approach is just to highlight this quite significant difference.
     *
     * @param originalId the original id of the user.
     * @param existingUser the user, populated with new data
     *
     * @return the updated entry
     */
    private User updateUserAd(LdapName originalId, User existingUser) {
        LdapName oldMemberDn = toAbsoluteDn(originalId);
        Collection<Group> groups = groupRepo.findByMember(oldMemberDn);

        User savedUser = userRepo.save(existingUser);
        LdapName newMemberDn = toAbsoluteDn(savedUser.getId());

        if(!originalId.equals(savedUser.getId())) {
            // The user has moved - we need to update group references.
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    private void updateGroupReferences(Collection<Group> groups, Name originalId, Name newId) {
        for (Group group : groups) {
            group.removeMember(originalId);
            group.addMember(newId);

            groupRepo.save(group);
        }
    }

    public List<User> searchByNameName(String lastName) {
        return userRepo.findByFullNameContains(lastName);
    }
}
