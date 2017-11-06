package net.agata.desktopmodel.domain.desktop.valueobject;

import java.util.Comparator;
import java.util.List;

import net.agata.desktopmodel.domain.desktop.entity.Desktop;
import net.agata.desktopmodel.domain.desktop.entity.DesktopItem;

public enum DisplacementMode {
    LESS_EQUALS_TO_LEFT {
	@Override
	public void reorderItemsFromPivot(List<DesktopItem> itemsToRelocate, DesktopItem pivot) {
	    for (DesktopItem desktopItemToReorder : itemsToRelocate) {
		if (desktopItemToReorder.getOrder().shortValue() <= pivot.getOrder()) {
		    desktopItemToReorder.reorder((short) (desktopItemToReorder.getOrder() - 1));
		}
	    }
	    itemsToRelocate.add(pivot);
	    zipDesktopItemsOrder(itemsToRelocate);
	}

	@Override
	public void reorderDesktopsFromPivot(List<Desktop> desktopsToRelocate, Desktop pivot) {
	    for (Desktop desktop : desktopsToRelocate) {
		if (desktop.getOrder().shortValue() <= pivot.getOrder()) {
		    desktop.reorder((short) (desktop.getOrder() - 1));
		}
	    }
	    desktopsToRelocate.add(pivot);
	    zipDesktopsOrder(desktopsToRelocate);
	}
    },
    GREAT_EQUALS_TO_RIGHT {
	@Override
	public void reorderItemsFromPivot(List<DesktopItem> itemsToRelocate, DesktopItem pivot) {
	    for (DesktopItem desktopItemToReorder : itemsToRelocate) {
		if (desktopItemToReorder.getOrder().shortValue() >= pivot.getOrder()) {
		    desktopItemToReorder.reorder((short) (desktopItemToReorder.getOrder() + 1));
		}
	    }
	    itemsToRelocate.add(pivot);
	    zipDesktopItemsOrder(itemsToRelocate);
	}

	@Override
	public void reorderDesktopsFromPivot(List<Desktop> desktopsToRelocate, Desktop pivot) {
	    for (Desktop desktop : desktopsToRelocate) {
		if (desktop.getOrder().shortValue() >= pivot.getOrder()) {
		    desktop.reorder((short) (desktop.getOrder() + 1));
		}
	    }
	    desktopsToRelocate.add(pivot);
	    zipDesktopsOrder(desktopsToRelocate);
	}
    };

    public static DisplacementMode from(short orderFrom, short orderTo) {
	if (orderFrom < orderTo) {
	    return DisplacementMode.LESS_EQUALS_TO_LEFT;
	}
	return DisplacementMode.GREAT_EQUALS_TO_RIGHT;
    }
    
    public void reorderItemsFromPivot(List<DesktopItem> itemsToRelocate, DesktopItem pivot) {
	throw new UnsupportedOperationException();
    }

    public void reorderDesktopsFromPivot(List<Desktop> desktopsToRelocate, Desktop order) {
	throw new UnsupportedOperationException();
    }

    private static void zipDesktopsOrder(List<Desktop> desktopsToRelocate) {
	desktopsToRelocate.sort(Comparator.comparing(Desktop::getOrder));
	int index = 0;
	for (Desktop desktop : desktopsToRelocate) {
	    desktop.reorder((short) index++);
	}
    }

    private static void zipDesktopItemsOrder(List<DesktopItem> itemsToRelocate) {
	itemsToRelocate.sort(Comparator.comparing(DesktopItem::getOrder));
	int index = 0;
	for (DesktopItem desktopItem : itemsToRelocate) {
	    desktopItem.reorder((short) index++);
	}
    }
}
