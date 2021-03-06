/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package test.jdbc.jdbcusertx;
import org.testng.annotations.Configuration;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Simple TestNG client for basic WAR containing one JSP,one Servlet and one static
 *HTML resource.Each resources (HTML,JSP,Servlet) is invoked as a separate test.
 *
 */
public class JdbcUserTxTestNG {

    private static final String TEST_NAME =
        "jdbc-jdbcusertx";
   
    private String strContextRoot="jdbcusertx";

    static String result = "";
    String host=System.getProperty("http.host");
    String port=System.getProperty("http.port");
           
    /*
     *If two asserts are mentioned in one method, then last assert is taken in
     *to account.
     *Each method can act as one test within one test suite
     */


    @Test(groups ={ "pulse"} ) // test method
    public void testUserTx() throws Exception{
        
        try{

          String testurl = "http://" + host  + ":" + port + "/"+ 
	    strContextRoot + "/MyServlet?testcase=usertx";
	  URL url = new URL(testurl);
	  //echo("Connecting to: " + url.toString());
	  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	  conn.connect();
	  int responseCode = conn.getResponseCode();

	  InputStream is = conn.getInputStream();
	  BufferedReader input = new BufferedReader(new InputStreamReader(is));

	  String line = null;
	  boolean result=false;
	  String testLine = null;        
	  String EXPECTED_RESPONSE ="user-tx-commit:true";
	  String EXPECTED_RESPONSE2 ="user-tx-rollback:true";
	  while ((line = input.readLine()) != null) {
	    // echo(line);
            if(line.indexOf(EXPECTED_RESPONSE)!=-1 &&
               line.indexOf(EXPECTED_RESPONSE2)!=-1){
	      testLine = line;
	      //echo(testLine);
	      result=true;
	      break;
            }
	  }        
                
	  Assert.assertEquals(result, true,"Unexpected Results");
        
        }catch(Exception e){
	  e.printStackTrace();
	  throw new Exception(e);
        }

    }

    @Test(groups ={ "pulse"} ) // test method
    public void testNoLeak() throws Exception{
        
        try{

          String testurl = "http://" + host  + ":" + port + "/"+ 
	    strContextRoot + "/MyServlet?testcase=noleak";
	  URL url = new URL(testurl);
	  //echo("Connecting to: " + url.toString());
	  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	  conn.connect();
	  int responseCode = conn.getResponseCode();

	  InputStream is = conn.getInputStream();
	  BufferedReader input = new BufferedReader(new InputStreamReader(is));

	  String line = null;
	  boolean result=false;
	  String testLine = null;        
	  String EXPECTED_RESPONSE ="no-leak-test:true";
	  while ((line = input.readLine()) != null) {
	    // echo(line);
   	    if(line.indexOf(EXPECTED_RESPONSE)!=-1){
	      testLine = line;
	      //echo(testLine);
	      result=true;
	      break;
            }
	  }        
                
	  Assert.assertEquals(result, true,"Unexpected Results");
               
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    public static void echo(String msg) {
        System.out.println(msg);
    }

}
