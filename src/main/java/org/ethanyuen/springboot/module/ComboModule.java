package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.aop.RequestAop;
import org.ethanyuen.springboot.bean.BaseEntity;
import org.ethanyuen.springboot.bean.Company;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.bean.query.CompanyQueryParam;
import org.ethanyuen.springboot.enums.DaoOperator;
import org.ethanyuen.springboot.utilbean.*;
import org.ethanyuen.springboot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("combo")
@Api(tags ="combo管理")
public class ComboModule extends BaseModule<BaseEntity>{
    @Autowired
    CompanyModule companyModule;

    @SneakyThrows
    @RequestMapping
    @SaCheckLogin
    //@SaCheckPermission("combo:getCompanyTree")
    @ApiOperation(value = "获取公司地区树列表")
    public Result<TreeCombo> getCompanyTree(String key) {
        Result result = new Result();
        User user= RequestAop.getLogUser();
        List<TreeCombo> combos = new ArrayList<>();
        TreeCombo province = null;
        TreeCombo city = null;
        TreeCombo companyCombo = null;
        List<Company> companies = new ArrayList<>();
        BaseCondition baseCondition = new BaseCondition();
        Company baseEntity = new Company();
        List orders = new ArrayList();
        OrderCombo combo = new OrderCombo(Company.Fields.province, DaoOperator.DESC);
        orders.add(combo);
        combo = new OrderCombo(Company.Fields.city, DaoOperator.DESC);
        orders.add(combo);
        baseCondition.setOrders(orders);
        if (StrUtil.isNotBlank(key)) {
            baseEntity.setCity(key);
            baseEntity.setCounty(key);
            baseEntity.setAddress(key);
            baseEntity.setContact(key);
            baseEntity.setName(key);
            baseEntity.setPhone(key);
            baseEntity.setProvince(key);
        }
        if (user.getCompanyId() == 0) {
            province = new TreeCombo(0L, "未绑定", new ArrayList<>());
            combos.add(province);
        }
        companies = companyModule.getCompanies(Utils.transferPojo(CompanyQueryParam.class,baseEntity), baseCondition, true).getList();
        String lastCity = "";
        String lastProvince = "";
        for (Company company : companies) {
            String currentCity = company.getCity();
            String currentProvince = company.getProvince();
            if (!lastProvince.equals(currentProvince)) {
                province = new TreeCombo(-1L, currentProvince, new ArrayList<>());
                combos.add(province);
                lastProvince = currentProvince;
            }
            if (!lastCity.equals(currentCity)) {
                city = new TreeCombo(-2L, currentCity, new ArrayList<>());
                province.getChildren().add(city);
                lastCity = currentCity;
            }
            companyCombo = new TreeCombo(company.getId(), company.getName(), null);
            city.getChildren().add(companyCombo);

        }
        result.setList(combos);
        return result.setOk();
    }

    /**
     * @param enumName 枚举类名
     * @param showAttr 将枚举某个属性作为text，没有则用英文,属性名第一个英文大写
     * @return
     */
    @SneakyThrows
    @RequestMapping
    @SaCheckLogin
    //@SaCheckPermission("combo:getEnumCombo")
    @ApiOperation(value = "获取枚举combo")
    public Result<GeneralCombo> getEnumCombo(String enumName, String showAttr) {
        Result result = new Result();
        List<GeneralCombo> combos = new ArrayList<>();
        Class<Enum> enumClazz =(Class<Enum>) Class.forName(Params.BASE_PACKAGE+".enums."+enumName);
        Method method = enumClazz.getMethod("values");
        Enum enums[] = (Enum[]) method.invoke(null);
        Method getTitle;
        try {
             getTitle = enumClazz.getDeclaredMethod("get"+showAttr);
        }catch(Throwable e){
            getTitle=null;
        }
        for (Enum enumObj : enums) {
            String name = enumObj.name();
            GeneralCombo combo = new GeneralCombo(name, getTitle==null?name:getTitle.invoke(enumObj).toString());
            combos.add(combo);
        }
        result.setList(combos);
        result.setOk();
        return result;
    }

}
