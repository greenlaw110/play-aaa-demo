package models;

import play.modules.aaa.IAccount;
import play.modules.aaa.PlayDynamicRightChecker;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/06/12
 * Time: 6:10 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IDynamicRightAsset {

    Profile owner();

    public static class DynamicAccessChecker implements PlayDynamicRightChecker.IAccessChecker<IDynamicRightAsset> {
        @Override
        public boolean hasAccess(IAccount account, IDynamicRightAsset asset) {
            Profile profile = asset.owner();
            return (account.getName().equals(profile.username));
        }
    }
}
