package ru.sparural.gobals;

import java.util.Locale;

/**
 * @author Vorobyev Vyacheslav
 */
public final class RolesConstants {
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String HAS_ROLE_SPEL_FORMAT = "hasRole('%s')";

    public static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
    public static final String HAS_ROLE_ADMIN = "hasRole('" + ROLE_ADMIN + "')";
    public static final String ROLE_CLIENT = ROLE_PREFIX + "CLIENT";
    public static final String HAS_ROLE_CLIENT = "hasRole('" + ROLE_CLIENT + "')";
    public static final String ROLE_MANAGER = ROLE_PREFIX + "MANAGER";
    public static final String HAS_ROLE_MANAGER = "hasRole('" + ROLE_MANAGER + "')";
    public static final String ROLE_ANONYMOUS = ROLE_PREFIX + "ANONYMOUS";
    public static final String HAS_ROLE_ANONYMOUS = "hasRole('" + ROLE_ANONYMOUS + ";)";

    public static String createRoleString(RoleNames roleNames) {
        return createRoleString(roleNames.getName());
    }

    public static String createRoleString(String role) {
        return RolesConstants.ROLE_PREFIX + role.toUpperCase(Locale.ROOT);
    }

    public static String createHasRoleString(String role) {
        return String.format(RolesConstants.HAS_ROLE_SPEL_FORMAT, role);
    }
}