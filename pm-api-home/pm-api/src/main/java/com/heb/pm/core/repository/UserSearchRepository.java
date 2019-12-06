package com.heb.pm.core.repository;

import com.heb.pm.util.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for repository that return user information.
 *
 * @author d116773
 * @since 1.0.0
 */
@Repository
public interface UserSearchRepository {

	/**
	 * Returns a list of users searching by user names.
	 *
	 * @param userNameList The list of names to search for.
	 * @return A list of users who match the names provided.
	 * @throws Exception
	 */
	List<User> getUserList(Iterable<String> userNameList) throws Exception;
}
