package com.sap.bc.cts.tp.net;

import java.io.*;
/**
 * Title:        SemaphoreServer
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Ralf-Juergen Hauck
 * @version 1.0
 */

public interface ExtendedServiceIF extends Service{
    public void serve(NetComm nc) throws InterruptedIOException,IOException;
}