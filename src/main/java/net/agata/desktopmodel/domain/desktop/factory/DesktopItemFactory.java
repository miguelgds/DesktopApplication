package net.agata.desktopmodel.domain.desktop.factory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.domain.page.valueobject.SharedPage;
import net.agata.desktopmodel.subdomain.user.UserID;

public class DesktopItemFactory {

    private PageRepository pageRepository;
    private DesktopRepository desktopRepository;

    public DesktopItemFactory(PageRepository pageRepository, DesktopRepository desktopRepository) {
	super();
	this.pageRepository = pageRepository;
	this.desktopRepository = desktopRepository;
    }

    public Collection<DesktopItem> sharedDesktopItemsByUser(UserID userId, DesktopID desktopId) {
	AtomicInteger index = new AtomicInteger(0);
	return this.pageRepository.findSharedPagesByUser(userId)
			   .stream()
			   .map(SharedPage::getPageId)
			   .distinct()
			   .map(desktopRepository::findDesktopItemByPage)
			   .map(di -> new DesktopItem(desktopId, di.getIconId(), di.getColorId(), di.getPageId(), null, false, (short)index.getAndIncrement()))
			   .collect(Collectors.toList());
    }

}
