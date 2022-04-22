package com.gguproject.jarvis.plugin.vaccum.service;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import javax.inject.Named;

@Named
public class AbstractVacuumService {

    protected final TestService testService;

    public AbstractVacuumService(@Qualifier("test") TestService testService) {
        this.testService = testService;
    }

    public void test(){
        this.testService.test();
    }
}
