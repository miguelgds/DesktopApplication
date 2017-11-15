package net.agata.desktopmodel.domain.desktop.event;

import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import net.agata.desktopmodel.domain.DomainEvent;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopItemID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;

public class DesktopItemPageRemoved implements DomainEvent {

    public static final String EVENT_TYPE = "agataui.desktops.itemRemoved";

    private final DesktopID desktopId;
    private final DesktopItemID desktopItemId;
    private final PageID pageId;
    private final Instant when;

    public DesktopItemPageRemoved(DesktopID desktopId, DesktopItemID desktopItemId, PageID pageId) {
	Validate.notNull(desktopId);
	Validate.notNull(desktopItemId);
	Validate.notNull(pageId);
	this.desktopId = desktopId;
	this.desktopItemId = desktopItemId;
	this.pageId = pageId;
	this.when = Instant.now();
    }

    public DesktopItemID getDesktopItemId() {
	return desktopItemId;
    }

    public PageID getPageId() {
	return pageId;
    }

    @Override
    public String type() {
	return EVENT_TYPE;
    }

    @Override
    public Instant when() {
	return this.when;
    }

    @Override
    public UUID aggregateId() {
	return UUID.fromString(this.desktopId.getId());
    }

    @Override
    public String toString() {
	return "DesktopItemPageRemoved [desktopId=" + desktopId + ", desktopItemId=" + desktopItemId + ", pageId=" + pageId + ", when="
		+ when + "]";
    }

}
