package net.agata.desktopmodel.domain.desktop.valueobject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import io.vavr.control.Option;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.factory.DesktopFactory;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.user.UserGroupID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.exceptions.ExceptionUtils;
import net.agata.desktopmodel.utils.types.PermissionEnum;
import net.agata.desktopmodel.utils.types.StateEnum;

public class UserDesktops {
    private UserID userId;
    private DesktopRepository desktopRepository;
    private DesktopFactory desktopFactory;

    public UserDesktops(UserID userId, DesktopRepository desktopRepository, DesktopFactory desktopFactory) {
	super();
	setUserId(userId);
	this.desktopRepository = desktopRepository;
	this.desktopFactory = desktopFactory;
    }

    /**
     * BUSINESS LOGIC
     */

    public void changeDesktopOrder(DesktopID desktopId, Short order) {
	Validate.notNull(desktopId);
	Validate.notNull(order);
	
	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(d -> reorderDesktopAndRelocateTheOthers(d, order));
    }

    private void reorderDesktopAndRelocateTheOthers(Desktop desktopToRelocate, Short order) {
	Validate.isTrue(desktopToRelocate.isActive(), "No se puede recolocar un escritorio eliminado");

	DisplacementMode displacementMode = DisplacementMode.from(desktopToRelocate.getOrder(), order);
	desktopToRelocate.reorder(order);
	List<Desktop> desktopsToRelocate = this.userActiveDesktops()
					      .stream()
					      .filter(d -> !d.equals(desktopToRelocate))
					      .collect(Collectors.toList());
	displacementMode.reorderDesktopsFromPivot(desktopsToRelocate, desktopToRelocate);
	desktopsToRelocate.stream()
			  .forEach(desktopRepository::update);
    }

    public void removeDesktop(DesktopID desktopId) {
	Validate.notNull(desktopId);

	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(this::removeDesktop);	
    }
    
    private void removeDesktop(Desktop desktop) {
	desktop.remove();
	desktopRepository.update(desktop);
    }

    private Option<Desktop> findUserDesktopActive(DesktopID desktopId) {
	return Option.ofOptional(this.userActiveDesktops()
		   		     .stream()
		   		     .filter(d -> d.getDesktopId().equals(desktopId))
		   		     .findAny());
    }

    public void moveItem(DesktopID desktopFrom, Short itemToMoveOrder, DesktopID desktopTo) {
	Validate.notNull(desktopFrom);
	Validate.notNull(itemToMoveOrder);
	Validate.notNull(desktopTo);

	Desktop desktopSource = this.findUserDesktopActive(desktopFrom)
		    .getOrElseThrow(() -> new IllegalArgumentException(String.format("El usuario no tiene asignado un escritorio con id: %s", desktopFrom)));
	
	Desktop desktopTarget = this.findUserDesktopActive(desktopTo)
		    .getOrElseThrow(() -> new IllegalArgumentException(String.format("El usuario no tiene asignado un escritorio con id: %s", desktopTo)));

	Option.ofOptional(desktopSource.findItem(itemToMoveOrder))
	      .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No existe un item para el escritorio %s con orden %d", desktopFrom, itemToMoveOrder))
	      .peek(itemToMove -> moveItem(desktopSource, itemToMove, desktopTarget));
    }
    
    private void moveItem(Desktop desktopSource, DesktopItem itemToMove, Desktop desktopTarget) {
	desktopSource.moveItem(itemToMove, desktopTarget);
	desktopRepository.update(desktopSource);
	desktopRepository.update(desktopTarget);
    }
    
    
    private Set<Desktop> userActiveDesktops() {
	return this.desktopRepository.findByUser(this.userId)
				     .stream()
				     .filter(Desktop::isActive)				     
				     .collect(Collectors.toSet());
    }

    public void setItemAsFavourite(DesktopID desktopId, Short order) {
	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(d -> this.setItemAsFavourite(d, order));
    }
    
    private void setItemAsFavourite(Desktop desktop, Short order) {
	this.findFavourite()	    
	    .peek(item -> this.findUserDesktopActive(item.getDesktopId())
		    	      .peek(desktopForCurrentFavourite -> unsetDesktopItemAsFavourite(desktopForCurrentFavourite, item.getOrder())));

	setDesktopItemAsFavourite(desktop, order);
    }

    private void setDesktopItemAsFavourite(Desktop desktop, Short order) {
	desktop.setItemAsFavourite(order);
	desktopRepository.update(desktop);
    }
    
