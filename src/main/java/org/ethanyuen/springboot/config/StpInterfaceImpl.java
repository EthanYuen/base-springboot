package org.ethanyuen.springboot.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.ListUtil;
import org.ethanyuen.springboot.module.PermissionModule;
import org.ethanyuen.springboot.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId+"");
        Map<String, Set> authInfo = (Map<String, Set>) Utils.getIocBean(PermissionModule.class).getUserAndRolesPermissions(userId).getObj();
        return ListUtil.toList(authInfo.get("ownedPermissions") );
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId+"");
        Map<String, Set> authInfo = (Map<String, Set>) Utils.getIocBean(PermissionModule.class).getUserAndRolesPermissions(userId).getObj();
        return ListUtil.toList(authInfo.get("ownedRoles"));
    }

}