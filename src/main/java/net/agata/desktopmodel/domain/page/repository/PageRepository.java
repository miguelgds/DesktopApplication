package net.agata.desktopmodel.domain.page.repository;

import java.util.Collection;

import net.agata.desktopmodel.domain.page.valueobject.SharedPage;
import net.agata.desktopmodel.subdomain.user.UserID;

public interface PageRepository {

    public Collection<SharedPage> findSharedPagesByUser(UserID userId);
}
