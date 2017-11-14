package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import io.vavr.Tuple2;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.service.SharedDesktopsAndItemsService;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopItemID;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktop;
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
    private SharedDesktopsAndItemsService sharedDesktopsAndItemsService;

    public UserDesktopsTest() {
	super();
	InMemoryDatabase.initData();
	userId = new UserID(4);
	this.pageRepository = new PageRepositoryInMemoryImpl();
	this.desktopRepository = new DesktopRepositoryInMemoryImpl();

	this.sharedDesktopsAndItemsService = new SharedDesktopsAndItemsService(this.pageRepository, desktopRepository);
	this.userDesktops = new UserDesktops(this.userId, this.desktopRepository, this.sharedDesktopsAndItemsService);
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
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_1_0;
	DesktopID desktopTo = InMemoryDatabase.DESKTOP_ID_5;
	
	userDesktops.moveItem(desktopItemId, desktopTo);

	Assert.assertTrue(desktopRepository.findByUser(this.userId)
					   .stream()
					   .flatMap(d -> d.getItems().stream())
					   .filter(di -> di.getDesktopItemId().equals(desktopItemId))
					   .allMatch(di -> di.getDesktopId().equals(desktopTo)));
    }

    @Test
    public void changeItemFavourite() {
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_1_0;
	userDesktops.setItemAsFavourite(desktopItemId);

	Assert.assertTrue(desktopRepository.findByUser(this.userId)
	 		 .stream()
	 		 .flatMap(d -> d.getItems().stream())
	 		 .filter(DesktopItem::getIsFavourite)
	 		 .allMatch(di -> di.getDesktopItemId().equals(desktopItemId)));
    }

    @Test
    public void unsetItemFavourite() {
	userDesktops.unsetFavouriteItem();

	Assert.assertTrue(desktopRepository.findByUser(this.userId)
	 		 		  .stream()
	 		 		  .flatMap(d -> d.getItems().stream())
	 		 		  .noneMatch(DesktopItem::getIsFavourite));
    }

    @Test
    public void relocateDesktopItem() {
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_2_4;
	Short itemOrderTo = (short) 1;
	
	userDesktops.changeDesktopItemOrder(desktopItemId, itemOrderTo);
	
	Assert.assertTrue(desktopRepository.findByUser(userId)
        		       		   .stream()
        		       		   .flatMap(d -> d.getItems()
        		       			   	  .stream())
        		       		   .filter(di -> di.getDesktopItemId().equals(desktopItemId))
        		       		   .allMatch(di -> di.getOrder().equals(itemOrderTo)));
    }

    @Test
    public void removeDesktopItem() {
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_2_5;
	
	userDesktops.removeDesktopItem(desktopItemId);
	
	Assert.assertTrue(desktopRepository.findByUser(userId)
    		   			   .stream()
    		   			   .flatMap(d -> d.getItems()
    		   				   	  .stream())
    		   			   .noneMatch(di -> di.getDesktopItemId().equals(desktopItemId)));
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

	DesktopItem newItem = userDesktops.appendPageToDesktop(desktopId, itemIcon, itemColor, itemPageId);
	
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
	List<SharedDesktopItem> sharedItems = userDesktops.sharedPages();

	Assert.assertNotNull(sharedItems);
	Assert.assertTrue(sharedItems.size() == 2);
    }

    @Test
    public void calculateSharedDesktops() {
	List<SharedDesktop> sharedDesktops = userDesktops.sharedDesktops();

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

    @Test
    public void removeSharedDesktopItem() {
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_6_0;
	
	userDesktops.removeDesktopItem(desktopItemId);
	
	Assert.assertTrue(desktopRepository.findByUser(userId)
    		   			   .stream()
    		   			   .flatMap(d -> d.getItems()
    		   				   	  .stream())
    		   			   .noneMatch(di -> di.getDesktopItemId().equals(desktopItemId)));
    }
    
    @Test
    public void removeSharedDesktop() {
	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_6;
	userDesktops.removeDesktop(desktopId);
	Assert.assertTrue(desktopRepository.findById(desktopId).getState().equals(StateEnum.DELETED));
    }
    
    @Test
    public void addPageToSharedDesktop() {

	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_6;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	PageID itemPageId = new PageID(77);

	DesktopItem newItem = userDesktops.appendPageToDesktop(desktopId, itemIcon, itemColor, itemPageId);
	
	Optional<DesktopItem> desktopItemCreated = desktopRepository.findById(newItem.getDesktopId())
					   			    .getItems()
					   			    .stream()
					   			    .max(Comparator.comparing(DesktopItem::getOrder));

	Assert.assertTrue(desktopItemCreated.isPresent());
	Assert.assertEquals(desktopItemCreated.get().getIconId(), itemIcon);
	Assert.assertEquals(desktopItemCreated.get().getColorId(), itemColor);
	Assert.assertEquals(desktopItemCreated.get().getPageId(), itemPageId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }
    
    @Test
    public void addApplicationToSharedDesktop() {

	DesktopID desktopId = InMemoryDatabase.DESKTOP_ID_6;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	ApplicationID itemApplicationId = new ApplicationID("898");

	DesktopItem newItem = userDesktops.addApplicationToDesktop(desktopId, itemIcon, itemColor, itemApplicationId);
	
	Optional<DesktopItem> desktopItemCreated = desktopRepository.findById(newItem.getDesktopId())
	 		 		   			    .getItems()
	 		 		   			    .stream()
	 		 		   			    .max(Comparator.comparing(DesktopItem::getOrder));
	 		 			      	  
	Assert.assertTrue(desktopItemCreated.isPresent());
	Assert.assertEquals(desktopItemCreated.get().getIconId(), itemIcon);
	Assert.assertEquals(desktopItemCreated.get().getColorId(), itemColor);
	Assert.assertEquals(desktopItemCreated.get().getApplicationId(), itemApplicationId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }
    
    @Test
    public void relocateSharedDesktopItem() {
	DesktopItemID desktopItemId = InMemoryDatabase.DESKTOP_ITEM_ID_6_0;
	Short itemOrderTo = (short) 1;
	
	userDesktops.changeDesktopItemOrder(desktopItemId, itemOrderTo);
	
	Assert.assertTrue(desktopRepository.findByUser(userId)
        		       		   .stream()
        		       		   .flatMap(d -> d.getItems()
        		       			   	  .stream())
        		       		   .filter(di -> di.getDesktopItemId().equals(desktopItemId))
        		       		   .allMatch(di -> di.getOrder().equals(itemOrderTo)));	

    }

}
