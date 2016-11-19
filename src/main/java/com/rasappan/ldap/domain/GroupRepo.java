package com.rasappan.ldap.domain;

import org.springframework.ldap.repository.LdapRepository;
import org.springframework.ldap.repository.Query;

import javax.naming.Name;
import java.util.Collection;

/**
 * Spring Data-generated repository for Group administration. The methods defined in LdapRepository
 * and its superinterfaces directly map mot {@link org.springframework.ldap.repository.support.SimpleLdapRepository}.
 *
 * The methods defined in {@link GroupRepoExtension} are implemented in the generated instance by 'weaving in' a reference
 * to a bean in the applicationContext implementing the interface.
 *
 * In the {@link #findByName(String)} method, the filter will be automatically be generated based on
 * naming convention; the 'ByName' constraint will be fulfilled using a filter based on the attribute mapping of
 * the name attribute in the target entity class.
 *
 * The {@link #findByMember(javax.naming.Name)} acts on the Query annotation, building an
 * {@link org.springframework.ldap.query.LdapQuery} from the annotation attributes.
 */
public interface GroupRepo extends LdapRepository<Group>, GroupRepoExtension {
    public final static String USER_GROUP = "ROLE_USER";

    Group findByName(String groupName);

    @Query("(member={0})")
    Collection<Group> findByMember(Name member);
}
