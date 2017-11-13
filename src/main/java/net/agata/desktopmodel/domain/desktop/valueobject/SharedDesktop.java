package net.agata.desktopmodel.domain.desktop.valueobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.utils.types.PermissionEnum;

public class SharedDesktop {
    private final DesktopID desktopId;
    private final String name;
    private final Boolean fixed;
    private final Boolean readonly;
    private final PermissionEnum permission;
    private final List<SharedDesktopDesktopItem> items = new ArrayList<>();

    public SharedDesktop(DesktopID desktopId, String name, Boolean fixed, Boolean readonly, PermissionEnum permission,
	    List<SharedDesktopDesktopItem> items) {
	super();
	Validate.notNull(desktopId);
	Validate.notBlank(name);
	Validate.notNull(fixed);
	Validate.notNull(readonly);
	Validate.notNull(permission);
	this.desktopId = desktopId;
	this.name = name;
	this.fixed = fixed;
	this.readonly = readonly;
	this.permission = permission;
	this.items.addAll(items);
    }

    public DesktopID getDesktopId() {
	return desktopId;
    }

    public String getName() {
	return name;
    }

    public Boolean getFixed() {
	return fixed;
    }

    public Boolean getReadonly() {
	return readonly;
    }

    public PermissionEnum getPermission() {
	return permission;
    }

    public List<SharedDesktopDesktopItem> getItems() {
	return items;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((desktopId == null) ? 0 : desktopId.hashCode());
	result = prime * result + ((fixed == null) ? 0 : fixed.hashCode());
	result = prime * result + ((items == null) ? 0 : items.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((permission == null) ? 0 : permission.hashCode());
	result = prime * result + ((readonly == null) ? 0 : readonly.hashCode());
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
	SharedDesktop other = (SharedDesktop) obj;
	if (desktopId == null) {
	    if (other.desktopId != null)
		return false;
	} else if (!desktopId.equals(other.desktopId))
	    return false;
	if (fixed == null) {
	    if (other.fixed != null)
		return false;
	} else if (!fixed.equals(other.fixed))
	    return false;
	if (items == null) {
	    if (other.items != null)
		return false;
	} else if (!items.equals(other.items))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (permission != other.permission)
	    return false;
	if (readonly == null) {
	    if (other.readonly != null)
		return false;
	} else if (!readonly.equals(other.readonly))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "SharedDesktop [desktopId=" + desktopId + ", name=" + name + ", fixed=" + fixed + ", readonly=" + readonly + ", permission="
		+ permission + ", items=" + items + "]";
    }

}
