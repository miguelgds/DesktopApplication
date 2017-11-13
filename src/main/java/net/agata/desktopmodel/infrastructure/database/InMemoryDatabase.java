package net.agata.desktopmodel.infrastructure.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple7;
import io.vavr.Tuple8;
import net.agata.desktopmodel.domain.application.valueobject.ApplicationID;
import net.agata.desktopmodel.domain.desktop.valueobject.DesktopID;
import net.agata.desktopmodel.domain.page.valueobject.PageID;
import net.agata.desktopmodel.subdomain.ui.ColorID;
import net.agata.desktopmodel.subdomain.ui.IconID;
import net.agata.desktopmodel.subdomain.user.UserGroupID;
import net.agata.desktopmodel.subdomain.user.UserID;
import net.agata.desktopmodel.utils.types.PermissionEnum;
import net.agata.desktopmodel.utils.types.StateEnum;

public class InMemoryDatabase {

    private InMemoryDatabase() {
	super();
    }

    public static void initData() {
	DESKTOP.clear();
	DESKTOP.putAll(DESKTOP_INITIAL_DATA);

	DESKTOP_USER_GROUP.clear();
	DESKTOP_USER_GROUP.putAll(DESKTOP_USER_GROUP_INITIAL_DATA);

	DESKTOP_ITEM.clear();
	DESKTOP_ITEM.putAll(DESKTOP_ITEM_INITIAL_DATA);
    }

