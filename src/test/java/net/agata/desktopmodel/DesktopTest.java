package net.agata.desktopmodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopSatateEnum;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.ui.PageID;
import net.agata.desktopmodel.subdomain.user.UserID;

@RunWith(Parameterized.class)
public class DesktopTest {

    @Parameter
    public DesktopID desktopId;
    @Parameter(1)
    public String name;
    @Parameter(2)
    public UserID userId;
    @Parameter(3)
    public Short order;
    @Parameter(4)
    public Boolean fixed;
    @Parameter(5)
    public Boolean readonly;
    @Parameter(6)
    public DesktopSatateEnum state;
    @Parameter(7)
    public Set<DesktopItem> items;

    @Parameters
    public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] {
		{ new DesktopID(UUID.randomUUID().toString()), "Panel de usuario", new UserID(1), Short.valueOf("0"), Boolean.TRUE,
			Boolean.FALSE, DesktopSatateEnum.ACTIVE, new HashSet<>() }
	});
    }

    @Test
    public void newDesktop() {
	Desktop desktop = new Desktop(this.desktopId, this.name, this.userId, this.order, this.fixed, this.readonly, this.state,
		this.items);
	Assert.assertEquals(this.desktopId, desktop.getDesktopId());
	Assert.assertEquals(this.name, desktop.getName());
	Assert.assertEquals(this.userId, desktop.getUserId());
	Assert.assertEquals(this.order, desktop.getOrder());
	Assert.assertEquals(this.fixed, desktop.getFixed());
	Assert.assertEquals(this.readonly, desktop.getReadonly());
    }

    @Test
    public void addApplicationToDesktop() {
	Desktop desktop = new Desktop(this.desktopId, this.name, this.userId, this.order, this.fixed, this.readonly, this.state,
		this.items);
	ApplicationID appId = new ApplicationID(UUID.randomUUID().toString());
	desktop.addApplication(new IconID((short) 1), new ColorID((short) 2), appId);
	Assert.assertTrue(desktop.getItems().size() == 1);
	Assert.assertTrue(desktop.getItems()
				 .stream()
				 .filter(di -> di.getApplicationId().equals(appId))
				 .count() == 1L);
    }

    @Test
    public void addPageToDesktop() {
	Desktop desktop = new Desktop(this.desktopId, this.name, this.userId, this.order, this.fixed, this.readonly, this.state,
		this.items);
	PageID pageId = new PageID(1);
	desktop.addPage(new IconID((short) 1), new ColorID((short) 2), pageId);
	Assert.assertTrue(desktop.getItems().size() == 1);
	Assert.assertTrue(desktop.getItems()
				 .stream()
				 .filter(di -> di.getPageId().equals(pageId))
				 .count() == 1L);
    }
}
