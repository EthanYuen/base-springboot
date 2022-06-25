package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.enums.BusinessType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.*;

@ApiModel("日志类")
@Table
@Data
@FieldNameConstants
public class SystemLog extends BaseEntity {

	@ApiModelProperty("类方法名行号")
	@Column
	@ColDefine(type= ColType.VARCHAR,width=128)
	private String source;

	@ApiModelProperty("操作描述")
	@Column
	@ColDefine(type= ColType.VARCHAR,width=128)
	@Search
	private String operation;

	@ApiModelProperty("其他信息")
	@Column
	@ColDefine(type= ColType.TEXT)
	private String info;

	@ApiModelProperty("业务类型")
	@Column
	@Search
	private BusinessType type;

	@ApiModelProperty("所属公司id")
	@Column
	private Long companyId;

	@ApiModelProperty("所属公司")
	@One(field = Fields.companyId)
	private Company company;

	public SystemLog(String operation, BusinessType type, Long companyId) {
		this.operation = operation;
		this.type = type;
		this.companyId = companyId;
	}

	public SystemLog() {

	}
}
