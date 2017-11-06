package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopSatateEnum;
import net.agata.desktopmodel.domain.desktop.valueobject.UserDesktops;
import net.agata.desktopmodel.infrastructure.desktop.repository.DesktopRepositoryInMemoryImpl;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.ui.PageID;
import net.agata.desktopmodel.subdomain.user.UserID;

public class UserDesktopsTest {

    private UserDesktops userDesktops;
    private UserID userId;
    private DesktopRepository desktopRepository;
    
    private final DesktopID desktop1 = new DesktopID("1");
    private DesktopItem desktop1_item0;
    
    private final DesktopID desktop2 = new DesktopID("2");
    private DesktopItem desktop2_item0;
    private DesktopItem desktop2_item1;
    private DesktopItem desktop2_item2;
    private DesktopItem desktop2_item3;
    private DesktopItem desktop2_item4;
    private DesktopItem desktop2_item5;
    
    private final DesktopID desktop3 = new DesktopID("3");
    
    private final DesktopID desktop4 = new DesktopID("4");
    
    private final DesktopID desktop5 = new DesktopID("5");
    private DesktopItem desktop5_item0;
    
    private final DesktopID desktop6 = new DesktopID("6");

    public UserDesktopsTest() {
	super();
	userId = new UserID(1);
	
	desktop1_item0 = new DesktopItem(desktop1, new IconID((short) 1), new ColorID((short) 1), "PAGINA 1", new PageID(1), null, false, (short) 0);
	
	desktop2_item0 = new DesktopItem(desktop2, new IconID((short) 7), new ColorID((short) 1), "ACTIVOS", new PageID(7), null, false, (short) 0);
	desktop2_item1 = new DesktopItem(desktop2, new IconID((short) 6), new ColorID((short) 2), "ALERTAS", new PageID(4), null, false, (short) 1);
	desktop2_item2 = new DesktopItem(desktop2, new IconID((short) 1), new ColorID((short) 5), "INFORMES", new PageID(3), null, false, (short) 2);
	desktop2_item3 = new DesktopItem(desktop2, new IconID((short) 5), new ColorID((short) 6), "CRISIS", new PageID(6), null, false, (short) 3);
	desktop2_item4 = new DesktopItem(desktop2, new IconID((short) 8), new ColorID((short) 2), "PROPIEDADES MANUALES", new PageID(8), null, false, (short) 4);
	desktop2_item5 = new DesktopItem(desktop2, new IconID((short) 3), new ColorID((short) 3), "TORRES SUMINISTRO", new PageID(2), null, true, (short) 5);
	
	desktop5_item0 = new DesktopItem(desktop5, new IconID((short) 2), new ColorID((short) 2), "APP 1", null, new ApplicationID("ID"), false, (short) 0);
	
	desktopRepository = new DesktopRepositoryInMemoryImpl(new HashSet<>(Arrays.asList(
		new Desktop(desktop1, "PANEL DE USUARIO       ", userId, (short) 0, true, true, DesktopSatateEnum.ACTIVE, 
			new HashSet<>(Arrays.asList(
				desktop1_item0
			))),
		new Desktop(desktop2, "FUNCIONALIDADES COMUNES", userId, (short) 1, true, false, DesktopSatateEnum.ACTIVE,
			new HashSet<>(Arrays.asList(
				desktop2_item0, desktop2_item1, desktop2_item2, desktop2_item3, desktop2_item4, desktop2_item5
			))),
		new Desktop(desktop3, "PANEL DE CONFIGURACION ", userId, (short) 2, true, true, DesktopSatateEnum.DELETED, new HashSet<>()),
		new Desktop(desktop4, "PANEL CUSTOMIZADO1     ", userId, (short) 3, false, true, DesktopSatateEnum.ACTIVE, new HashSet<>()),
		new Desktop(desktop5, "PANEL CUSTOMIZADO2     ", userId, (short) 4, false, false, DesktopSatateEnum.ACTIVE, 
			new HashSet<>(Arrays.asList(
				desktop5_item0
			))),
		new Desktop(desktop6, "FUNCIONALIDADES COMUNES", new UserID(2), (short) 1, false, true, DesktopSatateEnum.ACTIVE, new HashSet<>())
	)));
	this.userDesktops = new UserDesktops(userId, desktopRepository);
    }

