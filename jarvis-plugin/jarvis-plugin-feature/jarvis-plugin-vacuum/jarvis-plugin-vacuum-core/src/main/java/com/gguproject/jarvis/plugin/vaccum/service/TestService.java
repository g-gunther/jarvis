package com.gguproject.jarvis.plugin.vaccum.service;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import javax.inject.Named;

@Named
@Qualifier("test")
public class TestService {

    public void test(){
        System.out.println("ok");
    }
}
