package models;

import com.google.code.morphia.annotations.Entity;
import com.greenlaw110.play.api.IUser;
import controllers.filters.Config;
import play.Logger;
import play.data.validation.Password;
import play.data.validation.Required;
import play.modules.aaa.*;
import play.modules.aaa.utils.AAAFactory;
import play.modules.morphia.Model;
import play.modules.morphia.validation.Unique;
import sys.App;

/**
 * A User Profile
 */
@Entity(value = "user", noClassnameStored = true)
public class User extends Model implements IUserProperty, IUser {
    @Required
    public String fullName;

    @Required
    @Unique
    public String username;

    @Required
    @Password
    transient public String password;

    public User(String username, String password, String fullName) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
    }

    @Override
    public User owner() {
        return this;
    }

    public static User current() {
        return (User)Config.app.currentUser();
    }

    private static IAccount findAccountByUsername(String username) {
        return (IAccount)AAAFactory.account()._findById(username);
    }

    public static User findByUsername(String username) {
        return User.find("username", username).get();
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
    //allow registration @RequirePrivilege("sys-admin")
    //allow registration @RequireRight("manage-my-profile")
    @RequireAccounting("create new profile")
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
    @RequireAccounting("update profile")
    void checkUpdateAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking update access");
    }

    @RequirePrivilege("sys-admin")
    @RequireRight("manage-my-profile")
    @RequireAccounting("reset password")
    public void resetPassword(String password) {
        IAccount account = findAccountByUsername(username);
        account.setPassword(password)._save();
    }

    @RequireAccounting("delete profile")
    @RequirePrivilege("sys-admin")
    @RequireRight("manage-user-profile")
    void checkDeleteAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking delete access");
    }

}
