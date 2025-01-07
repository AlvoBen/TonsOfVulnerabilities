package com.sap.engine.services.httpserver.chain.impl;

import com.sap.engine.services.httpserver.chain.BaseFilterConfig;
import com.sap.engine.services.httpserver.chain.ChainComposer;
import com.sap.engine.services.httpserver.chain.Filter;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.filters.DSRHttpFilter;
import com.sap.engine.services.httpserver.filters.DefineHostFilter;
import com.sap.engine.services.httpserver.filters.HeaderModifer;
import com.sap.engine.services.httpserver.filters.MemoryStatisticFilter;
import com.sap.engine.services.httpserver.filters.MonitoringFilter;
import com.sap.engine.services.httpserver.filters.ResponseLogWriter;
import com.sap.engine.services.httpserver.filters.SessionSizeFilter;
import com.sap.engine.services.httpserver.filters.SmdResponseLogWriter;
import com.sap.engine.services.httpserver.filters.StaticResourceHandler;
import com.sap.engine.services.httpserver.filters.WebContainerInvoker;
import com.sap.engine.services.httpserver.filters.LogonGroupsConfigRequestsFilter;

public class ChainComposerImpl extends ChainComposer {
  public ChainComposerImpl() throws FilterException{
    
    Filter filter = new DSRHttpFilter();
    filter.init(new BaseFilterConfig("DSR Filter for instrumentaion of the Http service"));
    filters.add(filter);
    
    filter = new MemoryStatisticFilter();
    filter.init(new BaseFilterConfig("Memory Statistic Filter"));
    filters.add(filter);
    
    filter = new SessionSizeFilter();
    filter.init(new BaseFilterConfig("Session Size Calculation Filter"));
    filters.add(filter);
    
    filter = new MonitoringFilter();
    filter.init(new BaseFilterConfig("Monitoring Filter"));
    filters.add(filter);

    filter = new LogonGroupsConfigRequestsFilter();
    filter.init(new BaseFilterConfig("Serves requests of Web Dispatcher about logon groups configuration"));
    filters.add(filter);
    
    filter = new DefineHostFilter();
    filter.init(new BaseFilterConfig("Define Host Filter"));
    filters.add(filter);
    
    filter = new ResponseLogWriter();
    filter.init(new BaseFilterConfig("Response Log Writer"));
    filters.add(filter);

    filter = new SmdResponseLogWriter();
    filter.init(new BaseFilterConfig("SMD Response Log Writer"));
    filters.add(filter);

    filter = new WebContainerInvoker();
    filter.init(new BaseFilterConfig("Web Container Invoker"));
    filters.add(filter);

    filter = new HeaderModifer();
    filter.init(new BaseFilterConfig("Header Modifier Filter"));
    filters.add(filter);
    
    // Extend here
    extensionIndex = filters.size();

    filter = new StaticResourceHandler();
    filter.init(new BaseFilterConfig("Static Resource Filter"));
    filters.add(filter);
  }
}
