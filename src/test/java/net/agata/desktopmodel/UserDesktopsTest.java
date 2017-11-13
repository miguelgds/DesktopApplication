package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import io.vavr.Tuple2;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.factory.DesktopFactory;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktopItem;
import net.agata.desktopmodel.domain.desktop.valueobject.UserDesktops;
import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.infrastructure.database.InMemoryDatabase;
import net.agata.desktopmodel.infrastructure.desktop.repository.DesktopRepositoryInMemoryImpl;
import net.agata.desktopmodel.infrastructure.page.repository.PageRepositoryInMemoryImpl;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.user.UserGroupID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.types.PermissionEnum;
import net.agata.desktopmodel.utils.types.StateEnum;

public class UserDesktopsTest {

    private UserDesktops userDesktops;
    private UserID userId;
    private DesktopRepository desktopRepository;
    private PageRepository pageRepository;
    private DesktopFactory desktopFactory;

    public UserDesktopsTest() {
	super();
	InMemoryDatabase.initData();
	userId = new UserID(4);
	this.pageRepository = new PageRepositoryInMemoryImpl();
	this.desktopRepository = new DesktopRepositoryInMemoryImpl();

	this.desktopFactory = new DesktopFactory(this.pageRepository, desktopRepository);
	this.userDesktops = new UserDesktops(this.userId, this.desktopRepository, this.desktopFactory);
    }

    @Test
    public void relocateDesktop() {
	List<DesktopID> expectedOrder = Arrays.asList(InMemoryDatabase.DESKTOP_ID_2, 
						      InMemoryDatabase.DESKTOP_ID_4,
						      InMemoryDatabase.DESKTOP_ID_5,
						      InMemoryDatabase.DESKTOP_ID_1);
	
	userDesktops.changeDesktopOrder(InMemoryDatabase.DESKTOP_ID_1, (short) 4);
	List<DesktopID> desktopsOrdered = desktopRepository.findByUser(this.userId)
			 				   .stream()
			 				   .filter(Desktop::isActive)
			 				   .sorted(Comparator.comparing(Desktop::getOrder))
			 				   .map(Desktop::getDesktopId)			 
			 				   .collect(Collectors.toList());
	int index = 0;
	for (DesktopID desktopID : expectedOrder) {
	    Assert.assertEquals(desktopID, desktopsOrdered.get(index++));
	}
    }

