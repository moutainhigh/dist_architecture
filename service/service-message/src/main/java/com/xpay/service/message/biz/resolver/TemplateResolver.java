package com.xpay.service.message.biz.resolver;

import com.xpay.common.statics.exceptions.BizException;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.io.StringWriter;

@Component
public class TemplateResolver {
    @Autowired
    private FreeMarkerConfigurationFactoryBean freeMarkerConfiguration;

    public String resolve(String tplName, Object param){
        Template template = null;

        try{
            template = freeMarkerConfiguration.getObject().getTemplate(tplName);
        }catch(TemplateNotFoundException e){
            throw new BizException(e.getTemplateName()+" 模版不存在");
        }catch(MalformedTemplateNameException e){
            throw new BizException(e.getTemplateName()+" 模版名称不合法，"+e.getMalformednessDescription());
        }catch(ParseException e){
            throw new BizException(e.getTemplateName() + "，" + e.getMessage());
        }catch(Exception e){
            throw new BizException(tplName + " 模版解析出现异常，" + e.getMessage());
        }

        try{
            StringWriter result = new StringWriter();
            template.process(param, result);
            return result.toString();
        }catch(TemplateException e){
            throw new BizException(e.getTemplateName() + "，" + e.getMessage());
        }catch(Exception e){
            throw new BizException(tplName + " 模版处理过程出现异常，" + e.getMessage());
        }
    }
}
