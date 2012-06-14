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
public class Profile extends Model implements IDynamicRightAsset {
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

    @Override
    public Profile owner() {
        return this;
    }

}
