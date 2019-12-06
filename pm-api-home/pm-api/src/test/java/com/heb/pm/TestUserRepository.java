package com.heb.pm;

import com.heb.pm.core.repository.UserSearchRepository;
import com.heb.pm.util.user.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Fakes a user repository since we won't have access to LDAP when running tests.
 */
public class TestUserRepository implements UserSearchRepository {

	@Override
	public List<User> getUserList(Iterable<String> userNameList) throws Exception {

		List<User> users = new LinkedList<>();

		for (String userName : userNameList) {
			if ("c591918".equalsIgnoreCase(userName)) {
				users.add(new User("c591918", "c591918", "Cristina", "Callahan")
						.setFullName("Callahan,Cristina"));
			}
			if ("h361956".equalsIgnoreCase(userName)) {
				users.add(new User("h361956", "h361956", "Hooks", "Brandon")
						.setFullName("Hooks,Brandon"));
			}
			if ("vn94553".equalsIgnoreCase(userName)) {
				users.add(new User("vn94553", "vn94553", "Varghese", "Shimmy")
						.setFullName("Varghese, Shimmy").setJobCode("004563"));
			}
		}
		return users;
	}
}
