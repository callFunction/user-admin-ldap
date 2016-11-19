package com.rasappan.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathBeanPostProcessor;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@PropertySource(value = "classpath:ldap.properties")
public class LdapConfig {

	@Value("${ldap.url}")
	String ldapUrl;

	@Value("${ldap.base}")
	String ldapBase;

	@Value("${ldap.userDn}")
	String ldapUser;

	@Value("${ldap.password}")
	String ldapPassword;

	@Bean
	public LdapContextSource contextSource () {
		LdapContextSource contextSource= new LdapContextSource();
		contextSource.setUrl(ldapUrl);
		contextSource.setBase(ldapBase);
		contextSource.setUserDn(ldapUser);
		contextSource.setPassword(ldapPassword);
		return contextSource;
	}

	@Bean
	public LdapTemplate ldapTemplate() {
		return new LdapTemplate(contextSource());
	}

	@Bean
	public BaseLdapPathBeanPostProcessor baseLdapPathBeanPostProcessor() {
		return new BaseLdapPathBeanPostProcessor();
	}

}