    /**
     * ID_DESKTOP, NAME, ID_USER, ORDER, FIXED, READONLY, STATE, VERSION
     */
    public static final DesktopID DESKTOP_ID_1 = new DesktopID("1");
    public static final DesktopID DESKTOP_ID_2 = new DesktopID("2");
    public static final DesktopID DESKTOP_ID_3 = new DesktopID("3");
    public static final DesktopID DESKTOP_ID_4 = new DesktopID("4");
    public static final DesktopID DESKTOP_ID_5 = new DesktopID("5");
    public static final DesktopID DESKTOP_ID_6 = new DesktopID("6");
    private static final Map<DesktopID, Tuple8<DesktopID, String, UserID, Short, Boolean, Boolean, StateEnum, Long>> DESKTOP_INITIAL_DATA = new HashMap<>();
    public static final Map<DesktopID, Tuple8<DesktopID, String, UserID, Short, Boolean, Boolean, StateEnum, Long>> DESKTOP = new HashMap<>();
    static{
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_1, new Tuple8<>(DESKTOP_ID_1, "PANEL DE USUARIO", new UserID(4), (short) 0, true, true, StateEnum.ACTIVE, 0L));
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_2, new Tuple8<>(DESKTOP_ID_2, "FUNCIONALIDADES COMUNES", new UserID(4), (short) 1, true, false, StateEnum.ACTIVE, 0L));
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_3, new Tuple8<>(DESKTOP_ID_3, "PANEL DE CONFIGURACION", new UserID(4), (short) 2, true, true, StateEnum.DELETED, 1L));
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_4, new Tuple8<>(DESKTOP_ID_4, "PANEL CUSTOMIZADO1", new UserID(4), (short) 3, false, true, StateEnum.ACTIVE, 0L));
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_5, new Tuple8<>(DESKTOP_ID_5, "PANEL CUSTOMIZADO2", new UserID(4), (short) 4, false, false, StateEnum.ACTIVE, 0L));
	DESKTOP_INITIAL_DATA.put(DESKTOP_ID_6, new Tuple8<>(DESKTOP_ID_6, "FUNCIONALIDADES COMUNES", new UserID(3), (short) 0, false, false, StateEnum.ACTIVE, 0L));
    }
    
    /**
     * ID_DESKTOP, ID_USER_GROUP, PERMISSIONS
     */
    private static final Map<Tuple2<DesktopID, UserGroupID>, Tuple3<DesktopID, UserGroupID, PermissionEnum>> DESKTOP_USER_GROUP_INITIAL_DATA = new HashMap<>();
    public static final Map<Tuple2<DesktopID, UserGroupID>, Tuple3<DesktopID, UserGroupID, PermissionEnum>> DESKTOP_USER_GROUP = new HashMap<>();
    static {
	DESKTOP_USER_GROUP_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_6, new UserGroupID(8)), new Tuple3<>(DESKTOP_ID_6, new UserGroupID(8), PermissionEnum.READ_WRITE));
    }

    /**
     * ID_DESKTOP, ID_ICON, ID_COLOR, ID_PAGE, ID_APPLICATION, IS_FAVOURITE, ORDER
     */
    private static final Map<Tuple2<DesktopID, Short>, Tuple7<DesktopID, IconID, ColorID, PageID, ApplicationID, Boolean, Short>> DESKTOP_ITEM_INITIAL_DATA = new HashMap<>();
    public static final Map<Tuple2<DesktopID, Short>, Tuple7<DesktopID, IconID, ColorID, PageID, ApplicationID, Boolean, Short>> DESKTOP_ITEM = new HashMap<>();
    static{
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_1, (short) 0), new Tuple7<>(DESKTOP_ID_1, new IconID((short) 1), new ColorID((short) 1), new PageID(1), null, false, (short) 0));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 0), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 7), new ColorID((short) 1), new PageID(7), null, false, (short) 0));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 1), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 6), new ColorID((short) 2), new PageID(4), null, false, (short) 1));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 2), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 1), new ColorID((short) 5), new PageID(3), null, false, (short) 2));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 3), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 5), new ColorID((short) 6), new PageID(2), null, false, (short) 3));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 4), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 8), new ColorID((short) 2), new PageID(8), null, false, (short) 4));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_2, (short) 5), new Tuple7<>(DESKTOP_ID_2, new IconID((short) 3), new ColorID((short) 3), new PageID(2), null, false, (short) 5));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_5, (short) 0), new Tuple7<>(DESKTOP_ID_5, new IconID((short) 2), new ColorID((short) 2), null, new ApplicationID("1"), false, (short) 0));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_6, (short) 0), new Tuple7<>(DESKTOP_ID_6, new IconID((short) 11), new ColorID((short) 7), new PageID(12), null, false, (short) 0));
	DESKTOP_ITEM_INITIAL_DATA.put(new Tuple2<>(DESKTOP_ID_6, (short) 1), new Tuple7<>(DESKTOP_ID_6, new IconID((short) 12), new ColorID((short) 13), new PageID(6), null, false, (short) 1));
    }
    
    /**
     * ID_PAGE, OWNER, NAME
     */
    public static final List<Tuple3<PageID, UserID, String>> USER_PAGE = Arrays.asList(
	    	new Tuple3<>(new PageID(6), new UserID(3), "Monitorización"),
	    	new Tuple3<>(new PageID(12), new UserID(3), "GKN"),
	    	new Tuple3<>(new PageID(14), new UserID(4), "Monitorización")
	    );

    /**
     * ID_USER_GROUP, ID_USER
     */
    public static final List<Tuple2<UserGroupID, UserID>> USER_GROUP_USER = Arrays.asList(
	    	new Tuple2<>(new UserGroupID(2), new UserID(4)),
	    	new Tuple2<>(new UserGroupID(8), new UserID(4))
	    );

    /**
     * ID_USER_GROUP, ID_PAGE, RO_RW
     */
    public static final List<Tuple3<UserGroupID, PageID, PermissionEnum>> USER_GROUP_PAGE = Arrays.asList(
	    	new Tuple3<>(new UserGroupID(1), new PageID(6), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(2), new PageID(6), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(7), new PageID(6), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(8), new PageID(6), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(8), new PageID(12), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(9), new PageID(6), PermissionEnum.READ),
	    	new Tuple3<>(new UserGroupID(9), new PageID(14), PermissionEnum.READ_WRITE)
	    );
}
