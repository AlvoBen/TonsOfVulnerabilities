package com.sap.engine.core.session;

import com.sap.engine.frame.cluster.message.MessageAnswer;

import java.io.*;
import java.util.Properties;

/**
 * User: pavel-b
 * Date: 2006-8-17
 * Time: 16:52:54
 */
public class ClusterMessage implements Serializable {
  public static final String CONTEXT_PROP = "cid";
  public static final String DOMAIN_PROP = "did";
  public static final String SERVER_PROP = "sid";
  public static final String SESSION_ID_PROP = "jid";

  public static final byte ANSWER_OK = 88;
  public static final byte ANSWER_FAILED = 77;

  public static final byte TYPE_CREATE = 0;

  private Properties props = new Properties();

  public ClusterMessage(Properties props) {
    this.props = props;
  }

  public Properties getProps() {
    return props;
  }

  public ClusterMessage(byte[] propBytes, int offset, int len) throws ClassNotFoundException, IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(propBytes, offset, len);
    ObjectInputStream ois = new ObjectInputStream(bais);
    props = (Properties)ois.readObject();
    ois.close();
    bais.close();
  }

  public byte[] getMessageBytes() throws java.io.IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(props);
    oos.close();
    baos.close();

    return baos.toByteArray();
  }

  public static boolean checkIfAnswerOK(MessageAnswer answer) {
    return !(answer == null ||
            answer.getMessage().length == 0 ||
            answer.getMessage()[0] != ANSWER_OK);

  }

}
