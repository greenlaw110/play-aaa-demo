package models;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Password;
import play.data.validation.Required;
import play.libs.Crypto;
import play.modules.aaa.IAccount;
import play.modules.aaa.IRole;
import play.modules.aaa.PlayDynamicRightChecker;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequirePrivilege;
import play.modules.aaa.RequireRight;
import play.modules.aaa.utils.AAAFactory;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Transient;

/**
 * A User Profile
 */
@Entity(value = "users", noClassnameStored = true)
public class User extends Model {

	@Required
	public String username;

	@Required
	@Password
	public String password;

	@Required
	public String fullName;

	/**
	 * Hold the raw password for later use. Won't save to db.
	 */
	@Transient
	private String rawPassword;

	public User(String username, String password, String fullName) {
		this.username = username;
		this.fullName = fullName;
		if (StringUtils.isNotBlank(password)) {
			resetPassword(password);
		}
	}

	public void setUsername(String username) {
		if (!isNew()) {
			if (this.username.equals(username))
				return;
			throw new IllegalStateException("Cannot change username");
		}
		checkUsername(username);
		this.username = username;
	}

	public void resetPassword(String password) {
		this.rawPassword = password;
		this.password = cryptPassword(password);
	}

	public boolean checkPassword(String password) {
		return this.password.equals(cryptPassword(password));
	}

	public IAccount getAccount() {
		return findAccountByUsername(username);
	}

	@Override
	public String toString() {
		return username;
	}

	@OnAdd
	@RequireAccounting("create new profile")
	void createUserAccount() {
		Logger.info("saving new profile and create user account for it");
		IAccount account = getAccount();
		if (null != account)
			throw new RuntimeException("username already exists");
		checkUsername(username);
		account = AAAFactory.account().create(username);
		IRole role = (IRole) AAAFactory.role()._findById("client");
		account.assignRole(role);
		if (this.rawPassword != null) {
			account.setPassword(this.rawPassword);
		}
		account._save();
	}

	@OnUpdate
	@RequirePrivilege("sys-admin")
	@RequireRight("manage-my-profile")
	@RequireAccounting("update profile")
	void checkUpdateAccess() {
		Logger.info("checking update access");
		if (this.rawPassword != null) {
			IAccount account = getAccount();
			account.setPassword(this.rawPassword)._save();
		}
	}

	@OnDelete
	@RequirePrivilege("sys-admin")
	@RequireAccounting("delete user")
	void checkDeleteAccess() {
		Logger.info("checking delete access");
	}

	private String cryptPassword(String password) {
		return Crypto.passwordHash(password + ":" + username + ":" + password);
	}

	private void checkUsername(String username) {
		long count = count("username", username);
		if (count > 0)
			throw new RuntimeException("username already exists");
	}

	private static IAccount findAccountByUsername(String username) {
		return (IAccount) AAAFactory.account()._findById(username);
	}

	public static class DynamicAccessChecker implements PlayDynamicRightChecker.IAccessChecker<User> {
		@Override
		public boolean hasAccess(IAccount account, User user) {
			return account.getName().equals(user.username);
		}
	}

}
