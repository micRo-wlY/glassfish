/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.security.wss.defprovider.servlet.taxcal.client;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

import javax.naming.*;
import javax.xml.rpc.Stub;


public class TaxCalClient {

	private static SimpleReporterAdapter stat = new SimpleReporterAdapter("appserv-tests");
	private static String stateTaxEndpoint = null;
	private static String fedTaxEndpoint = null;
	private static String testSuite = "Sec::Servlet_Based_WSS_test Encrypt then Sign";
	private static String testCase = null;
	private static TaxCalServletService taxCalService = null;

	public static void main (String[] args) {

		if(args[0] == null || args[1] == null){
			System.out.println("TaxCal client: Argument missing. Please provide target" +
				 "endpoint address as argument");
			System.exit(1);
		} else {
			stateTaxEndpoint = args[0];
			fedTaxEndpoint = args[1];
		}

		stat.addDescription(testSuite);
		try { 
			TaxCalClient client = new TaxCalClient();
			Context ic = new InitialContext();
			taxCalService = (TaxCalServletService)
				ic.lookup("java:comp/env/service/TaxCalServletService");

			client.callStateTaxService();
			client.callFedTaxService();
			stat.addStatus(testSuite, stat.PASS);
		}catch(Exception e){
			stat.addStatus(testSuite, stat.FAIL);
			e.printStackTrace();
		}

		stat.printSummary(testSuite);
    }
    
	public void callStateTaxService() {
		double income = 85000.00;
		double deductions = 5000.00;

		//String targetEndpointAddress =
		//	"http://localhost:1024/taxcalculator";

		try {

			StateTaxIF taxCalIFPort = taxCalService.getStateTaxIFPort();

			((Stub)taxCalIFPort)._setProperty (Stub.ENDPOINT_ADDRESS_PROPERTY,
				stateTaxEndpoint);

			double stateTax = taxCalIFPort.getStateTax(income, deductions);
			System.out.println("State tax from servlet based TaxCalService :" + stateTax);

			if(stateTax == 24000.00)
				stat.addStatus(testSuite + " StateTaxPort", stat.PASS);
			else
				stat.addStatus(testSuite + " StateTaxPort", stat.FAIL);

		} catch (Exception ex) {
			System.out.println("TaxCalEjbWebservice client failed");
			stat.addStatus(testSuite + " StateTaxPort", stat.FAIL);
			ex.printStackTrace();
		} 
	}

	public void callFedTaxService() {
		double income = 97000.00;
		double deductions = 7000.00;
		try {
			//String targetEndpointAddress =
			//"http://localhost:1024/FindInterestServlet/FindInterest";

			FedTaxIF taxCalIFPort = taxCalService.getFedTaxIFPort();
			((Stub)taxCalIFPort)._setProperty (Stub.ENDPOINT_ADDRESS_PROPERTY,
				fedTaxEndpoint);

			double fedTax = taxCalIFPort.getFedTax(income, deductions);
			System.out.println("Fed tax from Servlet based TaxCalService :" + fedTax);

			if(fedTax == 18000.00)
				stat.addStatus(testSuite + " FedTaxPort", stat.PASS);
			else
				stat.addStatus(testSuite + " FedTaxPort", stat.FAIL);
                
		} catch (Exception ex) {
			System.out.println("TaxCalServletWebService client failed");
			stat.addStatus(testSuite + " FedTaxPort", stat.FAIL);
			ex.printStackTrace();
		} 
	}
}

