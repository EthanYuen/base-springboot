# 项目简介
该项目是基于springboot的单体应用脚手架，个人追求代码简洁，没有service层和dao层，controller继承一个基类即可，RequestMapping不写接口名默认为方法名，自动根据接口生成权限，可基于注解自动生成vo,dto,根据po字段注解自动解析sql查询、更新条件，自动备份数据库、根据接口自动生成接口文档等功能。
## 技术选型
|功能|技术选型|
| :---: | :---:|
| 总体框架 | springboot |
|权限校验|sa-token|
|ORM框架|nutz-dao|
|数据库缓存|ehcache|
|编译自动生成代码|auto-service,javapoet|
|定时器|quartz|
