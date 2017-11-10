package net.agata.desktopmodel.infrastructure.desktop.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple7;
import io.vavr.Tuple8;
import io.vavr.control.Option;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.infrastructure.database.InMemoryDatabase;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.user.UserGroupID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.exceptions.ExceptionUtils;
import net.agata.desktopmodel.utils.types.PermissionEnum;
import net.agata.desktopmodel.utils.types.StateEnum;

public class DesktopRepositoryInMemoryImpl implements DesktopRepository {

    @Override
    public DesktopID nextId() {
	return new DesktopID(UUID.randomUUID().toString());
    }

    @Override
    public Collection<Desktop> findAll() {
	return InMemoryDatabase.DESKTOP
			       .values()
			       .stream()
			       .map(t_d -> toDesktop(t_d, InMemoryDatabase.DESKTOP_ITEM
				       			   .values()
				 		     	   .stream()
				 		     	   .filter(t_di -> t_di._1.equals(t_d._1))
			       				   .collect(Collectors.toList())))
			       .collect(Collectors.toList());
    }
    
    private static Desktop toDesktop(Tuple8<DesktopID, String, UserID, Short, Boolean, Boolean, StateEnum, Long> desktopTuple,
	    List<Tuple7<DesktopID, IconID, ColorID, PageID, ApplicationID, Boolean, Short>> desktopItems){
	Desktop desktop = new Desktop(desktopTuple._1, desktopTuple._2, desktopTuple._3, desktopTuple._4, desktopTuple._5, desktopTuple._6,
		desktopTuple._7, desktopItems.stream()
			    		     .map(DesktopRepositoryInMemoryImpl::toDesktopItem)
			    		     .collect(Collectors.toSet()));
	try {
	    FieldUtils.writeDeclaredField(desktop, "version", desktopTuple._8, true);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException("No se puede acceder al campo version", e);
	}
	return desktop;
    }

    private static DesktopItem toDesktopItem(Tuple7<DesktopID, IconID, ColorID, PageID, ApplicationID, Boolean, Short> desktopItem) {
	return new DesktopItem(desktopItem._1, desktopItem._2, desktopItem._3, desktopItem._4, desktopItem._5, desktopItem._6,
		desktopItem._7);
    }

    @Override
    public Collection<Desktop> findByUser(UserID userId) {
	return findAll().stream()
			.filter(d -> d.getUserId().equals(userId))
			.collect(Collectors.toList());
    }

    @Override
    public Desktop findById(DesktopID desktopId) {
	return findAll().stream()
			.filter(d -> d.getDesktopId().equals(desktopId))
			.findAny()
			.orElse(null);
    }

    @Override
    public Desktop save(Desktop desktop) {
	InMemoryDatabase.DESKTOP.put(desktop.getDesktopId(), toDesktopTuple(desktop));
	List<Tuple2<DesktopID, Short>> itemsToDelete = InMemoryDatabase.DESKTOP_ITEM.entrySet()	
	       			     .stream()
	       			     .filter(entry -> entry.getValue()._1.equals(desktop.getDesktopId()))
	       			     .map(Entry::getKey)
	       			     .collect(Collectors.toList());
	       			     
	itemsToDelete.forEach(InMemoryDatabase.DESKTOP_ITEM::remove);
	desktop.getItems()
	       .stream()
	       .forEach(desktopItem -> InMemoryDatabase.DESKTOP_ITEM.put(
		       				new Tuple2<>(desktopItem.getDesktopId(), 
		       				desktopItem.getOrder()), toDesktopItemTuple(desktopItem)));
	return desktop;
    }

    private Tuple7<DesktopID, IconID, ColorID, PageID, ApplicationID, Boolean, Short> toDesktopItemTuple(DesktopItem desktopItem) {
	return new Tuple7<>(desktopItem.getDesktopId(), desktopItem.getIconId(), desktopItem.getColorId(), desktopItem.getPageId(),
		desktopItem.getApplicationId(), desktopItem.getIsFavourite(), desktopItem.getOrder());
    }

    private Tuple8<DesktopID, String, UserID, Short, Boolean, Boolean, StateEnum, Long> toDesktopTuple(Desktop desktop) {
	return new Tuple8<>(desktop.getDesktopId(), desktop.getName(), desktop.getUserId(), desktop.getOrder(), desktop.getFixed(),
		desktop.getReadonly(), desktop.getState(), desktop.getVersion());
    }

    @Override
    public void update(Desktop desktop) {
	try {
	    FieldUtils.writeDeclaredField(desktop, "version", desktop.getVersion() + 1L, true);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException("No se puede acceder al campo version", e);
	}
	save(desktop);
    }

    @Override
    public DesktopItem findDesktopItemByPage(PageID pageId) {
	return InMemoryDatabase.DESKTOP_ITEM
			.values()
			.stream()
			.filter(t_di -> pageId.equals(t_di._4))
			.findAny()
			.map(DesktopRepositoryInMemoryImpl::toDesktopItem)
			.orElse(null);
    }

    @Override
    public List<Desktop> sharedDesktopsByUser(UserID userId) {
	return InMemoryDatabase.USER_GROUP_USER
			       .stream()			       
			       .filter(t_ug -> t_ug._2.equals(userId))
			       .map(Tuple2::_1)
			       .flatMap(ug -> InMemoryDatabase.DESKTOP_USER_GROUP
				       			      .values()
				       			      .stream()
				       			      .filter(t_dug -> t_dug._2.equals(ug)))
			       .flatMap(t_dug -> InMemoryDatabase.DESKTOP
				       			     .values()
				       			     .stream()
				       			     .filter(t_d -> t_d._1.equals(t_dug._1)))
			       .map(t_d -> DesktopRepositoryInMemoryImpl.toDesktop(t_d, InMemoryDatabase.DESKTOP_ITEM
				       			       						    .values()
				       			       						    .stream()
				       			       						    .filter(t_di -> t_di._1.equals(t_d._1))
				       			       						    .collect(Collectors.toList())))	
			       .collect(Collectors.toList());
    }

    @Override
    public void shareDesktop(UserID userId, DesktopID desktopId, UserGroupID userGroupId, PermissionEnum permission) {
	Option.ofOptional(InMemoryDatabase.USER_GROUP_USER
			.stream()
			.filter(t_ugu -> t_ugu._2.equals(userId) && t_ugu._1.equals(userGroupId))
			.findAny())
	      .onEmpty(() -> ExceptionUtils.throwIllegalArgumentException("El USER_GROUP %s no pertenece al USUARIO %s", userGroupId, userId));
	
	InMemoryDatabase.DESKTOP_USER_GROUP.put(new Tuple2<>(desktopId, userGroupId), new Tuple3<>(desktopId, userGroupId, permission));
    }

}
