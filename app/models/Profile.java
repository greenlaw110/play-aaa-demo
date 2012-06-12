package models;

import com.google.code.morphia.annotations.Entity;
import play.Logger;
import play.data.validation.Password;
import play.modules.aaa.*;
import play.modules.aaa.utils.AAAFactory;
import play.modules.morphia.Model;

/**
 * A User Profile
 */
@Entity(value = "profile", noClassnameStored = true)
public class Profile extends Model {
    public String fullName;

    public String username;

    @Password
    public String password;

    private static IAccount findAccountByUsername(String username) {
        return (IAccount)AAAFactory.account()._findById(username);
    }

    private void checkUsername(String username) {
        IAccount account = findAccountByUsername(username);
        if (null != account) throw new RuntimeException("username already exists");
    }

    public void setUsername(String username) {
        if (!isNew()) {
            if (this.username.equals(username)) return;
            throw new IllegalStateException("Cannot change username");
        }
        checkUsername(username);
        this.username = username;
    }

    @OnAdd
    @RequirePrivilege("sys-admin")
    @RequireRight("manage-my-profile")
    public void createUserAccount() {
        Logger.info("saving new profile and create user account for it");
        IAccount account = findAccountByUsername(username);
        if (null != account) throw new RuntimeException("username already exists");
        checkUsername(username);
        account = AAAFactory.account().create(username);
        IRole role = (IRole)AAAFactory.role()._findById("client");
        account.assignRole(role).setPassword(password)._save();
    }

    @OnUpdate
    @RequirePrivilege("sys-admin")
    @RequireRight("manage-my-profile")
    public void checkUpdateAccess() {
        Logger.info("checking update access");
    }

    @RequirePrivilege("sys-admin")
    @RequireRight("manage-my-profile")
    @RequireAccounting("reset password")
    public void resetPassword(String password) {
        IAccount account = findAccountByUsername(username);
        account.setPassword(password)._save();
    }

    public static class DynamicAccessChecker implements PlayDynamicRightChecker.IAccessChecker<Profile> {
        @Override
        public boolean hasAccess(IAccount account, Profile profile) {
            return (account.getName().equals(profile.username));
        }
    }
}
