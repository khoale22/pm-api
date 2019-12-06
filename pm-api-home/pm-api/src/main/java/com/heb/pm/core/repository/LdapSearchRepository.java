/*
 * LdapSearchRepository
 *
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.core.repository;

import com.heb.pm.util.user.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.stereotype.Repository;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.*;

/**
 * The repository to search on Ldap.
 *
 * @author l730832
 * @since 1.0.0
 */
@Repository
@Profile({"local", "cert", "prod"})
public class LdapSearchRepository implements UserSearchRepository {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LdapSearchRepository.class);

	@Autowired
	private transient LdapTemplate hebLdapTemplate;

	@Value("${heb.ldap.userSearchBase}")
	private transient String searchBase;

	@Value("${heb.ldap.url}")
	private transient String ldapUrl;

	/**
	 * This is the mapper that comes from LDAP and maps it to User.
	 */
	private static class LdapAttributeMapper implements AttributesMapper<User> {

		/**
		 * Other attributes that may be needed similar to "uid".
		 * hebJobCode      - the Heb Job Code.
		 * hebjobdesc      - the Heb Job Desc
		 * mail            - the mail.
		 * hebUser         - the Heb User
		 * hebDeptName     - the Heb Dept Name
		 * hebGLdepartment - the Heb GL Department
		 * hebGLlocation   - the Heb GL Location
		 * hebRegion       - the Heb role code.
		 * @param attributes
		 * @return
		 * @throws NamingException
		 */
		@Override
		public User mapFromAttributes(Attributes attributes) throws NamingException {
			User user = new User();

			this.lookupAttributeById(attributes, "uid").ifPresent(user::setUid);
			this.lookupAttributeById(attributes, "hebLegacyId").ifPresent(user::setHebLegacyID);
			this.lookupAttributeById(attributes, "givenName").ifPresent(user::setGivenName);
			this.lookupAttributeById(attributes, "sn").ifPresent(user::setLastName);
			this.lookupAttributeById(attributes, "cn").ifPresent(user::setFullName);
			this.lookupAttributeById(attributes, "mail").ifPresent(user::setMail);
			this.lookupAttributeById(attributes, "telephonenumber").ifPresent(user::setTelephoneNumber);
			this.lookupAttributeById(attributes, "hebJobCode").ifPresent(user::setJobCode);

			return user;
		}

		private Optional<String> lookupAttributeById(Attributes attributes, String id) throws NamingException {

			Attribute attribute = attributes.get(id);
			if (Objects.nonNull(attribute)) {
				return Optional.of(attribute.get().toString());
			}
			return Optional.empty();
		}
	}

	/**
	 * Returns a list of users from LDAP using the uid or the hebLegacyId.
	 * @param userNameList
	 * @return List of users without duplicates.
	 * @throws Exception
	 */
	@Override
	public List<User> getUserList(Iterable<String> userNameList) throws Exception {
		List<User> users = new ArrayList<>();

		if (LdapSearchRepository.logger.isTraceEnabled()) {
			LdapSearchRepository.logger.trace("Connected to LDAP with URL " + this.ldapUrl);
			StringBuilder sb = new StringBuilder();
			Iterator<String> userNameIterator = userNameList.iterator();
			while (userNameIterator.hasNext()) {
				sb.append(userNameIterator.next());
				if (userNameIterator.hasNext()) {
					sb.append(',');
				}
			}
			LdapSearchRepository.logger.trace("Looking for users [" + sb.toString() + "]");
		}

		OrFilter filter = new OrFilter();
		OrFilter hebLegacyIdFilter = new OrFilter();
		if (userNameList != null) {
			for (String userName : userNameList) {
				if (StringUtils.isNotBlank(userName)) {
					filter.or(new EqualsFilter("uid", userName.trim()));
					hebLegacyIdFilter.or(new EqualsFilter("hebLegacyId", userName.trim()));
					filter.append(hebLegacyIdFilter);
				}
			}
			users = this.hebLdapTemplate.search(this.searchBase, filter.encode(), new LdapAttributeMapper());
		}

		if (users != null) {
			Set<User> hashset = new HashSet<>(users);
			users.clear();
			users.addAll(hashset);
		}
		return users;
	}
}
