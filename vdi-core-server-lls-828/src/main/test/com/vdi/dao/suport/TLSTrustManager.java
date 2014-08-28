///*******************************************************************************
// * $Novell: TLSTrustManager.java,v 1.6 2003/08/21 11:56:19 $
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
// * $name:           TLSTrustManager.java
// *
// * $description:    The TLSTrustManager.java sample shows how to set up a JSSE
// *                  SSL connection and implement an SSLContext trust manager
// *                  (X509TrustManager). This trust manager is implemented as a
// *                  sub_class called MyTrustManager.MyTrustManager will search
// *                  the keystore for an entry that matches the certificate
// *                  chain. If a match is found the chain is trusted. If no match
// *                  is found the user will be queried with information about the
// *                  unknown chain, to see if the user wants to trust it. If
// *                  trusted the unknown chain is added to the keystore and the
// *                  connection succeeds. If not the connection fails.
// *
// *                  The reason that someone would implement their own trust
// *                  manager is to allow human interaction to decide weather a
// *                  certificate chain should be trusted or not. The default
// *                  trust manager in the SSLContext does not allow this.
// *
// *                  The keystore is the JSSE trusted certificate store.
// *
// *                  See: http://java.sun.com/j2se/1.4/docs/guide/security/jsse/
// *                  JSSERefGuide.html for information on JSSE, SSLContext, and
// *                  trust managers (X509TrustManager).
// *
// *       Note:
// *       eDirectory 8.7 and above support TLS.
// *
// *       This sample uses objects specific to Sun's JSSE provider.
// *       This provider is included with JDK1.4, otherwise you must obtain the
// *       JSSE package from Sun.  This sample must be used with version 1.7 or
// *       later of the Java LDAP NDK.
// *
// ******************************************************************************/
//import com.novell.ldap.LDAPJSSESecureSocketFactory;
//import com.novell.ldap.LDAPConnection;
//import com.novell.ldap.LDAPException;
//import com.sun.net.ssl.SSLContext;
//import com.sun.net.ssl.TrustManager;
//import com.sun.net.ssl.X509TrustManager;
//import com.sun.net.ssl.TrustManagerFactory;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.security.Security;
//import java.security.KeyStore;
//import java.security.cert.X509Certificate;
//import java.util.Enumeration;
//import java.io.UnsupportedEncodingException;
//
//public class TLSTrustManager
//{
//    public static void main( String[] args )
//    {
//        try
//        {
//            // Check for valid arguments
//            if (args.length != 3 && args.length != 5)
//            {
//                System.err.println("Usage:   java TLSTrustManager <host name>"
//                                 + " <login dn> <password>"
//                                 + " <keystore path> <keystore password>");
//                System.err.println("*Note: If <keystore path> does not"
//                                 + " exist it will be created if the untrusted"
//                                 + " certificate chain is trusted by the user."
//                                 );
//                System.err.println("Example: java TLSTrustManager Acme.com"
//                                 + " \"cn=Admin,o=Acme\" secret my.keystore"
//                                 + " mykeypass");
//                System.exit(1);
//            }
//
//            String ldapHost         = args[0];
//            String loginDN          = args[1];
//            String password         = args[2];
//            String keyStorePath     = args[3];
//            char[] keyStorePassword = args[4].toCharArray();
//
//            //dynamically set SunJSSE as a security provider
//            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//
//            // Open the keystore file.
//            FileInputStream keyStoreIStream = null;
//            try
//            {
//                // Open the stream to read in the keystore.
//                keyStoreIStream = new FileInputStream(keyStorePath);
//            }
//            catch( FileNotFoundException e )
//            {
//                // If the path does not exist then a null stream means
//                // the keystore is initialized empty. If an untrusted
//                // certificate chain is trusted by the user, then it will be
//                // saved in the file pointed to by keyStorePath.
//                keyStoreIStream = null;
//            }
//            // Create a KeyStore Object
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            // Init the Keystore with the contents of the keystore file.
//            // If the input stream is null the keystore is initialized empty.
//            keyStore.load(keyStoreIStream, keyStorePassword);
//
//            // Close keystore input stream
//            if(keyStoreIStream != null)
//            {
//                keyStoreIStream.close();
//                keyStoreIStream = null;
//            }
//
//            // Instantiate MyTrustManager that will handle server certificate
//            // chains.
//            TrustManager[] tms = {new MyTrustManager(keyStore,
//                                                     keyStorePath,
//                                                     keyStorePassword)};
//
//            // Create context based on a TLS protocol and a SunJSSE provider.
//            SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
//
//            // Initialize the SSL context with MyTrustManager.
//            // The first parameter - null means use the default key manager.
//            // The third parameter - null means use the default value
//            // "secure random".
//            context.init(null, tms, null);
//
//
//            // The socket factory must be set before the connection is made.
//            // Version 1.6 or later of the NDK must be used in order for this
//            // code to compile properly.
//            LDAPJSSESecureSocketFactory ssf =
//                  new LDAPJSSESecureSocketFactory( context.getSocketFactory() );
//
//            // Create an LDAP connection object. Pass in the secure socket.
//            LDAPConnection lc = new LDAPConnection(ssf);
//
//            // Connect to the server.
//            lc.connect( ldapHost, LDAPConnection.DEFAULT_SSL_PORT );
//
//            // Authenticate to the server
//            lc.bind( LDAPConnection.LDAP_V3, loginDN, password.getBytes("UTF8") );
//
//            // At this point you are connected with a secure connection.
//            System.out.println( "Successful bind using TLS connection.");
//
//            // Close the connection.
//            lc.disconnect();
//            System.out.println( "Disconnect");
//
//            System.exit(0);
//        }
//        catch( UnsupportedEncodingException e ) {
//            System.out.println( "Error: " + e.toString() );
//        }
//        catch( LDAPException ex )
//        {
//            System.out.println( "LDAP Error: " + ex.toString() );
//        }
//        catch( Exception e )
//        {
//            System.out.println( "main Error: " + e.toString() );
//            System.exit(1);
//        }
//    }
//
//    // MyTrustManager
//    // This sub_class implements the X509TrustManager interface. MyTrustManager
//    // trusts known certificate chains, and queries the user to approve unknown
//    // chains. It will add trusted chains to the keystore.
//
//    private static class MyTrustManager implements X509TrustManager
//    {
//        private KeyStore keyStore;
//        private String   keyStorePath;
//        private char[]   keyStorePassword;
//
//
//        // MyTrustManager constructor. Save off keyStore object along with
//        // the path to the keystore (keyStorePath) and it's password
//        // (keyStorePassword).
//        public MyTrustManager(KeyStore         keyStore,
//                              String           keyStorePath,
//                              char[]           keyStorePassword)
//        {
//            this.keyStore = keyStore;
//            this.keyStorePath = keyStorePath;
//            this.keyStorePassword = keyStorePassword;
//        }
//
//        // isClientTrusted checks to see if the chain is in the keyStore object.
//        // This is done with a call to isChainTrusted.
//        public boolean isClientTrusted(X509Certificate[] chain)
//        {
//            return isChainTrusted(chain);
//        }
//
//        // isServerTrusted checks to see if the chain is in the keyStore object.
//        // This is done with a call to isChainTrusted. If not it queries the
//        // user to see if the chain should be trusted and stored into the
//        // keyStore object. The keyStore is then saved in the file whose path
//        // keyStorePath
//        public boolean isServerTrusted(X509Certificate[] chain)
//        {
//            boolean trusted = false;
//            try
//            {
//                // See if the chain is in the keyStore object.
//                trusted = isChainTrusted(chain);
//
//                // If certificate is untrusted
//                if(!trusted)
//                {
//                    System.out.println("Untrusted Certificate chain:");
//                    for (int i = 0; i < chain.length; i++)
//                    {
//                        // display certificate chain information
//                        System.out.println("Certificate chain[" + i + "]:");
//                        System.out.println("Subject: "
//                                         + chain[i].getSubjectDN().toString());
//                        System.out.println("Issuer: "
//                                         + chain[i].getIssuerDN().toString());
//                    }
//                    // Ask the user if the certificate should be trusted.
//                    System.out.print("Trust this certificate chain and"
//                                   + " add it to the keystore? (y or n) ");
//                    int readStatus = System.in.read();
//                    if(readStatus == 'y'|| readStatus == 'Y')
//                    {
//                        // Trust the chain.
//                        trusted = true;
//                        // Add Chain to the keyStore.
//                        for (int i = 0; i < chain.length; i++)
//                        {
//                            keyStore.setCertificateEntry
//                                (chain[i].getIssuerDN().toString(), chain[i]);
//                        }
//                        System.out.println("Certificate chain to keystore.");
//                        // Save keystore to file.
//                        FileOutputStream keyStoreOStream =
//                            new FileOutputStream(keyStorePath);
//                        keyStore.store(keyStoreOStream, keyStorePassword);
//                        keyStoreOStream.close();
//                        keyStoreOStream = null;
//                        System.out.println("Keystore saved in " + keyStorePath);
//                    }
//                    else
//                        trusted = false;
//                }
//            }
//            catch( Exception e )
//            {
//                System.out.println( "isServerTrusted Error: " + e.toString() );
//                trusted = false;
//            }
//            return trusted;
//        }
//
//        // getAcceptedIssuers retrieves all of the certificates in the keyStore
//        // and returns them in an X509Certificate array.
//        public X509Certificate[] getAcceptedIssuers()
//        {
//            X509Certificate[] X509Certs = null;
//            try
//            {
//                // See how many certificates are in the keystore.
//                int numberOfEntry = keyStore.size();
//                // If there are any certificates in the keystore.
//                if(numberOfEntry > 0)
//                {
//                    // Create an array of X509Certificates
//                    X509Certs = new X509Certificate[numberOfEntry];
//
//                    // Get all of the certificate alias out of the keystore.
//                    Enumeration aliases = keyStore.aliases();
//
//                    // Retrieve all of the certificates out of the keystore
//                    // via the alias name.
//                    int i = 0;
//                    while (aliases.hasMoreElements())
//                    {
//                        X509Certs[i] =
//                            (X509Certificate)keyStore.
//                            getCertificate((String)aliases.nextElement());
//                        i++;
//                    }
//
//                }
//            }
//            catch( Exception e )
//            {
//                System.out.println( "getAcceptedIssuers Exception: "
//                                 + e.toString() );
//                X509Certs = null;
//            }
//            return X509Certs;
//        }
//
//        // isChainTrusted searches the keyStore for any certificate in the
//        // certificate chain.
//        private boolean isChainTrusted(X509Certificate[] chain)
//        {
//            boolean trusted = false;
//            try
//            {
//                // Start with the root and see if it is in the Keystore.
//                // The root is at the end of the chain.
//                for (int i = chain.length - 1; i >= 0; i-- )
//                {
//                    if (keyStore.getCertificateAlias(chain[i]) != null)
//                    {
//                        trusted = true;
//                        break;
//                    }
//                }
//            }
//            catch( Exception e )
//            {
//                System.out.println( "isChainTrusted Exception: "
//                                 + e.toString() );
//                trusted = false;
//            }
//            return trusted;
//        }
//    }
//}