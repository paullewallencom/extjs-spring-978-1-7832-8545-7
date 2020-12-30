package com.gieman.tttracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration("/testingContext.xml")
public abstract class AbstractServiceForTesting extends AbstractTransactionalJUnit4SpringContextTests {

    final protected Logger logger = LoggerFactory.getLogger(this.getClass());
        
}