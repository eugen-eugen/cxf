package cxf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

import cfxws.MyException_Exception;
import cfxws.MyWebService;
import net.webservicex.GlobalWeather;
import net.webservicex.GlobalWeatherSoap;

public class Main {

	public static void main1(String[] args) throws MalformedURLException, ClassNotFoundException {
		Class.forName("org.apache.cxf.service.factory.ServiceConstructionException");
		URL wsdlURL = new URL("http://www.webservicex.com/globalweather.asmx?WSDL");
		QName SERVICE_NAME = new QName("http://www.webserviceX.NET", "GlobalWeather");
		Service service = Service.create(wsdlURL, SERVICE_NAME);
		GlobalWeatherSoap weather = service.getPort(GlobalWeatherSoap.class);
		String cities = weather.getCitiesByCountry("Germany");
		System.out.println(cities);
		Iterator<QName> ports = service.getPorts();
		for (; ports.hasNext();) {
			System.out.println(ports.next().toString());
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, GeneralSecurityException {
		URL wsdlURL = Main.class.getResource("/MyWebService.wsdl");
		QName SERVICE_NAME = new QName("http://cfxws/", "MyWebServiceService");
		@SuppressWarnings("restriction")
		Service service = Service.create(wsdlURL, SERVICE_NAME);
		MyWebService myws = service.getPort(MyWebService.class);
		setupTLS(myws);
		String hello;
		try {
			hello = myws.hello("eugen");
			System.out.println(hello);
		} catch (MyException_Exception e) {
			System.out.println("cought "+e.getClass().getName());

		}
	}
	
    private static void setupTLS(MyWebService port) 
            throws FileNotFoundException, IOException, GeneralSecurityException {
            HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(port).getConduit();
            
            TLSClientParameters tlsCP = httpConduit.getTlsClientParameters();
            String keyPassword = "changeit";
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("C:\\install\\JBoss\\WF10\\wildfly-10.1.0.Final\\standalone\\configuration\\users.jks"), "changeit".toCharArray());
            KeyManager[] myKeyManagers = getKeyManagers(keyStore, keyPassword);
              tlsCP.setKeyManagers(myKeyManagers);
            tlsCP.setHostnameVerifier(new HostnameVerifier() {
				
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
     
            
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("C:\\install\\JBoss\\WF10\\wildfly-10.1.0.Final\\standalone\\configuration\\trustedlocalhost.jks"), "changeit".toCharArray());
            TrustManager[] myTrustStoreKeyManagers = getTrustManagers(trustStore);
            tlsCP.setTrustManagers(myTrustStoreKeyManagers);
            
            httpConduit.setTlsClientParameters(tlsCP);
        }
	
    private static KeyManager[] getKeyManagers(KeyStore keyStore, String keyPassword) 
            throws GeneralSecurityException, IOException {
            String alg = KeyManagerFactory.getDefaultAlgorithm();
            char[] keyPass = keyPassword != null
                         ? keyPassword.toCharArray()
                         : null;
            KeyManagerFactory fac = KeyManagerFactory.getInstance(alg);
            fac.init(keyStore, keyPass);
            return fac.getKeyManagers();
        }
    

    private static TrustManager[] getTrustManagers(KeyStore trustStore) 
        throws NoSuchAlgorithmException, KeyStoreException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory fac = TrustManagerFactory.getInstance(alg);
        fac.init(trustStore);
        return fac.getTrustManagers();
    }
    }
