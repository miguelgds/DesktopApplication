package net.agata.desktopmodel.domain.desktop.factory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktopItem;
import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.subdomain.user.UserID;

public class DesktopFactory {

    private PageRepository pageRepository;
    private DesktopRepository desktopRepository;

    public DesktopFactory(PageRepository pageRepository, DesktopRepository desktopRepository) {
	super();
	this.pageRepository = pageRepository;
	this.desktopRepository = desktopRepository;
    }

    public List<SharedDesktopItem> sharedPagesToUser(UserID userId) {
	return this.pageRepository.findSharedPagesByUser(userId)
			   .stream()			   
			   .distinct()			   
			   .map(sharedPage -> Optional.ofNullable(desktopRepository.findDesktopItemByPage(sharedPage.getPageId()))
				   		      .map(di -> new SharedDesktopItem(di.getIconId(), di.getColorId(), di.getPageId(), di.getApplicationId(), sharedPage.getPermission()))
			   			      .orElse(null))
			   .collect(Collectors.toList());
    }

    public List<Desktop> desktopsShared(UserID userId) {
	return this.desktopRepository.sharedDesktopsByUser(userId);
    }

}
