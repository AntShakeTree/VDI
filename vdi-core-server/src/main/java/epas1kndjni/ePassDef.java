// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ePassDef.java

package epas1kndjni;


public class ePassDef
{

	public static final int FT_SUCCESS = 0;
	public static final int FT_CANNOT_OPEN_DRIVER = 1;
	public static final int FT_INVALID_DRVR_VERSION = 2;
	public static final int FT_INVALID_COMMAND = 3;
	public static final int FT_ACCESS_DENIED = 4;
	public static final int FT_ALREADY_ZERO = 5;
	public static final int FT_UNIT_NOT_FOUND = 6;
	public static final int FT_DEVICE_REMOVED = 7;
	public static final int FT_COMMUNICATIONS_ERROR = 8;
	public static final int FT_DIR_NOT_FOUND = 9;
	public static final int FT_FILE_NOT_FOUND = 10;
	public static final int FT_MEM_CORRUPT = 11;
	public static final int FT_INTERNAL_HW_ERROR = 12;
	public static final int FT_INVALID_RESP_SIZE = 13;
	public static final int FT_PIN_EXPIRED = 14;
	public static final int FT_ALREADY_EXISTS = 15;
	public static final int FT_NOT_ENOUGH_MEMORY = 16;
	public static final int FT_INVALID_PARAMETER = 17;
	public static final int FT_ALIGNMENT_ERROR = 18;
	public static final int FT_INPUT_TOO_LONG = 19;
	public static final int FT_INVALID_FILE_SELECTED = 20;
	public static final int FT_DEVICE_IN_USE = 21;
	public static final int FT_INVALID_API_VERSION = 22;
	public static final int FT_TIME_OUT_ERROR = 23;
	public static final int FT_ITEM_NOT_FOUND = 24;
	public static final int FT_COMMAND_ABORTED = 25;
	public static final int FT_INVALID_STATUS = 255;
	public static final int FT_LIBRARY_NOT_FOUND = 16385;
	public static final int FT_LIBRARY_OBSOLETE = 16386;
	public static final int FT_LIBRARY_MISMATCH = 16387;
	public static final int EPAS_ROOT_DIR = 0;
	public static final int EPAS_7816_ROOT_DIR = 16128;
	public static final int EPAS_AUTO_ID_BASE = 0xf0000;
	public static final int EPAS_INDEX_FILE = 65535;
	public static final int EPAS_TOKEN_NAME_FILE = 65534;
	public static final int EPAS_SYS_TYPE1 = 1;
	public static final int EPAS_APP_NAME_SIZE = 32;
	public static final int EPAS_FRIENDLY_NAME_SIZE = 32;
	public static final int EPAS_DIR_BY_ID = 256;
	public static final int EPAS_DIR_BY_LONG_ID = 512;
	public static final int EPAS_DIR_BY_NAME = 768;
	public static final int EPAS_DIR_BY_GUID = 1024;
	public static final int EPAS_DIR_BY_GUID_STR = 1280;
	public static final int EPAS_DIR_BY_MASK = 3840;
	public static final int EPAS_FILETYPE_UNUSED = 0;
	public static final int EPAS_FILETYPE_DIR = 1;
	public static final int EPAS_FILETYPE_DATA = 2;
	public static final int EPAS_FILETYPE_KEY = 4;
	public static final int EPAS_FILETYPE_MD5 = 4;
	public static final int EPAS_FILETYPE_TEA = 8;
	public static final int EPAS_FILETYPE_UNKNOWN = 255;
	public static final int EPAS_ACCESS_ANYONE = 0;
	public static final int EPAS_ACCESS_USER = 1;
	public static final int EPAS_ACCESS_OFFICER = 2;
	public static final int EPAS_ACCESS_NONE = 7;
	public static final int EPAS_ACCESS_READ = 1;
	public static final int EPAS_ACCESS_WRITE = 2;
	public static final int EPAS_ACCESS_CRYPT = 4;
	public static final int EPAS_CAPS_MD5HMAC = 1;
	public static final int EPAS_OPEN_NEXT = 0;
	public static final int EPAS_OPEN_FIRST = 1;
	public static final int EPAS_OPEN_CURRENT = 2;
	public static final int EPAS_OPEN_SPECIFIC = 3;
	public static final int EPAS_OPEN_BY_NAME = 256;
	public static final int EPAS_OPEN_BY_GUID = 512;
	public static final int EPAS_OPEN_BY_GUID_STR = 768;
	public static final int EPAS_DIR_FROM_MF = 0;
	public static final int EPAS_DIR_FROM_CUR_DF = 16;
	public static final int EPAS_DIR_TO_PARENT = 32;
	public static final int EPAS_FILE_READ = 16;
	public static final int EPAS_FILE_WRITE = 32;
	public static final int EPAS_FILE_CRYPT = 64;
	public static final int EPAS_CREATE_AUTO_ID = 0x10000;
	public static final int EPAS_DELETE_RECURSIVE = 0x10000;
	public static final int EPAS_VERIFY_USER_PIN = 0;
	public static final int EPAS_VERIFY_SO_PIN = 1;
	public static final int EPAS_VERIFY_NO_HASH = 4096;
	public static final int EPAS_VERIFY_HASH_UPIN = 8192;
	public static final int EPAS_CHANGE_USER_PIN = 0;
	public static final int EPAS_UNBLOCK_USER_PIN = 1;
	public static final int EPAS_CHANGE_SO_PIN = 2;
	public static final int EPAS_CHANGE_NO_HASH = 4096;
	public static final int EPAS_CHANGE_HASH_UPIN = 8192;
	public static final int EPAS_SCOPE_MF = 0;
	public static final int EPAS_SCOPE_DF = 1;
	public static final int EPAS_HASH_MD5_HMAC = 1;
	public static final int EPAS_PROP_CAPABILITIES = 0;
	public static final int EPAS_PROP_MEM_SIZE = 1;
	public static final int EPAS_PROP_ACCESSINFO = 3;
	public static final int EPAS_PROP_APP_NAME = 4;
	public static final int EPAS_PROP_APP_GUID = 5;
	public static final int EPAS_PROP_VERSIONINFO = 6;
	public static final int EPAS_PROP_SERNUM = 7;
	public static final int EPAS_PROP_LED_ON = 8;
	public static final int EPAS_PROP_LED_OFF = 9;
	public static final int EPAS_PROP_FRIENDLY_NAME = 11;
	public static final int EPAS_PROP_SYSINFO = 12;
	public static final int EPAS_API_VERSION = 256;
	public static final int EPAS_TEA_ENC = 1;
	public static final int EPAS_TEA_DEC = 2;

	public ePassDef()
	{
	}
}
