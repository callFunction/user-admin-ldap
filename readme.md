
### Users and Groups in LDAP

Users in LDAP are usually represented as `organizationalPerson` or `inetOrgPerson` entries in the LDAP tree.
The attributes in these entries represent the basic information on the users, e.g. (subset of available attributes):

* `cn`: Common Name, or full name of the user
* `sn`: Surname
* `mail`: Email address
* `telephoneNumber`: Phone number
* ...several more attributes are available

Groups in LDAP are typically organized in `groupOfName` or `groupOfUniqueName` entries. These entries have a name,
an optional description, and an attribute containing the distinguished names (i.e. the unique identifiers) of all
members of the group. This attribute is named `member` and `uniqueMember` respectively.

Thus, user administration in LDAP typically involves creating and manipulating `orgalizationalPerson` or `inetOrgPerson`
entries and adding or removing references to these entries in `groupOfName` or `groupOfUniqueName` entries.

This application demonstrates to do this easily and efficiently using Spring LDAP.
