// Copyright (c) 2018, Project Haystack Corporation
// Licensed under the Academic Free License version 3.0
//
// History:
//   15 Mar 2018  Stuart Longland  Creation
//
package nhaystack.test;

/**
 * TestCaseCommon: Common properties for all test cases.
 *
 * Rather than hard-coding credentials in several test cases, we'll define
 * these all in one place and inherit these in our test cases to prevent
 * us having to edit lots of lines of test cases.
 */
public class TestCore
{

//////////////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////////////////

    /** URI to the nhaystack_simple station */
    protected final String SIMPLE_URI = "http://localhost:82/haystack/";

    /** Valid username for nhaystack_simple station */
    protected final String SIMPLE_USER = "admin";

    /** Password of user for nhaystack_simple station */
    protected final String SIMPLE_PASS = "Vk3ldb237847";


    /** URI to the nhaystack_sup station */
    protected final String SUPERVISOR_URI = "http://127.0.0.1:20080/haystack/";

    /** Valid username for nhaystack_sup station */
    protected final String SUPERVISOR_USER = "admin";

    /** Password of user for nhaystack_sup station */
    protected final String SUPERVISOR_PASS = "ChangeMe123";


    /** Username that is not valid on either station */
    protected final String INVALID_USER = "idonotexist";

    /** Password for the invalid user */
    protected final String INVALID_PASS = "correct horse battery staple";
}
