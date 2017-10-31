package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopSatateEnum;
import net.agata.desktopmodel.domain.desktop.valueobject.UserDesktops;
import net.agata.desktopmodel.infrastructure.desktop.repository.DesktopRepositoryInMemoryImpl;
import net.agata.desktopmodel.subdomain.user.UserID;

public class UserDesktopsTest {

    private UserDesktops userDesktops;
    private UserID userId;
    private DesktopRepository desktopRepository;

    public UserDesktopsTest() {
	super();
	userId = new UserID(1);
	desktopRepository = new DesktopRepositoryInMemoryImpl(new HashSet<>(Arrays.asList(
		new Desktop(new DesktopID("1"), "PANEL DE USUARIO       ", userId, (short) 0, true, true, DesktopSatateEnum.ACTIVE, new HashSet<>()),
		new Desktop(new DesktopID("2"), "FUNCIONALIDADES COMUNES", userId, (short) 1, true, true, DesktopSatateEnum.ACTIVE, new HashSet<>()),
		new Desktop(new DesktopID("3"), "PANEL DE CONFIGURACION ", userId, (short) 2, true, true, DesktopSatateEnum.DELETED, new HashSet<>()),
		new Desktop(new DesktopID("4"), "PANEL CUSTOMIZADO1     ", userId, (short) 3, false, true, DesktopSatateEnum.ACTIVE, new HashSet<>()),
		new Desktop(new DesktopID("5"), "PANEL CUSTOMIZADO2     ", userId, (short) 4, false, false, DesktopSatateEnum.ACTIVE, new HashSet<>()),
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

}
