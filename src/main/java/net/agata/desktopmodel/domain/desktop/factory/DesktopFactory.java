package net.agata.desktopmodel.domain.desktop.factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.domain.page.valueobject.SharedPage;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.types.StateEnum;

public class DesktopFactory {

    private PageRepository pageRepository;
    private DesktopRepository desktopRepository;

    public DesktopFactory(PageRepository pageRepository, DesktopRepository desktopRepository) {
	super();
	this.pageRepository = pageRepository;
	this.desktopRepository = desktopRepository;
    }

    public Desktop desktopSharedPages(UserID userId, Short desktopOrder) {
	DesktopID desktopId = new DesktopID("shared-desktop");
	AtomicInteger index = new AtomicInteger(0);
	Collection<DesktopItem> sharedPages = this.pageRepository.findSharedPagesByUser(userId)
			   .stream()
			   .map(SharedPage::getPageId)
			   .distinct()
			   .map(desktopRepository::findDesktopItemByPage)
			   .map(di -> new DesktopItem(desktopId, di.getIconId(), di.getColorId(), di.getPageId(), null, false, (short)index.getAndIncrement()))
			   .collect(Collectors.toList());
	return new Desktop(desktopId, "PÁGINAS COMPARTIDAS", userId, desktopOrder, true, true, StateEnum.ACTIVE,
		new HashSet<>(sharedPages));
    }

    public List<Desktop> desktopsShared(UserID userId) {
	return this.desktopRepository.sharedDesktopsByUser(userId);
    }

}
