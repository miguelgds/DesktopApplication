package net.agata.desktopmodel.domain.desktop.entity;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.ui.PageID;

public class DesktopItem {

    private DesktopID desktopId;
    private IconID iconId;
    private ColorID colorId;
    private String name;
    private PageID pageId;
    private ApplicationID applicationId;
    private Boolean isFavourite;
    private Short order;

    public DesktopItem(DesktopID desktopId, IconID iconId, ColorID colorId, String name, PageID pageId, ApplicationID applicationId,
	    Boolean isFavourite, Short order) {
	super();
	setDesktopId(desktopId);
	setIconId(iconId);
	setColorId(colorId);
	setName(name);
	Validate.isTrue(pageId != null || applicationId != null);
	setPageId(pageId);
	setApplicationId(applicationId);
	setIsFavourite(isFavourite);
	setOrder(order);
    }
    
    public DesktopItem(DesktopItem item){
	this(item.getDesktopId(), item.getIconId(), item.getColorId(), item.getName(), item.getPageId(), item.getApplicationId(), item.getIsFavourite(), item.getOrder());
    }

    /**
     * BUSINESS LOGIC
     */
    public boolean isApplication() {
	return applicationId == null;
    }

    public boolean isPage() {
	return pageId == null;
    }

    public void reorder(Short order) {
	setOrder(order);
    }

    public void moveToDesktop(DesktopID desktopTo) {
	setDesktopId(desktopTo);
    }

    public void unsetAsFavourite() {
	if (getIsFavourite()) {
	    setIsFavourite(false);
	}
    }

    public void setAsFavourite() {
	if (!getIsFavourite()) {
	    setIsFavourite(true);
	}
    }

    /**
     * ACCESSORS
     */

    public DesktopID getDesktopId() {
	return desktopId;
    }

    private void setDesktopId(DesktopID desktopId) {
	Validate.notNull(desktopId);
	this.desktopId = desktopId;
    }

    public IconID getIconId() {
	return iconId;
    }

    private void setIconId(IconID iconId) {
	Validate.notNull(iconId);
	this.iconId = iconId;
    }

    public ColorID getColorId() {
	return colorId;
    }

    private void setColorId(ColorID colorId) {
	Validate.notNull(colorId);
	this.colorId = colorId;
    }

    public String getName() {
	return name;
    }

    private void setName(String name) {
	Validate.notBlank(name);
	this.name = name;
    }

    public PageID getPageId() {
	return pageId;
    }

    private void setPageId(PageID pageId) {
	this.pageId = pageId;
    }

    public ApplicationID getApplicationId() {
	return applicationId;
    }

    private void setApplicationId(ApplicationID applicationId) {
	this.applicationId = applicationId;
    }

    public Boolean getIsFavourite() {
	return isFavourite;
    }

    private void setIsFavourite(Boolean isFavourite) {
	Validate.notNull(isFavourite);
	this.isFavourite = isFavourite;
    }

    public Short getOrder() {
	return order;
    }

    private void setOrder(Short order) {
	Validate.notNull(order);
	this.order = order;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((desktopId == null) ? 0 : desktopId.hashCode());
	result = prime * result + ((order == null) ? 0 : order.hashCode());
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
	DesktopItem other = (DesktopItem) obj;
	if (desktopId == null) {
	    if (other.desktopId != null)
		return false;
	} else if (!desktopId.equals(other.desktopId))
	    return false;
	if (order == null) {
	    if (other.order != null)
		return false;
	} else if (!order.equals(other.order))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "DesktopItem [desktopId=" + desktopId + ", iconId=" + iconId + ", colorId=" + colorId + ", name=" + name + ", pageId="
		+ pageId + ", applicationId=" + applicationId + ", isFavourite=" + isFavourite + ", order=" + order + "]";
    }

}
