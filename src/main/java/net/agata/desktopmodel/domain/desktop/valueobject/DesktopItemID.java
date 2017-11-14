package net.agata.desktopmodel.domain.desktop.valueobject;

import org.apache.commons.lang3.Validate;

public class DesktopItemID {
    private String id;

    public DesktopItemID(String id) {
	super();
	setId(id);
    }

    private void setId(String id) {
	Validate.notBlank(id);
	this.id = id;
    }

    public String getId() {
	return id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	DesktopItemID other = (DesktopItemID) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "DesktopItemID [id=" + id + "]";
    }

}
