package com.vdi.common;

/**
 * Error code handler.
 */
public class Errors
{

  private static final int MAJOR_TO_MINOR_OFFSET = 4;

  private static final int SEVERITY_OFFSET = 30;
  private static final int SYSTEM_FLAG_BIT_OFFSET = 29;
  private static final int RESERVED_BIT_OFFSET = 28;
  private static final int ROOT_MODULE_OFFSET = 20;
  private static final int END_MODULE_OFFSET = 12;

  private static final int SYSTEM_FLAG_VALUE = 1;
  
  private static final int RESERVED_BIT_VALUE = 0;

  private static final int SEVERITY_INFORMATION = 1;
  private static final int SEVERITY_WARNING = 2;
  private static final int SEVERITY_ERROR = 3;

  private final int module;

  private Errors(final int module)
  {
    this.module = module;
  }
  
  /**
   * 
   * Factory method.
   * 
   * @param module Code of the module.
   * @return Created instance.
   */
  public static Errors newInstance(final int module)
  {
    return new Errors(module);
  }
  
  /**
   * 
   * Factory method.
   * 
   * @param majorModule Code of the major module.
   * @param minorModule Code of the minor module.
   * @return Created instance.
   */
  public static Errors newInstance(final int majorModule, final int minorModule)
  {
    return new Errors((majorModule << MAJOR_TO_MINOR_OFFSET) + minorModule);
  }
  
  /**
   * Wrapping the error code with the severity of 'Information'.
   * 
   * @param errorCode Error code.
   * @return Wrapped error code.
   */
  public int info(final int errorCode)
  {
    return wrap(SEVERITY_INFORMATION, errorCode);
  }

  /**
   * Wrapping the error code with the severity of 'Warning'.
   * 
   * @param errorCode Error code.
   * @return Wrapped error code.
   */
  public int warn(final int errorCode)
  {
    return wrap(SEVERITY_WARNING, errorCode);
  }
  
  /**
   * Wrapping the error code with the severity of 'Error'.
   * 
   * @param errorCode Error code.
   * @return Wrapped error code.
   */
  public int error(final int errorCode)
  {
    return wrap(SEVERITY_ERROR, errorCode);
  }
  
  /**
   * Unwrapping the error code.
   * 
   * @param errorCode Error code.
   * @return Unwrapped error code.
   */
  public int unwrap(final int errorCode)
  {
    return errorCode & 0xFFF;
  }
  
  /**
   * Indicating if the error code has the severity of 'Information'.
   * 
   * @param errorCode Error code.
   * @return [true] if the error code has the severity of 'Information'.
   */
  public boolean isInformation(final int errorCode)
  {
    return checkSeverity(errorCode, SEVERITY_INFORMATION);
  }

  /**
   * Indicating if the error code has the severity of 'Warning'.
   * 
   * @param errorCode Error code.
   * @return [true] if the error code has the severity of 'Warning'.
   */
  public boolean isWarning(final int errorCode)
  {
    return checkSeverity(errorCode, SEVERITY_WARNING);
  }
  
  /**
   * Indicating if the error code has the severity of 'Error'.
   * 
   * @param errorCode Error code.
   * @return [true] if the error code has the severity of 'Error'.
   */
  public boolean isError(final int errorCode)
  {
    return checkSeverity(errorCode, SEVERITY_ERROR);
  }
  
  /**
   * Getting the root major module code.
   * 
   * @param errorCode Error code.
   * @return Root major module code.
   */
  public int getRootMajorModule(final int errorCode)
  {
    return (errorCode >>> (MAJOR_TO_MINOR_OFFSET + ROOT_MODULE_OFFSET)) & 0xF;
  }
  
  /**
   * Getting the root minor module code.
   * 
   * @param errorCode Error code.
   * @return Root minor module code.
   */
  public int getRootMinorModule(final int errorCode)
  {
    return (errorCode >>> ROOT_MODULE_OFFSET) & 0xF;
  }
  
  /**
   * Getting the end major module code.
   * 
   * @param errorCode Error code.
   * @return End major module code.
   */
  public int getEndMajorModule(final int errorCode)
  {
    return (errorCode >>> (MAJOR_TO_MINOR_OFFSET + END_MODULE_OFFSET)) & 0xF;
  }
  
  /**
   * Getting the end minor module code.
   * 
   * @param errorCode Error code.
   * @return End minor module code.
   */
  public int getEndMinorModule(final int errorCode)
  {
    return (errorCode >>> END_MODULE_OFFSET) & 0xF;
  }
  
  private int wrap(final int severity, final int errorCode)
  {
    if (errorCode == 0)
    {
      return errorCode;
    }
    int finalErrorCode = 0;
    finalErrorCode |= severity << SEVERITY_OFFSET;
    finalErrorCode |= SYSTEM_FLAG_VALUE << SYSTEM_FLAG_BIT_OFFSET;
    finalErrorCode |= RESERVED_BIT_VALUE << RESERVED_BIT_OFFSET;
    if ((errorCode & (0xFF << ROOT_MODULE_OFFSET)) == 0)
    {
      finalErrorCode |= module << ROOT_MODULE_OFFSET;
    }
    finalErrorCode |= module << END_MODULE_OFFSET;
    finalErrorCode |= errorCode;
    return finalErrorCode;
  }
  
  private boolean checkSeverity(int errorCode, int severity)
  {
    return (errorCode >>> SEVERITY_OFFSET) == severity;
  }

}
