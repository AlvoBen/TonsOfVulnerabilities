package com.sap.sdo.impl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class OutputToInputStream extends ByteArrayOutputStream {
    public InputStream getInputStream() {
        return new ByteArrayInputStream(buf, 0, count);
    }
}

