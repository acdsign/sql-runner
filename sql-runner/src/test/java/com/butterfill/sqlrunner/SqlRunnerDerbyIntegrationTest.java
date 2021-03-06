
package com.butterfill.sqlrunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Peter Butterfill
 */
@ContextConfiguration(locations = "classpath:derby/test-context-derby.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlRunnerDerbyIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    SqlRunnerFactory sqlRunnerFactory;

    @Autowired
    DataSource dataSource;

    public SqlRunnerDerbyIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Shows how to insert a row and get back primary key value generated by the DB.
     */
    @Test
    public void testRun_withCallbackHandler_returning() {
        if (!TestHelper.isDerbyDb(dataSource)) {
            return;
        }

        final List<Object> results = new ArrayList<Object>();

        final String studyName = "test study name";

        final SqlRunner instance = sqlRunnerFactory.newSqlRunner();

        instance.runFile("int-test-setup.sql");

        final SqlRunnerCallbackHandler callbackHandler = new SqlRunnerCallbackHandler() {

            public PreparedStatement prepareStatement(
                    Connection connection, SqlRunnerStatement sqlRunnerStatement)
                    throws SQLException {
                // tall the DB we want the value of study_id back
                PreparedStatement preparedStatement = connection.prepareStatement(
                        sqlRunnerStatement.getSql(), new String[]{"study_id"});
                // set the study name (1st parameter in the insert statement)
                preparedStatement.setString(1, studyName);
                return preparedStatement;
            }

            public void executeComplete(PreparedStatement preparedStatement, SqlRunnerStatement sqlRunnerStatement) throws SQLException {
                // get the generated values
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                // move to the 1st row - there will be only one row
                generatedKeys.next();
                // save the generated study_id value so we can access it outside the callback handler
                results.add(generatedKeys.getLong(1));

            }
        };

        instance.setCallbackHandler(null, callbackHandler);
        instance.run("INSERT INTO study(study_name) VALUES(?)");
        assertEquals(1L, results.get(0));

    }


}
