package com.example.bll;

import com.example.dal.StudentDAL;
import com.example.model.Student;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentBLLTest {

    private StudentDAL mockDal;
    private StudentBLL bll;
    private boolean dbConnected = false;

    @BeforeAll
    public void connectDatabase() {
        System.out.println("=== Connecting to Database (Simulated) ===");
        dbConnected = true;
        assertTrue(dbConnected, "Database should be connected before tests start");
    }

    @AfterAll
    public void disconnectDatabase() {
        dbConnected = false;
        System.out.println("=== Database Disconnected (Simulated) ===");
        assertFalse(dbConnected, "Database should be disconnected after tests end");
    }

    @BeforeEach
    public void setUp() {
        mockDal = mock(StudentDAL.class);
        bll = new StudentBLL(mockDal);
        assertTrue(dbConnected, "Database should be connected before each test");
    }

    @Test
    public void testCount_and_Avg_whenDalReturnsNull() {
        when(mockDal.fetchAllStudents()).thenReturn(null);
        assertEquals(0, bll.countStudents());
        assertEquals(0.0, bll.calcAvgCGPA(), 1e-9);
        verify(mockDal, times(2)).fetchAllStudents();
    }

    @Test
    public void testCount_and_Avg_whenDalReturnsEmptyList() {
        when(mockDal.fetchAllStudents()).thenReturn(Collections.emptyList());
        assertEquals(0, bll.countStudents());
        assertEquals(0.0, bll.calcAvgCGPA(), 1e-9);
        verify(mockDal, times(2)).fetchAllStudents();
    }

    @Test
    public void testCount_and_Avg_singleStudent() {
        List<Student> list = Collections.singletonList(new Student(1, "R001", "Ali", 3.5));
        when(mockDal.fetchAllStudents()).thenReturn(list);
        assertEquals(1, bll.countStudents());
        assertEquals(3.5, bll.calcAvgCGPA(), 1e-9);
    }

    @Test
    public void testAvg_multipleStudents_boundaryCgpa() {
        List<Student> list = Arrays.asList(
                new Student(1, "R010", "A", 0.0),
                new Student(2, "R011", "B", 4.0)
        );
        when(mockDal.fetchAllStudents()).thenReturn(list);
        assertEquals(2, bll.countStudents());
        assertEquals(2.0, bll.calcAvgCGPA(), 1e-9);
    }

    @Test
    public void testAvg_throwsForInvalidCgpa() {
        List<Student> list = Arrays.asList(
                new Student(1, "R020", "C", 3.0),
                new Student(2, "R021", "D", -1.0)
        );
        when(mockDal.fetchAllStudents()).thenReturn(list);
        assertEquals(2, bll.countStudents());
        assertThrows(IllegalArgumentException.class, () -> bll.calcAvgCGPA());
    }

    @Test
    public void testAvg_listContainsNullStudentElements() {
        List<Student> list = Arrays.asList(
                null,
                new Student(2, "R030", "E", 3.0)
        );
        when(mockDal.fetchAllStudents()).thenReturn(list);
        assertEquals(2, bll.countStudents());
        assertEquals(1.5, bll.calcAvgCGPA(), 1e-9);
    }
}
