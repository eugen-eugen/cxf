package cfxws;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService
public class MyWebService  {

	@Resource
	WebServiceContext wsc;

	@WebMethod
	public String hello(String arg0) throws MyException{
		throw new MyException();
		//return "Hello "+wsc.getUserPrincipal().getName();
	}

}
