package com.sap.engine.tools.offlinedeploy.rdb;

/**
 * This interface represents the status of deployment operation. Possible status are SUCCESS and WARNING.
 * In case of WARNING status a list with warnings can be get. The class provides other useful data like
 * runtime component name, deploy name, etc.
 */
public interface ResultStatus {

  //deployment complete successfull
  public static byte SUCCESS = 0;
  //deployment complete with warnings
  public static byte WARNING = 1;

  /**
   * Returns deploy status.
   * @return deploy status (success | warning).
   */
  public byte getStatus();

  /**
   * Returns warnings string array or null if no warning is set.
   * @return warnings if status=success returns null.
   */
  public String[] getWarnings();

  /**
   * Returns SDA archive path or null if not applicable
   *
   * @return path
   */
  public String getSDAPath();

  /**
   * Returns CSN component for this SDA.
   *
   * @return CSN component or null if not exist.
   */
  public String getCSNComponent();

  /**
   * Returns component runtime name for interfaces, libraries and services
   *
   * @return runtime name or null if not applicable.
   */
  public String getRuntimeName();

}