    private void unsetDesktopItemAsFavourite(Desktop desktop, Short order) {
	desktop.unsetItemAsFavourite(order);
	desktopRepository.update(desktop);
    }

    private Option<DesktopItem> findFavourite() {
	return Option.ofOptional(this.userActiveDesktops()
		   		     .stream()
		   		     .flatMap(d -> d.getItems()
		   			     	    .stream()
		   			     	    .filter(DesktopItem::getIsFavourite))
		   		     .findAny());
    }

    public void changeDesktopItemOrder(DesktopID desktopId, Short itemOrderFrom, Short itemOrderTo) {
	Validate.notNull(desktopId);
	Validate.notNull(itemOrderFrom);
	Validate.notNull(itemOrderTo);
	
	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(d -> changeDesktopItemOrder(d, itemOrderFrom, itemOrderTo));
	
    }


    private void changeDesktopItemOrder(Desktop desktop, Short itemOrderFrom, Short itemOrderTo) {
	desktop.reorderItem(itemOrderFrom, itemOrderTo);
	desktopRepository.update(desktop);
    }

    public void removeDesktopItem(DesktopID desktopId, Short itemOrder) {
	Validate.notNull(desktopId);
	Validate.notNull(itemOrder);

	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(d -> removeDesktopItem(d, itemOrder));
    }

    private void removeDesktopItem(Desktop desktop, Short itemOrder) {
	desktop.removeItem(itemOrder);
	desktopRepository.update(desktop);
    }

    public Desktop addNewDesktop(String name, boolean fixed, boolean readonly) {
	Validate.notEmpty(name);
	
	Desktop newDesktop = new Desktop(desktopRepository.nextId(), 
					 name, 
					 this.userId, 
					 this.nextDesktopOrder(), 
					 fixed, 
					 readonly, 
					 StateEnum.ACTIVE, 
					 new HashSet<>());
	desktopRepository.save(newDesktop);
	return newDesktop;
    }
    
    private Short nextDesktopOrder(){
	return this.userActiveDesktops()
		   .stream()
		   .map(Desktop::getOrder)
		   .max(Integer::max)
		   .map(max -> Integer.sum(max, 1))
		   .orElse(0)
		   .shortValue();
    }

    public DesktopItem addPageToDesktop(DesktopID desktopId, IconID itemIcon, ColorID itemColor, PageID itemPageId) {
	return this.findUserDesktopActive(desktopId)
		   .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
		   .map(d -> addPageToDesktop(d, itemIcon, itemColor, itemPageId))
		   .getOrNull();	
    }

    private DesktopItem addPageToDesktop(Desktop desktop, IconID itemIcon, ColorID itemColor, PageID itemPageId) {
	DesktopItem newItem = desktop.addPage(itemIcon, itemColor, itemPageId);
	desktopRepository.update(desktop);
	return newItem;
    }
    
    public DesktopItem addApplicationToDesktop(DesktopID desktopId, IconID itemIcon, ColorID itemColor, ApplicationID itemApplicationId) {
	return this.findUserDesktopActive(desktopId)
		   .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
		   .map(d -> addApplicationToDesktop(d, itemIcon, itemColor, itemApplicationId))
		   .getOrNull();
    }
    
    private DesktopItem addApplicationToDesktop(Desktop desktop, IconID itemIcon, ColorID itemColor, ApplicationID itemApplicationId) {
	DesktopItem newItem = desktop.addApplication(itemIcon, itemColor, itemApplicationId);
	desktopRepository.update(desktop);
	return newItem;
    }

    public Desktop calculateSharedPagesDesktop() {
	return this.desktopFactory.desktopSharedPages(this.userId, nextDesktopOrder());
    }

    public List<Desktop> calculateSharedDesktops() {
	return this.desktopFactory.desktopsShared(this.userId);
    }

    public void shareDesktop(DesktopID desktopId, UserGroupID userGroupId, PermissionEnum permission) {
	Validate.notNull(desktopId);
	Validate.notNull(userGroupId);
	
	this.findUserDesktopActive(desktopId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
	    .peek(d -> desktopRepository.shareDesktop(this.userId, d.getDesktopId(), userGroupId, permission));
    }

    /**
     * ACCESSORS
     */

    public UserID getUserId() {
	return userId;
    }

    private void setUserId(UserID userId) {
	Validate.notNull(userId);
	this.userId = userId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
	UserDesktops other = (UserDesktops) obj;
	if (userId == null) {
	    if (other.userId != null)
		return false;
	} else if (!userId.equals(other.userId))
	    return false;
	return true;
    }

}
