package net.agata.desktopmodel.domain.desktop.service;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;

public class DesktopsService {

    private DesktopRepository desktopRepository;

    public DesktopsService(DesktopRepository desktopRepository) {
	this.desktopRepository = desktopRepository;
    }

    public void removeDesktopItemsRelatedToRemovedApplication(ApplicationID applicationId) {
	Validate.notNull(applicationId);

	this.desktopRepository.findDesktopsThatContainsApplication(applicationId)
			      .stream()
			      .map(d -> d.removeItemsRelatedToRemovedApplication(applicationId))
			      .forEach(desktopRepository::update);
    }
}
