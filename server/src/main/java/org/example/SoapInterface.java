package org.example;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import java.util.ArrayList;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SoapInterface {

    @WebMethod
    Object[][] findByProducer(String producer);
    @WebMethod
    Object[][] findByMatrix(String matrix);
    @WebMethod
    Object[][] findByAspectRatio(String proportion);
    @WebMethod
    void writeToDatabase(String[] sqls);
}