    @Test
    public void removeDesktop() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_5;
	userDesktops.removeDesktop(desktopId);
	Assert.assertTrue(desktopRepository.findByUser(this.userId)
					   .stream()
					   .filter(d -> d.getDesktopId().equals(desktopId))
					   .findAny()
					   .map(d -> d.getState().equals(StateEnum.DELETED))
					   .orElse(Boolean.FALSE));
    }

    @Test
    public void moveDesktopItem() {	
	DesktopID desktopFrom = InMemoryDatabase.DESKTOP_ID_1;
	DesktopID desktopTo = InMemoryDatabase.DESKTOP_ID_5;
	
	userDesktops.moveItem(desktopFrom, (short) 0, desktopTo);

	Assert.assertTrue(desktopRepository.findByUser(this.userId)
					   .stream()
					   .filter(d -> d.getDesktopId().equals(desktopFrom))
					   .findAny()
					   .map(d -> d.getItems().size())
					   .orElse(0) == 0);
	Assert.assertTrue(desktopRepository.findByUser(this.userId)
		   			   .stream()
		   			   .filter(d -> d.getDesktopId().equals(desktopTo))
		   			   .findAny()
					   .map(d -> d.getItems().size())
					   .orElse(0) == 2);
    }

    @Test
    public void changeItemFavourite() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_1;
	Short order = (short) 0;
	userDesktops.setItemAsFavourite(desktopId, order);

	desktopRepository.findByUser(this.userId)
	 		 .stream()
	 		 .flatMap(d -> d.getItems()
	 			        .stream()
	 			        .filter(DesktopItem::getIsFavourite))
	 		 .forEach(item -> Assert.assertTrue(item.getDesktopId().equals(desktopId)
	 			 				&& item.getOrder().equals(order)));
    }

    @Test
    public void relocateDesktopItem() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_2;
	Short itemOrderFrom = (short) 4;
	Short itemOrderTo = (short) 1;
	
	Optional<DesktopItem> desktopItemBefore = desktopRepository.findById(desktopId)
			 					 .getItems()
			 					 .stream()
			 					 .sorted(Comparator.comparing(DesktopItem::getOrder))
			 					 .skip(itemOrderFrom)
			 					 .findFirst();
	
	userDesktops.changeDesktopItemOrder(desktopId, itemOrderFrom, itemOrderTo);
	
	Optional<DesktopItem> desktopItemAfter = desktopRepository.findById(desktopId)
        		       					 .getItems()
        		       					 .stream()
        		       					 .sorted(Comparator.comparing(DesktopItem::getOrder))
        		       					 .skip(itemOrderTo)
			 					 .findFirst();
	
	Assert.assertTrue(desktopItemBefore.isPresent());
	Assert.assertTrue(desktopItemAfter.isPresent());
	Assert.assertTrue(desktopItemsEqualsExceptOrder(desktopItemBefore.get(), desktopItemAfter.get()));

    }

    private boolean desktopItemsEqualsExceptOrder(DesktopItem item1, DesktopItem item2){
	return Objects.equals(item1.getDesktopId(), item2.getDesktopId())
		&& Objects.equals(item1.getIconId(), item2.getIconId())
		&& Objects.equals(item1.getColorId(), item2.getColorId())
		&& Objects.equals(item1.getPageId(), item2.getPageId())
		&& Objects.equals(item1.getApplicationId(), item2.getApplicationId())
		&& Objects.equals(item1.getIsFavourite(), item2.getIsFavourite());
    }

    @Test
    public void removeDesktopItem() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_2;
	Short itemOrder = (short) 5;

	Optional<DesktopItem> desktopToRemove = desktopRepository.findById(desktopId)
			 					 .getItems()
			 					 .stream()
			 					 .filter(item -> item.getOrder().equals(itemOrder))
			 					 .findAny();
	Assert.assertTrue(desktopToRemove.isPresent());
	
	userDesktops.removeDesktopItem(desktopId, itemOrder);
	
	Assert.assertTrue(desktopRepository.findByUser(this.userId)
	 		 		   .stream()
	 		 		   .filter(d -> d.getDesktopId().equals(desktopId))
	 		 		   .flatMap(d -> d.getItems().stream())
	 		 		   .noneMatch(item -> this.desktopItemsEquals(item, desktopToRemove.get())));
    }
    
    private boolean desktopItemsEquals(DesktopItem item1, DesktopItem item2){
	return Objects.equals(item1.getDesktopId(), item2.getDesktopId())
		&& Objects.equals(item1.getIconId(), item2.getIconId())
		&& Objects.equals(item1.getColorId(), item2.getColorId())
		&& Objects.equals(item1.getPageId(), item2.getPageId())
		&& Objects.equals(item1.getApplicationId(), item2.getApplicationId())
		&& Objects.equals(item1.getIsFavourite(), item2.getIsFavourite())
		&& Objects.equals(item1.getOrder(), item2.getOrder());
    }

    @Test
    public void createDesktop() {
	String desktopName = "DESKTOP PRUEBA MGDS";
	Boolean desktopFixed = false;
	Boolean desktopReadonly = false;

	Desktop newDesktop = userDesktops.addNewDesktop(desktopName, desktopFixed, desktopReadonly);
	
	Optional<Desktop> desktopCreated = desktopRepository.findByUser(this.userId)
		   					    .stream()
		   					    .filter(d -> d.equals(newDesktop))
		   					    .findAny();
	Assert.assertTrue(desktopCreated.isPresent());
	Assert.assertEquals(desktopCreated.get().getName(), desktopName);
	Assert.assertEquals(desktopCreated.get().getUserId(), this.userId);
	Assert.assertNotNull(desktopCreated.get().getOrder());
	Assert.assertEquals(desktopCreated.get().getFixed(), desktopFixed);
	Assert.assertEquals(desktopCreated.get().getReadonly(), desktopReadonly);
	Assert.assertEquals(desktopCreated.get().getState(), StateEnum.ACTIVE);
    }

    @Test
    public void addPageToDesktop() {

	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_5;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	PageID itemPageId = new PageID(77);

	DesktopItem newItem = userDesktops.addPageToDesktop(desktopId, itemIcon, itemColor, itemPageId);
	
	Optional<DesktopItem> desktopItemCreated = desktopRepository.findByUser(this.userId)
	 		 		   .stream()
	 		 		   .filter(d -> d.getDesktopId().equals(desktopId))
	 		 		   .findAny()
	 		 		   .flatMap(d -> d.getItems()
	 		 			      	  .stream()
	 		 			      	  .filter(item -> item.equals(newItem))
	 		 			      	  .findAny());

	Assert.assertTrue(desktopItemCreated.isPresent());
	Assert.assertEquals(desktopItemCreated.get().getIconId(), itemIcon);
	Assert.assertEquals(desktopItemCreated.get().getColorId(), itemColor);
	Assert.assertEquals(desktopItemCreated.get().getPageId(), itemPageId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }
    
    @Test
    public void addApplicationToDesktop() {

	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_5;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	ApplicationID itemApplicationId = new ApplicationID("898");

	DesktopItem newItem = userDesktops.addApplicationToDesktop(desktopId, itemIcon, itemColor, itemApplicationId);
	
	Optional<DesktopItem> desktopItemCreated = desktopRepository.findByUser(this.userId)
	 		 		   .stream()
	 		 		   .filter(d -> d.getDesktopId().equals(desktopId))
	 		 		   .findAny()
	 		 		   .flatMap(d -> d.getItems()
	 		 			      	  .stream()
	 		 			      	  .filter(item -> item.equals(newItem))
	 		 			      	  .findAny());

	Assert.assertTrue(desktopItemCreated.isPresent());
	Assert.assertEquals(desktopItemCreated.get().getIconId(), itemIcon);
	Assert.assertEquals(desktopItemCreated.get().getColorId(), itemColor);
	Assert.assertEquals(desktopItemCreated.get().getApplicationId(), itemApplicationId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }

    @Test
    public void calculateSharedItemsDesktop() {
	List<SharedDesktopItem> sharedItems = userDesktops.calculateSharedPages();

	Assert.assertNotNull(sharedItems);
	Assert.assertTrue(sharedItems.size() == 2);
    }

    @Test
    public void calculateSharedDesktops() {
	// TODO DEVOLVER OBJETOS CUSTOM EN LUGAR DE UN DESKTOPS
	List<Desktop> sharedDesktops = userDesktops.calculateSharedDesktops();

	Assert.assertNotNull(sharedDesktops);
	Assert.assertTrue(sharedDesktops.size() == 1);
    }

    @Test
    public void shareDesktop() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_2;
	UserGroupID userGroupId = new UserGroupID(8);
	PermissionEnum permission = PermissionEnum.READ_WRITE;
	userDesktops.shareDesktop(desktopId, userGroupId, permission);

	Assert.assertTrue(InMemoryDatabase.DESKTOP_USER_GROUP
					  .containsKey(new Tuple2<>(desktopId, userGroupId)));
    }

}
