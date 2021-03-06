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

package com.sun.enterprise.tools.verifier.tests.ejb;

import com.sun.enterprise.deployment.EjbSessionDescriptor;
import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;

import java.util.logging.Level;

/**
 * Bean type test.  
 * The bean provider must use the appropriate session or entity element 
 * to declare the bean type.
 */
public class EjbBeanType extends EjbTest implements EjbCheck {


    /**
     * Bean Type Test.
     * The bean provider must use the appropriate session or entity element 
     * to declare the bean type.
     *   
     * @param descriptor the Enterprise Java Bean deployment descriptor   
     *
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

        Result result = getInitializedResult();
        ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

        if ((descriptor instanceof EjbSessionDescriptor) ||
                (descriptor instanceof EjbEntityDescriptor)) {

            try {
                Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());
                boolean validBean = false;
                // walk up the class tree (bug #4243606)
                do {
                    Class[] interfaces = c.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        logger.log(Level.FINE, getClass().getName() + ".debug1",
                                new Object[] {interfaces[i].getName()});

                        if (interfaces[i].getName().equals("javax.ejb.EntityBean") &&
                                (descriptor instanceof EjbEntityDescriptor)) {
                            validBean = true;
                            result.addGoodDetails(smh.getLocalString
                                    ("tests.componentNameConstructor",
                                            "For [ {0} ]",
                                            new Object[] {compName.toString()}));
                            result.passed(smh.getLocalString
                                    (getClass().getName() + ".passed",
                                            "[ {0} ] properly implements the {1}Bean interface.",
                                            new Object[] {descriptor.getEjbClassName(),"Entity"}));
                            break;
                        } else if (interfaces[i].getName().equals("javax.ejb.SessionBean") &&
                                descriptor instanceof EjbSessionDescriptor) {
                            validBean = true;
                            result.addGoodDetails(smh.getLocalString
                                    ("tests.componentNameConstructor",
                                            "For [ {0} ]",
                                            new Object[] {compName.toString()}));
                            result.passed(smh.getLocalString
                                    (getClass().getName() + ".passed",
                                            "[ {0} ] properly implements the {1}Bean interface.",
                                            new Object[] {descriptor.getEjbClassName(),"Session"}));
                            break;
                        }
                    }
                } while ((((c=c.getSuperclass()) != null) && (!validBean)));


                if (!validBean){
                    result.addErrorDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                                    "For [ {0} ]",
                                    new Object[] {compName.toString()}));
                    result.failed(smh.getLocalString
                            (getClass().getName() + ".failed",
                                    "Error: [ {0} ] is not a valid bean. The bean provider must use the appropriate {1} or {2} element to declare the bean type.",
                                    new Object[] {descriptor.getEjbClassName(),"session","entity"}));
                }
            } catch (ClassNotFoundException e) {
                Verifier.debug(e);
                result.addErrorDetails(smh.getLocalString
                        ("tests.componentNameConstructor",
                                "For [ {0} ]",
                                new Object[] {compName.toString()}));
                result.failed(smh.getLocalString
                        (getClass().getName() + ".failedException",
                                "Error: [ {0} ] class not found.",
                                new Object[] {descriptor.getEjbClassName()}));
            }
            return result;

        } else {
            result.addNaDetails(smh.getLocalString
                    ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
            result.notApplicable(smh.getLocalString
                    (getClass().getName() + ".notApplicable",
                            "[ {0} ] not called with a Session or Entity Bean.",
                            new Object[] {getClass()}));
            return result;
        }
    }
}
