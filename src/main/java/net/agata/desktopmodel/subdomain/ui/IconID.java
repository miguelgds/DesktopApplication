package net.agata.desktopmodel.subdomain.ui;

import org.apache.commons.lang3.Validate;

public class IconID {

    private Short id;

    public IconID(Short id) {
	super();
	setId(id);
    }

    public Short getId() {
	return id;
    }

    private void setId(Short id) {
	Validate.notNull(id);
	this.id = id;
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
	IconID other = (IconID) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "IconID [id=" + id + "]";
    }

}
