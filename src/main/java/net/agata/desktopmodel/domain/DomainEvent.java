package net.agata.desktopmodel.domain;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    String type();

    Instant when();

    UUID aggregateId();
}
