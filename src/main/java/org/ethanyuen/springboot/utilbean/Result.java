package org.ethanyuen.springboot.utilbean;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
@ApiModel("返回结果")
public class Result<T>
{
    public final static Integer StatusOk = 1;
    public final static Integer StatusFail = -1;
    public final static Integer Status500 = 500;
    public final static Integer StatusNoAuth = -9;

    public Result setOk()
    {
        this.setStatus(StatusOk);
        this.setInfo("操作成功");
        return this;
    }

    public Result setBad()
    {
        return this.setBad("");
    }

    public Result setBad(String info)
    {
        this.setStatus(StatusFail);
        this.setInfo(StrUtil.isBlank(info)?"操作失败":info);
        this.setTotal(0);
        return this;
    }

    @ApiModelProperty("处理结果信息")
    private String info;
    @ApiModelProperty("处理结果状态:-9登陆失效，1操作成功，-1操作失败")
    private Integer status;
    @ApiModelProperty("处理结果列表")
    private List<T> list;
    @ApiModelProperty("处理结果-实体类")
    private Object obj;
    @ApiModelProperty("总记录数")
    private int total;

    /**
     * 操作成功
     * @param info
     * @return
     */
    public static Result setOk(String info)
    {
        return new Result().setStatus(StatusOk).setInfo(StrUtil.isBlank(info)?"操作成功":info);
    }

    /**
     * 操作失败
     * @param info
     * @return
     */
    public static Result setFail(String info)
    {
        return new Result().setStatus(StatusFail).setInfo(StrUtil.isBlank(info)?"操作失败":info);
    }
    /**
     * 无权限
     * @return
     */
    public static Result setUnauthorize()
    {
        return new Result().setStatus(StatusNoAuth).setInfo("登录信息已失效,请重新登录");
    }
    public String getInfo()
    {
        return info;
    }

    public Result setInfo(String info)
    {
        this.info = info;
        return this;
    }

    public Integer getStatus()
    {
        return status;
    }

    public Result setStatus(Integer status)
    {
        this.status = status;
        return this;
    }

    public List getList()
    {
        return list;
    }

    public Result setList(List list)
    {
        this.list = list;
        return this;
    }

    public Object getObj()
    {
        return obj;
    }

    public Result setObj(Object obj)
    {
        this.obj = obj;
        return this;
    }

    public int getTotal()
    {
        return total;
    }

    public Result setTotal(int total)
    {
        this.total = total;
        return this;
    }

}
