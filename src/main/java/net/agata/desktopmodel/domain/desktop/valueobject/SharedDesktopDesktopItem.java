package net.agata.desktopmodel.domain.desktop.valueobject;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;

public class SharedDesktopDesktopItem {
    private final IconID icon;
    private final ColorID color;
    private final PageID page;
    private final ApplicationID application;
    private final Short order;

    public SharedDesktopDesktopItem(IconID icon, ColorID color, PageID page, ApplicationID application, Short order) {
	super();
	Validate.notNull(icon);
	Validate.notNull(color);
	Validate.isTrue(page != null || application != null);
	Validate.notNull(order);
	this.icon = icon;
	this.color = color;
	this.page = page;
	this.application = application;
	this.order = order;
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

    public Short getOrder() {
	return order;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((application == null) ? 0 : application.hashCode());
	result = prime * result + ((color == null) ? 0 : color.hashCode());
	result = prime * result + ((icon == null) ? 0 : icon.hashCode());
	result = prime * result + ((order == null) ? 0 : order.hashCode());
	result = prime * result + ((page == null) ? 0 : page.hashCode());
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
	SharedDesktopDesktopItem other = (SharedDesktopDesktopItem) obj;
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
	if (icon == null) {
	    if (other.icon != null)
		return false;
	} else if (!icon.equals(other.icon))
	    return false;
	if (order == null) {
	    if (other.order != null)
		return false;
	} else if (!order.equals(other.order))
	    return false;
	if (page == null) {
	    if (other.page != null)
		return false;
	} else if (!page.equals(other.page))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "SharedDesktopDesktopItem [icon=" + icon + ", color=" + color + ", page=" + page + ", application=" + application
		+ ", order=" + order + "]";
    }

}
