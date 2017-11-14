package net.agata.desktopmodel.domain.desktop.valueobject;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.utils.types.PermissionEnum;

public class SharedDesktopItem {
    private final DesktopItemID desktopItemId;
    private final IconID icon;
    private final ColorID color;
    private final PageID page;
    private final ApplicationID application;
    private final PermissionEnum permission;

    public SharedDesktopItem(DesktopItemID desktopItemId, IconID icon, ColorID color, PageID page, ApplicationID application,
	    PermissionEnum permission) {
	super();
	Validate.notNull(desktopItemId);
	Validate.notNull(icon);
	Validate.notNull(color);
	Validate.isTrue(page != null || application != null);
	Validate.notNull(permission);
	this.desktopItemId = desktopItemId;
	this.icon = icon;
	this.color = color;
	this.page = page;
	this.application = application;
	this.permission = permission;
    }

    public DesktopItemID getDesktopItemId() {
	return desktopItemId;
    }

    public IconID getIcon() {
	return icon;
    }

    public ColorID getColor() {
	return color;
    }

    public PageID getPage() {
	return page;
    }

    public ApplicationID getApplication() {
	return application;
    }

    public PermissionEnum getPermission() {
	return permission;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((application == null) ? 0 : application.hashCode());
	result = prime * result + ((color == null) ? 0 : color.hashCode());
	result = prime * result + ((desktopItemId == null) ? 0 : desktopItemId.hashCode());
	result = prime * result + ((icon == null) ? 0 : icon.hashCode());
	result = prime * result + ((page == null) ? 0 : page.hashCode());
	result = prime * result + ((permission == null) ? 0 : permission.hashCode());
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
	SharedDesktopItem other = (SharedDesktopItem) obj;
	if (application == null) {
	    if (other.application != null)
		return false;
	} else if (!application.equals(other.application))
	    return false;
	if (color == null) {
	    if (other.color != null)
		return false;
	} else if (!color.equals(other.color))
	    return false;
	if (desktopItemId == null) {
	    if (other.desktopItemId != null)
		return false;
	} else if (!desktopItemId.equals(other.desktopItemId))
	    return false;
	if (icon == null) {
	    if (other.icon != null)
		return false;
	} else if (!icon.equals(other.icon))
	    return false;
	if (page == null) {
	    if (other.page != null)
		return false;
	} else if (!page.equals(other.page))
	    return false;
	if (permission != other.permission)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "SharedDesktopItem [desktopItemId=" + desktopItemId + ", icon=" + icon + ", color=" + color + ", page=" + page
		+ ", application=" + application + ", permission=" + permission + "]";
    }

}
