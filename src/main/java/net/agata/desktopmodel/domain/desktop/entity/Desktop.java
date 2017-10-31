package net.agata.desktopmodel.domain.desktop.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopSatateEnum;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.ui.PageID;
import net.agata.desktopmodel.subdomain.user.UserID;

public class Desktop {

    private DesktopID desktopId;
    private String name;
    private UserID userId;
    private Short order;
    private Boolean fixed;
    private Boolean readonly;
    private DesktopSatateEnum state;
    private Set<DesktopItem> items = new HashSet<>();

    public Desktop(DesktopID desktopId, String name, UserID userId, Short order, Boolean fixed, Boolean readonly, DesktopSatateEnum state, Set<DesktopItem> items) {
	super();
	setDesktopId(desktopId);
	setName(name);
	setUserId(userId);
	setOrder(order);
	setFixed(fixed);
	setReadonly(readonly);
	setState(state);
	setItems(items);
    }

    /**
     * BUSINESS LOGIC
     */

    public void addApplication(IconID iconId, ColorID colorId, String name, ApplicationID applicationId, Boolean isFavourite) {
	Validate.isTrue(isActive(), "No se puede añadir una aplicación a un escritorio que no está activo");
	this.items.add(new DesktopItem(this.desktopId, iconId, colorId, name, null, applicationId, isFavourite, nextItemOrder()));
    }

    public void addPage(IconID iconId, ColorID colorId, String name, PageID pageId, Boolean isFavourite) {
	Validate.isTrue(isActive(), "No se puede añadir una página a un escritorio que no está activo");
	this.items.add(new DesktopItem(this.desktopId, iconId, colorId, name, pageId, null, isFavourite, nextItemOrder()));
    }

    private Short nextItemOrder() {
	return (short) this.items.stream()
		  		 .mapToInt(di -> di.getOrder().intValue())
		  		 .max()
		  		 .orElse(0);
    }

    public boolean isActive() {
	return DesktopSatateEnum.ACTIVE.equals(this.state);
    }

    public void remove() {
	Validate.isTrue(isActive() && !fixed && !readonly,
		"Sólo se pueden eliminar escritorios activos y que no sean fijos ni de solo lectura");
	setState(DesktopSatateEnum.DELETED);
    }

    public void reorder(Short order) {
	if (isActive()) {
	    setOrder(order);
	}
    }

    /**
     * ACCESSORS
     */

    public DesktopID getDesktopId() {
	return desktopId;
    }

    public void setDesktopId(DesktopID desktopId) {
	Validate.notNull(desktopId);
	this.desktopId = desktopId;
    }

    public String getName() {
	return name;
    }

    private void setName(String name) {
	Validate.notBlank(name);
	this.name = name;
    }

    public UserID getUserId() {
	return userId;
    }

    private void setUserId(UserID userId) {
	Validate.notNull(userId);
	this.userId = userId;
    }

    public Short getOrder() {
	return order;
    }

    private void setOrder(Short order) {
	Validate.notNull(order);
	this.order = order;
    }

    public Boolean getFixed() {
	return fixed;
    }

    private void setFixed(Boolean fixed) {
	Validate.notNull(fixed);
	this.fixed = fixed;
    }

    public Boolean getReadonly() {
	return readonly;
    }

    private void setReadonly(Boolean readonly) {
	Validate.notNull(readonly);
	this.readonly = readonly;
    }

    public DesktopSatateEnum getState() {
	return state;
    }

    private void setState(DesktopSatateEnum state) {
	Validate.notNull(state);
	this.state = state;
    }

    public List<DesktopItem> getItems() {
	return new ArrayList<>(items);
    }

    private void setItems(Set<DesktopItem> items) {
	Validate.notNull(items);
	this.items.addAll(items);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((desktopId == null) ? 0 : desktopId.hashCode());
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
	Desktop other = (Desktop) obj;
	if (desktopId == null) {
	    if (other.desktopId != null)
		return false;
	} else if (!desktopId.equals(other.desktopId))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Desktop [desktopId=" + desktopId + ", name=" + name + ", userId=" + userId + ", order=" + order + ", fixed=" + fixed
		+ ", readonly=" + readonly + ", state=" + state + ", items=" + items + "]";
    }

}
