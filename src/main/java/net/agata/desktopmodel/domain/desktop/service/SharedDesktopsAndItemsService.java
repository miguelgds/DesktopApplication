package net.agata.desktopmodel.domain.desktop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktop;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktopItem;
import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.subdomain.user.UserID;

public class SharedDesktopsAndItemsService {

    private PageRepository pageRepository;
    private DesktopRepository desktopRepository;

    public SharedDesktopsAndItemsService(PageRepository pageRepository, DesktopRepository desktopRepository) {
	super();
	this.pageRepository = pageRepository;
	this.desktopRepository = desktopRepository;
    }

    public List<SharedDesktopItem> sharedPagesToUser(UserID userId) {
	return this.pageRepository.findSharedPagesByUser(userId)
			   .stream()			   
			   .distinct()			   
			   .map(sharedPage -> Optional.ofNullable(desktopRepository.findDesktopItemByPage(sharedPage.getPageId()))
				   		      .map(di -> new SharedDesktopItem(di.getIconId(), di.getColorId(), di.getPageId(), di.getApplicationId(), 
				   			      sharedPage.getPermission()))
			   			      .orElse(null))
			   .collect(Collectors.toList());
    }

    public List<SharedDesktop> sharedDesktopsToUser(UserID userId) {
	return this.desktopRepository.sharedDesktopsByUser(userId);
    }

}
