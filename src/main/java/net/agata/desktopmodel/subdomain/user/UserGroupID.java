package net.agata.desktopmodel.subdomain.user;

import org.apache.commons.lang3.Validate;

public class UserGroupID {
    private Integer userGroupId;

    public UserGroupID(Integer userGroupId) {
	super();
	setUserGroupId(userGroupId);
    }

    public Integer getUserGroupId() {
	return userGroupId;
    }

    private void setUserGroupId(Integer userGroupId) {
	Validate.notNull(userGroupId);
	this.userGroupId = userGroupId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((userGroupId == null) ? 0 : userGroupId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UserGroupID other = (UserGroupID) obj;
	if (userGroupId == null) {
	    if (other.userGroupId != null)
		return false;
	} else if (!userGroupId.equals(other.userGroupId))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "UserGroupID [userGroupId=" + userGroupId + "]";
    }

}
