package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.aop.RequestAop;
import org.ethanyuen.springboot.bean.BaseNameEntity;
import org.ethanyuen.springboot.bean.Company;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.bean.form.CompanyFormParam;
import org.ethanyuen.springboot.bean.form.UserFormParam;
import org.ethanyuen.springboot.bean.query.CompanyQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.EntityDataException;
import org.ethanyuen.springboot.utilbean.Params;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utils.SQLUtil;
import org.ethanyuen.springboot.utils.Utils;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.aop.Aop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@Api(tags ="公司管理")
@RequestMapping("company")
public class CompanyModule extends BaseModule<Company>{
    @Autowired
    UserModule userModule;
    @Autowired
    RoleModule roleModule;
    @SneakyThrows
    @RequestMapping
    @SaCheckPermission("company:add")
    @SysLog(type = BusinessType.ADD)
    @ApiOperation(value = "新增公司")
    @Aop(TransAop.READ_COMMITTED)
    public Result addCompany(CompanyFormParam companyFormParam) {
        Result result = new Result();
        Company company = Utils.transferPojo(Company.class, companyFormParam);
        if (checkEntityExist(company)) {
            return result.setBad("该公司已存在");
        }
        company = dao.insert(company);
        User user = dao.fetch(User.class, SQLUtil.baseCnd().and(BaseNameEntity.Fields.name, "=", company.getPhone()));
        if (user==null) {
             user = new User();
            user.setName(company.getPhone());
            user.setCompanyId(company.getId());
            user.setPhone(company.getPhone());
            user.setPassword("123456");
            result = userModule.setUser(Utils.transferPojo(UserFormParam.class,user));
        }else{
            result.setObj(user.getId());
        }
        if (result.getStatus() == -1) {
            throw new EntityDataException(result.getInfo());
        } else {
            company.setManagerId((Long) result.getObj());
            dao.update(company);
//            Role role = dao.fetch(Role.class, Cnd.where(Role.Fields.name, "=", Roles.ROLE_COMPANYA));
//            roleModule.setUserRoles(user.getId(), role.getId() + "");
            SystemLog systemLog = new SystemLog(Params.OP_INIT,BusinessType.OTHER,company.getId());
            dao.insert(systemLog);
        }
        return result.setOk("操作成功：公司管理员账号为" + company.getPhone() + "默认密码123456");
    }

    @RequestMapping
    @SaCheckPermission("company:delete")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation(value = "删除公司")
    public Result deleteCompany(String ids) {
        return  deleteEntities(ids);
    }

    @RequestMapping
    @SaCheckPermission("company:edit")
    @SysLog(type = BusinessType.EDIT)
    @ApiOperation(value = "编辑公司")
    public Result editCompany(CompanyFormParam company) {
        return setEntity(company);
    }

    @RequestMapping
    @SaCheckPermission("company:get")
    @ApiOperation(value = "获取公司列表")
    public Result<Company> getCompanies(CompanyQueryParam condition, BaseCondition baseCondition, boolean isTree) {
        SqlExpressionGroup keyFilter=new SqlExpressionGroup();
        if (!isTree) {
            baseCondition.setLinks(Company.Fields.manager);
            keyFilter.orEX(Company.Fields.phone, "like", StrUtil.isBlank(condition.getPhone()) ? null : "%" + condition.getPhone() + "%");
            keyFilter.orEX(Company.Fields.contact, "like", StrUtil.isBlank(condition.getContact()) ? null : "%" + condition.getContact() + "%");
        }
        return getEntities(condition,baseCondition,keyFilter);
    }
    /**
     * 获取某用户可以查看的公司列表
     *
     * @return
     */
    public List<Long> getUserCompanies() {
        User user= RequestAop.getLogUser();
        Long userId=user.getId();
        List<Long> companyIdList=new ArrayList<>();
        List<Company> companies;
        if (user.getCompanyId()==0) {
            companies = dao.query(Company.class, SQLUtil.baseCnd());
            companyIdList = companies.stream().map(item -> item.getId()).collect(Collectors.toList());
        }
        /*else{
            List<CompanyGroup> companyGroups = dao.query(CompanyGroup.class, SQLUtil.baseCnd().and(CompanyGroup.Fields.receiverId, "=", userId));
            List<MidCompanyGroup> midCompanyGroups = new ArrayList<>();
            for (CompanyGroup companyGroup : companyGroups) {
                midCompanyGroups.addAll(dao.query(MidCompanyGroup.class, SQLUtil.baseCnd().and(MidCompanyGroup.Fields.companyGroupId, "=", companyGroup.getId())));
            }
            companyIdList = midCompanyGroups.stream().map(item -> item.getCompanyId()).collect(Collectors.toList());
            List<Company> userCompanies = dao.query(Company.class, SQLUtil.baseCnd().and(Company.Fields.managerId, "=", user.getId()));
            companyIdList.addAll(userCompanies.stream().map(item->item.getId()).collect(Collectors.toList()));
        }*/
        companyIdList.add(user.getCompanyId());
        companyIdList= companyIdList.stream().distinct().collect(Collectors.toList());
        return companyIdList;
    }
}
