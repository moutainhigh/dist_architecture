# 通用分布式系统架构
## 项目说明  
1).比较通用的互联网系统架构，使用Java作为后端语言，Vue作为前端语言  
2).后端基础框架使用SpringBoot+Dubbo来实现微服务治理，前端则使用ElemtneUI实现前后端分离  
3).功能组件有：RocketMQ、Redis、Elasticsearch、FastDFS、Zookeeper、Nacos、Quartz、SpringCloudGateway、JavaMail 等  
4).实现的业务功能有：服务注册与发现、配置中心、微服务网关、MQ消息、分布式事务、缓存、限流、分布式锁、文件存储、定时任务调度、邮件、短信、企业管理后台 等  
5).架构目标：对开发人员是简单易用的；每一层、每个中间件 是高可用、可水平拓展的；高性能优化及系统运维是方便的
 
## 架构图
![arch_pic](https://github.com/yufeng629/dist_architecture/blob/master/docs/sys_arch_pic.png)
 
## 微服务划分
我们有必要对业务架构进行归类划分，大类上分为几个模块：基础工具模块、基础业务服务模块、特定业务线模块、网关模块，大数据模块，其中，基础业务服务提供整个平台公用的功能和逻辑，必须保证功能独立、简洁高效、稳定自治，它的服务划分依据是功能性，特定业务线服务则是每个不同业务线下的单独服务，其服务划分依据是业务线，原则上基础业务服务不可调用其他任何服务，要实现完全的自治。整体划分参考样例如下： 
![microservice](https://github.com/yufeng629/dist_architecture/blob/master/docs/biz_service.png)

## 组件封装  
**RockemtMQ**：封装了RockemtMQ发送端，让普通开发人员不必弄懂RocketMQ的原理和API，开箱即用，降低门槛，同时也为后续的系统拓展和监控做预留  
**Redis**：封装Redis的客户端，使开发人员不必拘泥于其集群模式，开箱机用，同时也为后续的监控和热点key的处理做预留  
**Redis分布式锁**：封装好了加锁、释放锁、锁的续租功能，开箱即用  
**Elasticsearch**：封装了ES的查询客户端，以接近sql的方式让开发人员不必懂太多ES内容，可以完成常规业务的查询功能，也是开箱即用  
**FastDFS**：封装fastdfs的客户端，解决了其集群故障时无法自动故障转移的问题，开箱即用，开发人员不必关心fastdfs的内容  
**Email**：封装邮件发送接口，也是开箱即用，开发人员不必关注Email的实现原理，只需配置Email服务器信息即可  
**分布式ID**：以RPC接口的形式提供ID生成器，业务开发人员只管调用接口即可，无需关心序列号重复问题  
**网关**：封装了网关层常用功能验签、加签、限流等功能，开发人员只需关注业务即可，安全性由网关自身保证  
**定时任务**：以独立服务的形式运行，业务开发人员只需调用接口或者在页面上去添加、暂停、恢复定时任务即可，不用关注实现原理，任务触发之后业务端只需接收消息即可  
**配置中心**：能够实现配置共享、配置实时更新，可施行灰度发布，大大简化对配置文件的管理  
 
