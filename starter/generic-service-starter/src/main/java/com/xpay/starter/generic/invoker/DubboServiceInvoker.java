package com.xpay.starter.generic.invoker;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.List;

/**
 * @author chenyf
 * 作用：用以实现dubbo接口的泛化调用，类似REST调用，只不过REST模式下是使用http协议发起调用，而在这里是通过RMI使用dubbo协议发起调用
 */
public class DubboServiceInvoker {
    private final static String REFERENCE_CONFIG_CACHE_NAME = "_GENERIC_INVOKE_";

    public DubboServiceInvoker(String applicationName, String registryAddress){
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddress);
        SingletonHolder.INSTANCE.application = applicationConfig;
        SingletonHolder.INSTANCE.registry = registryConfig;
    }

    public Object invoke(String interfaceClass, String methodName, Parameters parameters){
        return SingletonHolder.INSTANCE.invoke(interfaceClass, methodName, parameters);
    }

    public void destroy(){
        try{
            Thread.sleep(4000);//休眠4秒，等待其他步骤完成
        }catch(Exception e){}
        ReferenceConfigCache.getCache(REFERENCE_CONFIG_CACHE_NAME).destroyAll();
    }

    private static class SingletonHolder {
        private static Invoker INSTANCE = new Invoker();
    }

    private static class Invoker {
        private ApplicationConfig application;
        private RegistryConfig registry;

        Object invoke(String interfaceClass, String methodName, Parameters parameters){
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(application);
            reference.setRegistry(registry);
            reference.setInterface(interfaceClass); // 接口名
            reference.setGeneric(true); // 声明为泛化接口

            //ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，需要缓存，否则重复生成ReferenceConfig
            // 可能造成性能问题并且会有内存和连接泄漏
            ReferenceConfigCache cache = ReferenceConfigCache.getCache(REFERENCE_CONFIG_CACHE_NAME);
            GenericService genericService = cache.get(reference);//用GenericService替代所有接口引用

            List<Parameters.Parameter> parameterList = parameters.getParameterList();
            int len = parameterList.size();
            String[] invokeParamTypes = new String[len];
            Object[] invokeParams = new Object[len];
            for(int i = 0; i < len; i++){
                invokeParamTypes[i] = parameterList.get(i).getType();
                invokeParams[i] = parameterList.get(i).getValue();
            }
            return genericService.$invoke(methodName, invokeParamTypes, invokeParams);
        }
    }
}
