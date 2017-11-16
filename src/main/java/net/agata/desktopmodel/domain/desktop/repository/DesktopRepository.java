package net.agata.desktopmodel.domain.desktop.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.desktop.valueobject.SharedDesktop;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.user.UserGroupID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.types.PermissionEnum;

public interface DesktopRepository {

    DesktopID nextId();

    Collection<Desktop> findAll();

    Collection<Desktop> findByUser(UserID userId);

    Desktop findById(DesktopID desktopId);

    Desktop save(Desktop desktop);

    void update(Desktop desktop);

    DesktopItem findDesktopItemByPage(PageID pageId);

    List<SharedDesktop> sharedDesktopsByUser(UserID userId);

    void shareDesktop(UserID userId, DesktopID desktopId, UserGroupID userGroupId, PermissionEnum permission);

    void unshareDesktop(DesktopID desktopId);

    Map<PermissionEnum, List<Desktop>> findSharedsByUser(UserID userId);

    Set<Desktop> findDesktopsThatContainsApplication(ApplicationID applicationId);

}
