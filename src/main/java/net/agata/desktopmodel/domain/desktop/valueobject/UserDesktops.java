package net.agata.desktopmodel.domain.desktop.valueobject;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.subdomain.user.UserID;

public class UserDesktops {
    private UserID userId;
    private DesktopRepository desktopRepository;

    public UserDesktops(UserID userId, DesktopRepository desktopRepository) {
	super();
	setUserId(userId);
	this.desktopRepository = desktopRepository;
    }

    /**
     * BUSINESS LOGIC
     */

    public void changeDesktopOrder(DesktopID desktopId, Short order) {
	Validate.notNull(desktopId);
	Validate.notNull(order);
	
	this.findUserDesktopActive(desktopId)
	    .ifPresent(d -> reorderDesktopAndRelocateTheOthers(d, order));
    }

    private void reorderDesktopAndRelocateTheOthers(Desktop desktopToRelocate, Short order) {
	Validate.isTrue(desktopToRelocate.isActive(), "No se puede recolocar un escritorio eliminado");

	desktopToRelocate.reorder(order);
	List<Desktop> desktopsToRelocate = this.userActiveDesktops()
					      .stream()
					      .filter(d -> !d.equals(desktopToRelocate))
					      .collect(Collectors.toList());
	for (Desktop desktop : desktopsToRelocate) {
	    if (desktop.getOrder().shortValue() >= order) {
		desktop.reorder((short) (desktop.getOrder() + 1));
	    }
	}
	desktopsToRelocate.add(desktopToRelocate);
	zipDesktopsOrder(desktopsToRelocate);
    }
    
    private void zipDesktopsOrder(List<Desktop> desktopsToRelocate) {
	desktopsToRelocate.sort(Comparator.comparing(Desktop::getOrder));
	int index = 0;
	for (Desktop desktop : desktopsToRelocate) {
	    desktop.reorder((short) index++);
	    desktopRepository.update(desktop);
	}
    }

    public void removeDesktop(DesktopID desktopId) {
	Validate.notNull(desktopId);

	this.findUserDesktopActive(desktopId)
	    .ifPresent(this::removeDesktop);	
    }
    
    private void removeDesktop(Desktop desktop) {
	desktop.remove();
	desktopRepository.update(desktop);
    }

    private Optional<Desktop> findUserDesktopActive(DesktopID desktopId) {
	return this.userActiveDesktops()
		   .stream()
		   .filter(d -> d.getDesktopId().equals(desktopId))
		   .findAny();
    }

    public void moveItem(DesktopID desktopFrom, Short itemToMoveIndex, DesktopID desktopTo) {
	// TODO
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

    public Set<Desktop> userActiveDesktops() {
	return this.desktopRepository.findByUser(this.userId)
				     .stream()
				     .filter(Desktop::isActive)				     
				     .collect(Collectors.toSet());
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
