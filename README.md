# 通用分布式系统架构
## 项目说明  
1).比较通用的互联网系统架构，使用Java作为后端语言，Vue作为前端语言  
2).后端基础框架使用SpringBoot+Dubbo来实现微服务治理，前端则使用ElemtneUI实现前后端分离  
3).功能组件有：RocketMQ、Redis、Elasticsearch、FastDFS、Zookeeper、Nacos、Quartz、SpringCloudGateway、JavaMail 等  
4).实现的业务功能有：服务注册与发现、配置中心、微服务网关、MQ消息、分布式事务、缓存、限流、分布式锁、文件存储、定时任务调度、邮件、短信、企业管理后台 等
 
## 架构图
![arch_pic](https://github.com/yufeng629/dist_architecture/blob/master/docs/sys_arch_pic.png)
 
## 微服务划分
我们有必要对业务架构进行归类划分，大类上分为几个模块：基础工具模块、基础业务服务模块、特定业务线模块、网关模块，大数据模块，其中，基础业务服务提供整个平台公用的功能和逻辑，必须保证功能独立、简洁高效、稳定自治，它的服务划分依据是功能性，特定业务线服务则是每个不同业务线下的单独服务，其服务划分依据是业务线，原则上基础业务服务不可调用其他任何服务，要实现完全的自治。整体划分参考样例如下： 
![microservice](https://github.com/yufeng629/dist_architecture/blob/master/docs/biz_service.png)
 
## 组件封装  

 
