package net.agata.desktopmodel.domain.desktop.valueobject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import io.vavr.control.Option;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.service.SharedDesktopsAndItemsService;
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
    private SharedDesktopsAndItemsService sharedDesktopsAndItemsService;

    public UserDesktops(UserID userId, DesktopRepository desktopRepository, SharedDesktopsAndItemsService desktopFactory) {
	super();
	setUserId(userId);
	this.desktopRepository = desktopRepository;
	this.sharedDesktopsAndItemsService = desktopFactory;
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
	    .orElse(() -> this.findReadWriteSharedDesktop(desktopId))
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

    private Option<Desktop> findUserDesktopActive(DesktopItemID desktopItemId) {
	return Option.ofOptional(this.userActiveDesktops()
		     		     .stream()
		     		     .filter(d -> d.getItems()
		     			     	   .stream()
		     			     	   .anyMatch(di -> di.getDesktopItemId().equals(desktopItemId)))
		     		     .findAny());	
    }
    
    public void moveItem(DesktopItemID desktopItemId, DesktopID desktopTo) {
	Validate.notNull(desktopItemId);
	Validate.notNull(desktopTo);

	Desktop desktopSource = this.findUserDesktopActive(desktopItemId)
		    .getOrElseThrow(() -> new IllegalArgumentException(String.format("El usuario no tiene asignado un item con id: %s", desktopItemId)));
	
	Desktop desktopTarget = this.findUserDesktopActive(desktopTo)
		    .getOrElseThrow(() -> new IllegalArgumentException(String.format("El usuario no tiene asignado un escritorio con id: %s", desktopTo)));

	desktopSource.findItem(desktopItemId)
	      	     .ifPresent(itemToMove -> moveItem(desktopSource, itemToMove, desktopTarget));
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

    public void setItemAsFavourite(DesktopItemID desktopItemId) {
	this.findUserDesktopActive(desktopItemId)
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con id %s asociado al usuario.", desktopItemId))
	    .peek(d -> this.setItemAsFavourite(d, desktopItemId));
    }
    
    private void setItemAsFavourite(Desktop desktop, DesktopItemID desktopItemId) {
	unsetFavouriteItem();
	setDesktopItemAsFavourite(desktop, desktopItemId);
    }
    
    public void unsetFavouriteItem(){
	this.findFavourite()	    
	    .peek(item -> this.findUserDesktopActive(item.getDesktopId())
		    	      .peek(desktopForCurrentFavourite -> unsetDesktopItemAsFavourite(desktopForCurrentFavourite, item.getDesktopItemId())));
    }

    private void setDesktopItemAsFavourite(Desktop desktop, DesktopItemID desktopItemId) {
	desktop.setItemAsFavourite(desktopItemId);
	desktopRepository.update(desktop);
    }
    
    private void unsetDesktopItemAsFavourite(Desktop desktop, DesktopItemID desktopItemId) {
	desktop.unsetItemAsFavourite(desktopItemId);
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

    public void changeDesktopItemOrder(DesktopItemID desktopItemId, Short itemOrderTo) {
	Validate.notNull(desktopItemId);
	Validate.notNull(itemOrderTo);
	
	this.findUserDesktopActive(desktopItemId)
	    .orElse(() -> this.findReadWriteSharedDesktop(desktopItemId))
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con id %s en ninguno de los escritorios propios y compartidos con permisos RW.", desktopItemId))
	    .peek(d -> changeDesktopItemOrder(d, desktopItemId, itemOrderTo));
	
    }

    private void changeDesktopItemOrder(Desktop desktop, DesktopItemID desktopItemId, Short itemOrderTo) {
	desktop.reorderItem(desktopItemId, itemOrderTo);
	desktopRepository.update(desktop);
    }

    public void removeDesktopItem(DesktopItemID desktopItemId) {
	Validate.notNull(desktopItemId);

	this.findUserDesktopActive(desktopItemId)
	    .orElse(() -> this.findReadWriteSharedDesktop(desktopItemId))
	    .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un item con id %s en ninguno de los escritorios propios y compartidos con permisos RW.", desktopItemId))
	    .peek(d -> removeDesktopItem(d, desktopItemId));
    }

    private Option<Desktop> findReadWriteSharedDesktop(DesktopID desktopId) {
	return Option.ofOptional(this.userSharedDesktops(PermissionEnum.READ_WRITE)
				     .stream()
				     .filter(d -> d.getDesktopId().equals(desktopId))
				     .findAny());
    }
    
    private Option<Desktop> findReadWriteSharedDesktop(DesktopItemID desktopItemId) {
	return Option.ofOptional(this.userSharedDesktops(PermissionEnum.READ_WRITE)
				     .stream()
				     .filter(d -> d.getItems()
					     	   .stream()
					     	   .anyMatch(di -> di.getDesktopItemId().equals(desktopItemId)))
				     .findAny());
    }

    private Set<Desktop> userSharedDesktops(PermissionEnum permission) {
	return this.desktopRepository.findSharedsByUser(this.userId)
				     .getOrDefault(permission, Collections.emptyList())
				     .stream()
				     .filter(Desktop::isActive)
				     .collect(Collectors.toSet());
    }

    private void removeDesktopItem(Desktop desktop, DesktopItemID desktopItemId) {
	desktop.removeItem(desktopItemId);
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
		   .map(d -> d.getOrder().intValue())
		   .reduce(Integer::max)
		   .map(max -> max + 1)
		   .orElse(0)
		   .shortValue();
    }

    public DesktopItem appendPageToDesktop(DesktopID desktopId, IconID itemIcon, ColorID itemColor, PageID itemPageId) {
	return this.findUserDesktopActive(desktopId)
		   .orElse(() -> this.findReadWriteSharedDesktop(desktopId))
		   .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
		   .map(d -> appendPageToDesktop(d, itemIcon, itemColor, itemPageId))
		   .getOrNull();	
    }

    private DesktopItem appendPageToDesktop(Desktop desktop, IconID itemIcon, ColorID itemColor, PageID itemPageId) {
	DesktopItem newItem = desktop.appendPage(itemIcon, itemColor, itemPageId);
	desktopRepository.update(desktop);
	return newItem;
    }
    
    public DesktopItem addApplicationToDesktop(DesktopID desktopId, IconID itemIcon, ColorID itemColor, ApplicationID itemApplicationId) {
	return this.findUserDesktopActive(desktopId)
		   .orElse(() -> this.findReadWriteSharedDesktop(desktopId)) 
		   .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("No hay un escritorio activo con id %s asociado al usuario.", desktopId))
		   .map(d -> addApplicationToDesktop(d, itemIcon, itemColor, itemApplicationId))
		   .getOrNull();
    }
    
    private DesktopItem addApplicationToDesktop(Desktop desktop, IconID itemIcon, ColorID itemColor, ApplicationID itemApplicationId) {
	DesktopItem newItem = desktop.appendApplication(itemIcon, itemColor, itemApplicationId);
	desktopRepository.update(desktop);
	return newItem;
    }

    public List<SharedDesktopItem> sharedPages() {
	return this.sharedDesktopsAndItemsService.sharedPagesToUser(this.userId);
    }

    public List<SharedDesktop> sharedDesktops() {
	return this.sharedDesktopsAndItemsService.sharedDesktopsToUser(this.userId);
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

    private void setUserId(UserID userId) {
	Validate.notNull(userId);
	this.userId = userId;
    }

}
