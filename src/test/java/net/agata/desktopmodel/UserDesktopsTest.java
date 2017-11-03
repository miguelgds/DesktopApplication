package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

    public UserDesktopsTest() {
	super();
	userId = new UserID(1);
	desktopRepository = new DesktopRepositoryInMemoryImpl(new HashSet<>(Arrays.asList(
		new Desktop(new DesktopID("1"), "PANEL DE USUARIO       ", userId, (short) 0, true, true, DesktopSatateEnum.ACTIVE, 
			new HashSet<>(Arrays.asList(
				new DesktopItem(new DesktopID("1"), new IconID((short) 1), new ColorID((short) 1), "PAGINA 1", new PageID(1), null, false, (short) 0)
			))),
		new Desktop(new DesktopID("2"), "FUNCIONALIDADES COMUNES", userId, (short) 1, true, false, DesktopSatateEnum.ACTIVE,
			new HashSet<>(Arrays.asList(
				new DesktopItem(new DesktopID("2"), new IconID((short) 7), new ColorID((short) 1), "ACTIVOS", new PageID(7), null, false, (short) 0),
				new DesktopItem(new DesktopID("2"), new IconID((short) 6), new ColorID((short) 2), "ALERTAS", new PageID(4), null, false, (short) 1),
				new DesktopItem(new DesktopID("2"), new IconID((short) 1), new ColorID((short) 5), "INFORMES", new PageID(3), null, false, (short) 2),
				new DesktopItem(new DesktopID("2"), new IconID((short) 5), new ColorID((short) 6), "CRISIS", new PageID(6), null, false, (short) 3),
				new DesktopItem(new DesktopID("2"), new IconID((short) 8), new ColorID((short) 2), "PROPIEDADES MANUALES", new PageID(8), null, false, (short) 4),
				new DesktopItem(new DesktopID("2"), new IconID((short) 3), new ColorID((short) 3), "TORRES SUMINISTRO", new PageID(2), null, true, (short) 5)				
			))),
		new Desktop(new DesktopID("3"), "PANEL DE CONFIGURACION ", userId, (short) 2, true, true, DesktopSatateEnum.DELETED, new HashSet<>()),
		new Desktop(new DesktopID("4"), "PANEL CUSTOMIZADO1     ", userId, (short) 3, false, true, DesktopSatateEnum.ACTIVE, new HashSet<>()),
		new Desktop(new DesktopID("5"), "PANEL CUSTOMIZADO2     ", userId, (short) 4, false, false, DesktopSatateEnum.ACTIVE, 
			new HashSet<>(Arrays.asList(
				new DesktopItem(new DesktopID("5"), new IconID((short) 2), new ColorID((short) 2), "APP 1", null, new ApplicationID("ID"), false, (short) 0)
			))),
		new Desktop(new DesktopID("6"), "FUNCIONALIDADES COMUNES", new UserID(2), (short) 1, false, true, DesktopSatateEnum.ACTIVE, new HashSet<>())
	)));
	this.userDesktops = new UserDesktops(userId, desktopRepository);
    }

    @Test
    public void relocateDesktop() {
	List<DesktopID> expectedOrder = Arrays.asList(						       						      
						      new DesktopID("2"), 
						      new DesktopID("4"),
						      new DesktopID("1"),
						      new DesktopID("5")
						      );
	
	userDesktops.changeDesktopOrder(new DesktopID("1"), (short) 4);
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
	DesktopID desktopId = new DesktopID("5");
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
	DesktopID desktopFrom = new DesktopID("1");
	DesktopID desktopTo = new DesktopID("5");
	
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
	DesktopID desktopId = new DesktopID("1");
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
	DesktopID desktopId = new DesktopID("2");
	Short itemOrderFrom = (short) 5;
	Short itemOrderTo = (short) 0;

	userDesktops.changeDesktopItemOrder(desktopId, itemOrderFrom, itemOrderTo);
	
	// TODO HACER UNA BUENA VALIDACION
	desktopRepository.findByUser(this.userId)
	 		 .stream()
	 		 .filter(d -> d.getDesktopId().equals(desktopId))
	 		 .flatMap(d -> d.getItems().stream())
	 		 .forEach(System.out::println);
    }

    @Test
    public void removeDesktopItem() {
	DesktopID desktopId = new DesktopID("2");
	Short itemOrder = (short) 5;

	userDesktops.removeDesktopItem(desktopId, itemOrder);
	
	// TODO HACER UNA BUENA VALIDACION
	desktopRepository.findByUser(this.userId)
	 		 .stream()
	 		 .filter(d -> d.getDesktopId().equals(desktopId))
	 		 .flatMap(d -> d.getItems().stream())
	 		 .forEach(System.out::println);
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

	DesktopID desktopId = new DesktopID("5");
	IconID itemIcon = new IconID((short) 17);
	ColorID itemColor = new ColorID((short) 7);
	String itemName = "PAGINA PRUEBA MGDS";
	PageID itemPageId = new PageID(77);

	DesktopItem newItem = userDesktops.addPageToDesktop(desktopId, itemIcon, itemColor, itemName, itemPageId);
	
	Assert.assertTrue(desktopRepository.findByUser(this.userId)
	 		 		   .stream()
	 		 		   .filter(d -> d.getDesktopId().equals(desktopId))
	 		 		   .findAny()
	 		 		   .map(d -> d.getItems()
	 		 			      .stream()
	 		 			      .anyMatch(item -> item.equals(newItem)))
	 		 		   .orElse(false));

    }


}
