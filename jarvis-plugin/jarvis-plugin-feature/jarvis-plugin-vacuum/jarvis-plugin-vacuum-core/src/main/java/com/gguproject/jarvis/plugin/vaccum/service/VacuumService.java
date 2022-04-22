package com.gguproject.jarvis.plugin.vaccum.service;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class VacuumService extends AbstractVacuumService{

    public VacuumService(@Qualifier("test") TestService testService,
                         @Qualifier("test") Optional<TestService> optionalTestService,
                         @Qualifier("test") List<TestService> testServices) {
        super(testService);
        System.out.println(testServices.size());
        System.out.println(optionalTestService.isPresent());
    }
}
