package net.agata.desktopmodel.domain.page.valueobject;

import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.types.PermissionEnum;

public class SharedPage {
    private PageID pageId;
    private UserID ownerId;
    private PermissionEnum permission;

    public SharedPage(PageID pageId, UserID ownerId, PermissionEnum permission) {
	super();
	this.pageId = pageId;
	this.ownerId = ownerId;
	this.permission = permission;
    }

    public PageID getPageId() {
	return pageId;
    }

    public UserID getOwnerId() {
	return ownerId;
    }

    public PermissionEnum getPermission() {
	return permission;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
	result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
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
	SharedPage other = (SharedPage) obj;
	if (ownerId == null) {
	    if (other.ownerId != null)
		return false;
	} else if (!ownerId.equals(other.ownerId))
	    return false;
	if (pageId == null) {
	    if (other.pageId != null)
		return false;
	} else if (!pageId.equals(other.pageId))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "SharedPage [pageId=" + pageId + ", ownerId=" + ownerId + ", permission=" + permission + "]";
    }


}
