package net.agata.desktopmodel.domain;

import java.util.Collection;

public interface DomainEventPublisher {

    void publish(Collection<DomainEvent> events);
}
