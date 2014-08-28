///*******************************************************************************
// * $Novell: StartTLS.java,v 1.7 2003/08/21 11:56:19 $
// * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
// *
// * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
// * TREATIES. USE AND REDISTRIBUTION OF THIS WORK IS SUBJECT TO THE LICENSE
// * AGREEMENT ACCOMPANYING THE SOFTWARE DEVELOPMENT KIT (SDK) THAT CONTAINS
// * THIS WORK. PURSUANT TO THE SDK LICENSE AGREEMENT, NOVELL HEREBY GRANTS TO
// * DEVELOPER A ROYALTY-FREE, NON-EXCLUSIVE LICENSE TO INCLUDE NOVELL'S SAMPLE
// * CODE IN ITS PRODUCT. NOVELL GRANTS DEVELOPER WORLDWIDE DISTRIBUTION RIGHTS
// * TO MARKET, DISTRIBUTE, OR SELL NOVELL'S SAMPLE CODE AS A COMPONENT OF
// * DEVELOPER'S PRODUCTS. NOVELL SHALL HAVE NO OBLIGATIONS TO DEVELOPER OR
// * DEVELOPER'S CUSTOMERS WITH RESPECT TO THIS CODE.
// *
// * $name:           StartTLS.java
// * $description:    The StartTLS.java sample shows how to set up a secure
// *                  TLS connection.  This sample needs the path to a keystore
// *                  where a certificate of the server is stored.  The sample
// *                  TLSTrustManager can create a keystore for you.  For
// *                  information on manually creating a keystore, see the keytool
// *                  documentation of a Java Developer Kit.
// *
// *                  Note: eDirectory 8.7 and above support TLS.
// ******************************************************************************/
//import com.novell.ldap.*;
//import java.security.Security;
//import java.io.UnsupportedEncodingException;
//
//public class StartTLS
//{
//    public static void main( String[] args )
//    {
//        if (args.length != 4) {
//           System.err.println("Usage:   java StartTLS <host>"
//                            + " <login dn> <password> <keystore_path>");
//           System.err.println("Example: java StartTLS nldap.novell.com"
//                            + " \"cn=user,o=novell\" password "
//                            + "/keystorePath/ssl.keystore");
//           System.exit(1);
//        }
//
//        int ldapPort = LDAPConnection.DEFAULT_PORT;
//        int ldapVersion = LDAPConnection.LDAP_V3;
//        String ldapHost = args[0];
//        String loginDN = args[1];
//        String password = args[2];
//        String path = args[3];
//        LDAPSocketFactory ssf;
//
//        try {
//
//            // Dynamically set Sun as the security provider
//            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//
//            // Dynamically set the property that JSSE uses to identify
//            // the keystore that holds trusted root certificates
//            System.setProperty("javax.net.ssl.trustStore", path);
//
//            // Set the socket factory for this connection
//            // Note: the socket factory can also set for all connections.
//            LDAPConnection lc = new LDAPConnection(
//                new LDAPJSSEStartTLSFactory());
//
//            // connect to the server: Anonymous authentication, clear text
//            lc.connect( ldapHost, ldapPort );
//
//            // secure the connection: Anonymous, encrypted text
//            lc.startTLS();
//
//            System.out.println( "Successful startTLS");
//
//            // bind to the server: Authenticated, encrypted text
//            lc.bind(ldapVersion,loginDN, password.getBytes("UTF8"));
//
//            System.out.println( "Successful secured bind with server.");
//
//            lc.stopTLS();
//
//            System.out.println( "Successful stopTLS.");
//
//            lc.disconnect();
//        }
//        catch( LDAPException e ) {
//            System.out.println("Error: " + e.toString());
//        }
//        catch( UnsupportedEncodingException e ) {
//            System.out.println( "Error: " + e.toString() );
//        }
//    }
//}
