package net.agata.desktopmodel.infrastructure.desktop.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.repository.DesktopRepository;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.subdomain.user.UserID;

public class DesktopRepositoryInMemoryImpl implements DesktopRepository {

    private final Map<DesktopID, Desktop> datasource = new HashMap<>();

    public DesktopRepositoryInMemoryImpl() {
	super();
    }

    public DesktopRepositoryInMemoryImpl(Set<Desktop> initialDesktops) {
	this();
	initialDesktops.forEach(this::save);
    }

    @Override
    public Collection<Desktop> findAll() {
	return datasource.values();
    }

    @Override
    public Collection<Desktop> findByUser(UserID userId) {
	return findAll().stream()
			.filter(d -> d.getUserId().equals(userId))
			.collect(Collectors.toList());
    }

    @Override
    public Desktop findById(DesktopID desktopId) {
	return datasource.getOrDefault(desktopId, null);
    }

    @Override
    public Desktop save(Desktop desktop) {
	datasource.putIfAbsent(desktop.getDesktopId(), desktop);
	return desktop;
    }

    @Override
    public void update(Desktop desktop) {
	datasource.replace(desktop.getDesktopId(), desktop);
    }

}