    @Test
    public void relocateDesktop() {
	List<DesktopID> expectedOrder = Arrays.asList(desktop2, 
						      desktop4,
						      desktop5,
						      desktop1);
	
	userDesktops.changeDesktopOrder(desktop1, (short) 4);
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
	DesktopID desktopId = desktop5;
	userDesktops.removeDesktop(desktopId);
	Assert.assertTrue(desktopRepository.findByUser(this.userId)
					   .stream()
					   .filter(d -> d.getDesktopId().equals(desktopId))
					   .findAny()
					   .map(d -> d.getState().equals(DesktopSatateEnum.DELETED))
					   .orElse(Boolean.FALSE));
    }

    @Test
    public void moveDesktopItem() {	
	DesktopID desktopFrom = desktop1;
	DesktopID desktopTo = desktop5;
	
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
	DesktopID desktopId = desktop1;
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
	DesktopID desktopId = desktop2;
	Short itemOrderFrom = (short) 4;
	Short itemOrderTo = (short) 1;
	
	List<DesktopItem> itemsOrderExpected = Arrays.asList(desktop2_item0,							     
							     desktop2_item4,					     
							     desktop2_item1,
							     desktop2_item2,
							     desktop2_item3,							     							     
							     desktop2_item5);
	
	userDesktops.changeDesktopItemOrder(desktopId, itemOrderFrom, itemOrderTo);
	
	List<DesktopItem> desktopItemsOrdered = desktopRepository.findById(desktopId)
        		       					 .getItems()
        		       					 .stream()
        		       					 .sorted(Comparator.comparing(DesktopItem::getOrder))
        		       					 .collect(Collectors.toList());
	
	int index = 0;
	for (DesktopItem desktopItem : itemsOrderExpected) {
	    Assert.assertTrue(desktopItemsEqualsExceptOrder(desktopItem, desktopItemsOrdered.get(index++)));
	}

    }
    
    private static boolean desktopItemsEqualsExceptOrder(DesktopItem item1, DesktopItem item2){
	return Objects.equals(item1.getDesktopId(), item2.getDesktopId())
		&& Objects.equals(item1.getIconId(), item2.getIconId())
		&& Objects.equals(item1.getColorId(), item2.getColorId())
		&& Objects.equals(item1.getName(), item2.getName())
		&& Objects.equals(item1.getPageId(), item2.getPageId())
		&& Objects.equals(item1.getApplicationId(), item2.getApplicationId())
		&& Objects.equals(item1.getIsFavourite(), item2.getIsFavourite());
    }

    @Test
    public void removeDesktopItem() {
	DesktopID desktopId = desktop2;
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
		&& Objects.equals(item1.getName(), item2.getName())
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
	Assert.assertEquals(desktopCreated.get().getState(), DesktopSatateEnum.ACTIVE);
    }

    @Test
    public void addPageToDesktop() {

	DesktopID desktopId = desktop5;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	String itemName = "PAGINA PRUEBA MGDS";
	PageID itemPageId = new PageID(77);

	DesktopItem newItem = userDesktops.addPageToDesktop(desktopId, itemIcon, itemColor, itemName, itemPageId);
	
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
	Assert.assertEquals(desktopItemCreated.get().getName(), itemName);
	Assert.assertEquals(desktopItemCreated.get().getPageId(), itemPageId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }
    
    @Test
    public void addApplicationToDesktop() {

	DesktopID desktopId = desktop5;
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	String itemName = "APLICACION PRUEBA MGDS";
	ApplicationID itemApplicationId = new ApplicationID("898");

	DesktopItem newItem = userDesktops.addApplicationToDesktop(desktopId, itemIcon, itemColor, itemName, itemApplicationId);
	
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
	Assert.assertEquals(desktopItemCreated.get().getName(), itemName);
	Assert.assertEquals(desktopItemCreated.get().getApplicationId(), itemApplicationId);
	Assert.assertEquals(desktopItemCreated.get().getIsFavourite(), false);
    }


}
