package scd_homework;

import static org.junit.jupiter.api.Assertions.*;

import com.bll.*;
import com.dal.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BLLFacadeTest {

    private Connection conn;
    private IStudent studentDAL;
    private IDALFacade dalFacade;
    private IStudentBO studentBO;
    private IBLLFacade bllFacade;

    @BeforeAll
    void setupAll() {
        System.out.println("🔹 Setting up database connection and objects...");

        // Initialize DB connection
        conn = DBConnectionDBC.getConnection();

        // Initialize DAL layer
        studentDAL = new Student(conn);
        dalFacade = new DALFacade(studentDAL);

        // Initialize BO layer
        studentBO = new StudentBO(dalFacade);

        // Initialize BLL Facade
        bllFacade = new BLLFacade(studentBO);
    }


    // Edge: countStudents()
    @Test
    void testCountStudents() {
    	Assertions.assertEquals(4,bllFacade.countStudents());
    }

    @Test
    void testCalAvgCgpa() {
    	Assertions.assertNotEquals(null,bllFacade.calcAvgCGPA());
    }
    @Test
    void testCalcAvgCgpa_NotZero() {
        // Ensure that the average CGPA is not zero for non-empty student table
        Assertions.assertNotEquals(0.0, bllFacade.calcAvgCGPA(), 
            "Average CGPA should not be zero when students exist");
    }

    @Test
    void testCalcAvgCgpa_CorrectValue() {
        // Check if the calculated average matches expected value (from 4 sample records)
        double expectedAvg = (3.5 + 3.8 + 2.9 + 3.2) / 4; // = 3.35
        Assertions.assertEquals(expectedAvg, bllFacade.calcAvgCGPA(), 0.001, 
            "Average CGPA should match expected value");
    }

}
