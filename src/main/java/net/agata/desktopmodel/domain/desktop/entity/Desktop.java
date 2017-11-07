package net.agata.desktopmodel.domain.desktop.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Validate;

import io.vavr.control.Option;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopSatateEnum;
import net.agata.desktopmodel.domain.desktop.valueobject.DisplacementMode;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.ui.PageID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.exceptions.ExceptionUtils;

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
    
    public Desktop(Desktop desktop){
	this(desktop.getDesktopId(), desktop.getName(), desktop.getUserId(), desktop.getOrder(), desktop.getFixed(), desktop.getReadonly(), desktop.getState(), 
		desktop.getItems()
		       .stream()
		       .map(DesktopItem::new)
		       .collect(Collectors.toSet()));
    }

    /**
     * BUSINESS LOGIC
     */

    public DesktopItem addApplication(IconID iconId, ColorID colorId, ApplicationID applicationId) {
	Validate.isTrue(isActive(), "No se puede añadir una aplicación a un escritorio que no está activo");

	DesktopItem newItem = new DesktopItem(this.desktopId, iconId, colorId, null, applicationId, false, nextItemOrder());
	this.items.add(newItem);
	return newItem;
    }

    public DesktopItem addPage(IconID iconId, ColorID colorId, PageID pageId) {
	Validate.isTrue(isActive(), "No se puede añadir una página a un escritorio que no está activo");

	DesktopItem newItem = new DesktopItem(this.desktopId, iconId, colorId, pageId, null, false, nextItemOrder());
	this.items.add(newItem);
	return newItem;
    }

    private Short nextItemOrder() {
	return this.items.stream()
		  	 .map(di -> di.getOrder().intValue())
		  	 .max(Integer::max)
		  	 .map(m -> m + 1)
		  	 .orElse(0)
		  	 .shortValue();
    }

    public boolean isActive() {
	return DesktopSatateEnum.ACTIVE.equals(this.state);
    }

    public void remove() {
	Validate.isTrue(isActive() && !this.fixed && !this.readonly,
		"Sólo se pueden eliminar escritorios activos y que no sean fijos ni de solo lectura");

	setState(DesktopSatateEnum.DELETED);
    }

    public void reorder(Short order) {
	if (isActive()) {
	    setOrder(order);
	}
    }

    public void removeItem(Short order) {
	Validate.isTrue(isActive() && !this.readonly,
		"Sólo se puede eliminar un item si el escritorio está activo y no es de solo lectura");

	this.findItemByOrder(order)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con orden %d para el escritorio.", order))
	    .peek(items::remove);
    }

    public void moveItem(DesktopItem item, Desktop desktopTo) {
	Validate.notNull(item);
	Validate.notNull(desktopTo);
	Validate.isTrue(this.getUserId().equals(desktopTo.getUserId()), "Sólo se pueden mover items entre escritorios del mismo usuario");

	this.items.remove(item);
	desktopTo.addNewItem(item);
    }

    private void addNewItem(DesktopItem item) {
	Validate.isTrue(!this.fixed && !this.readonly, "No se pueden añadir items a un escritorio fijo o de solo lectura");

	item.moveToDesktop(this.getDesktopId());
	item.reorder(nextItemOrder());
	this.items.add(item);
    }

    public void setItemAsFavourite(Short order) {
	Validate.notNull(order);
	
	this.findItemByOrder(order)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con orden %d para el escritorio.", order))
	    .peek(DesktopItem::setAsFavourite);
    }
    
    public void unsetItemAsFavourite(Short order) {
	Validate.notNull(order);
	
	this.findItemByOrder(order)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con orden %d para el escritorio.", order))
	    .peek(DesktopItem::unsetAsFavourite);
    }

    public Optional<DesktopItem> findItem(Short order){
	return this.getItems()
		   .stream()
		   .filter(item -> item.getOrder().equals(order))
		   .findAny();	
    }
    
    private Option<DesktopItem> findItemByOrder(Short order){
	return Option.ofOptional(this.items
		   		     .stream()
		   		     .filter(item -> item.getOrder().equals(order))
		   		     .findAny());
    }

    public void reorderItem(Short itemOrderFrom, Short itemOrderTo) {
	Validate.isTrue(!this.readonly, "No se pueden mover items en escritorios de solo lectura");
	Range<Short> itemsOrderRange = currentItemsOrderRange();
	Validate.isTrue(itemsOrderRange.contains(itemOrderFrom), "El orden origen (%d) debería estar entre %d y %d", itemOrderFrom,
		itemsOrderRange.getMinimum(), itemsOrderRange.getMaximum());
	Validate.isTrue(itemsOrderRange.contains(itemOrderTo), "El orden destino (%d) debería estar entre %d y %d", itemOrderTo,
		itemsOrderRange.getMinimum(), itemsOrderRange.getMaximum());
	
	this.findItemByOrder(itemOrderFrom)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con orden %d para el escritorio.", order))
	    .peek(itemToReorder -> this.reorderItemAnRelocateTheOthers(itemToReorder, itemOrderTo));
    }
    
    private Range<Short> currentItemsOrderRange() {
	Short min = (short) 0;
	Short max = (short) 0;
	for (DesktopItem desktopItem : items) {
	    short itemOrder = desktopItem.getOrder().shortValue();

	    if (itemOrder < min) {
		min = itemOrder;
	    }
	    if (itemOrder > max) {
		max = itemOrder;
	    }
	}
	return Range.between(min, max);
    }

    private void reorderItemAnRelocateTheOthers(DesktopItem itemToReorder, Short itemOrderTo) {
	List<DesktopItem> itemsToRelocate = this.items.stream()
		  				     .filter(item -> !item.equals(itemToReorder))
		  				     .collect(Collectors.toList());	    
	DisplacementMode displacementMode = DisplacementMode.from(itemToReorder.getOrder(), itemOrderTo);
	itemToReorder.reorder(itemOrderTo);
	displacementMode.reorderItemsFromPivot(itemsToRelocate, itemToReorder);
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

    public Set<DesktopItem> getItems() {
	return new HashSet<>(items);
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
