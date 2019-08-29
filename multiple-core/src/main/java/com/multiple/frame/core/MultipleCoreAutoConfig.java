package com.multiple.frame.core;

import com.multiple.frame.common.base.BizInterceptorOrder;
import com.multiple.frame.common.support.*;
import com.multiple.frame.core.config.MultipleFrameProperties;
import com.multiple.frame.core.dispatch.BizDispatch;
import com.multiple.frame.core.dispatch.ChannelBizDispatch;
import com.multiple.frame.core.handler.method.MethodMappingManager;
import com.multiple.frame.core.interceptor.InvokerMethodBizInterceptor;
import com.multiple.frame.core.interceptor.LookExecuteBizInterceptor;
import com.multiple.frame.core.interceptor.RequestResolverBizInterceptor;
import com.multiple.frame.core.interceptor.ReturnValueBizInterceptor;
import com.multiple.frame.core.support.ArgumentResolverComposite;
import com.multiple.frame.core.support.ExceptionHandlerComposite;
import com.multiple.frame.core.support.ReturnValueHandlerComposite;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 自动加载
 *
 * @author: junqing.li
 * @date: 2019/8/27
 */
@ComponentScan
@EnableConfigurationProperties(MultipleFrameProperties.class)
@Configuration
public class MultipleCoreAutoConfig {


    @Bean
    public MethodMappingManager methodMappingManager() {
        return new MethodMappingManager();
    }

    @Order(BizInterceptorOrder.invokerMethod)
    @Bean
    @ConditionalOnMissingBean(name = "invokerMethodBizInterceptor")
    public GlobalInterceptor invokerMethodBizInterceptor() {
        return new InvokerMethodBizInterceptor();
    }

    @Order(BizInterceptorOrder.lookExecute)
    @Bean
    @ConditionalOnMissingBean(name = "lookExecuteBizInterceptor")
    public GlobalInterceptor lookExecuteBizInterceptor(MethodMappingManager methodMappingManager) {
        LookExecuteBizInterceptor interceptor = new LookExecuteBizInterceptor();
        interceptor.setMethodMappingManager(methodMappingManager);
        return interceptor;
    }


    @Order(BizInterceptorOrder.argumentResolve)
    @Bean
    @ConditionalOnMissingBean(name = "requestResolverBizInterceptor")
    public GlobalInterceptor requestResolverBizInterceptor(List<ArgumentResolver> argumentResolvers) {
        RequestResolverBizInterceptor interceptor = new RequestResolverBizInterceptor();
        ArgumentResolverComposite composite = new ArgumentResolverComposite();
        composite.addArgumentResolver(argumentResolvers);
        interceptor.setArgumentResolverComposite(composite);
        return interceptor;
    }

    @Order(BizInterceptorOrder.returnValueHandler)
    @Bean
    @ConditionalOnMissingBean(name = "returnValueBizInterceptor")
    public GlobalInterceptor returnValueBizInterceptor(List<ReturnValueHandler> returnValueHandlers) {
        ReturnValueBizInterceptor interceptor = new ReturnValueBizInterceptor();
        ReturnValueHandlerComposite composite = new ReturnValueHandlerComposite();
        composite.addReturnValueHandler(returnValueHandlers);
        interceptor.setReturnValueHandlerComposite(composite);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(name = "exceptionHandlerComposite")
    public ExceptionHandlerComposite exceptionHandlerComposite(List<ExceptionHandler> exceptionHandlers) {

        ExceptionHandlerComposite composite = new ExceptionHandlerComposite();
        composite.addExceptionHandlers(exceptionHandlers);
        return composite;
    }


    @Bean
    @ConditionalOnMissingBean
    public BizDispatch bizDispatch(List<Interceptor> interceptors,
                                   ExceptionHandlerComposite exceptionHandlerComposite) {
        ChannelBizDispatch dispatch = new ChannelBizDispatch();
        dispatch.setInterceptors(interceptors);
        dispatch.setExceptionHandlerComposite(exceptionHandlerComposite);
        return dispatch;
    }

}
