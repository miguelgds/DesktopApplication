package net.agata.desktopmodel.domain.desktop.repository;

import java.util.Collection;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.user.UserID;

public interface DesktopRepository {

    DesktopID nextId();

    Collection<Desktop> findAll();

    Collection<Desktop> findByUser(UserID userId);

    Desktop findById(DesktopID desktopId);

    Desktop save(Desktop desktop);

    void update(Desktop desktop);

    DesktopItem findDesktopItemByPage(PageID pageId);

}
