package net.bhpachulski.tddcriteria.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.bhpachulski.tddcriteria.model.FileType;
import net.bhpachulski.tddcriteria.model.Student;
import net.bhpachulski.tddcriteria.model.StudentFile;
import net.bhpachulski.tddcriteria.model.TDDStage;
import net.bhpachulski.tddcriteria.util.PropertiesUtils;

/**
 *
 * @author bhpachulski
 */
public class TddCriteriaDAO {

    private String dbURL = "jdbc:derby:tddCriteriaDB/db;create=true";
    private Properties sqlProperties;
    private List<String> REQUIRED_TABLES;
    private Connection conn;

    public TddCriteriaDAO() throws SQLException {
        if (conn == null) {
            conn = DriverManager.getConnection(this.dbURL);
        }

        sqlProperties = new PropertiesUtils().loadProperties("SQL.properties");
        REQUIRED_TABLES = Arrays.asList(sqlProperties.getProperty("SQL_TABLES").split(";"));
    }

    public void init() throws SQLException {
        String SQL_CREATE_FILES = sqlProperties.getProperty("SQL_CREATE_FILES");
        String SQL_CREATE_STUDENTS = sqlProperties.getProperty("SQL_CREATE_STUDENTS");

        if (!getAllTables().containsAll(REQUIRED_TABLES)) {
            conn.prepareStatement(SQL_CREATE_STUDENTS).execute();
            conn.prepareStatement(SQL_CREATE_FILES).execute();
        }

    }

    public List<String> getAllTables() throws SQLException {
        List<String> tables = new ArrayList<String>();
        DatabaseMetaData dbmd = conn.getMetaData();

        ResultSet rs = dbmd.getTables("tddCriteriaDB", null, null, null);
        while (rs.next()) {
            tables.add(rs.getString(3));
        }

        return tables;
    }

    public StudentFile insertStudentFile(StudentFile sf) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO FILES (idStudent, file, typeID, fileName, projectName, sentIn, tddStage) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, sf.getStudentId());
        ps.setBinaryStream(2, sf.getFileIs());
        ps.setInt(3, sf.getType().getId());
        ps.setString(4, sf.getFileName());
        ps.setString(5, sf.getProjectName());
        ps.setString(6, sf.getStage().toString());
        ps.execute();
        
        ResultSet rsKey = ps.getGeneratedKeys();
        rsKey.next();
        
        sf.setId(rsKey.getInt(1));
        sf.setFileIs(null);
        
        return sf;
    }
    
    public Student findStudentByName (Student student) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM STUDENTS WHERE name = ?");
        ps.setString(1, student.getName());
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
            student.setId(rs.getInt("id"));
        
        return student;
    }
    
    public Student insertOrGetExistingStudent (Student s) throws SQLException {
    
        Student student = this.findStudentByName(s);
        
        if (student.getId() == null) {
            return this.insertStudent(s);
        } else {
            return student;
        }
    }

    public Student insertStudent(Student student) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO STUDENTS (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, student.getName());
        ps.execute();
        
        ResultSet rsKey = ps.getGeneratedKeys();
        rsKey.next();
        
        student.setId(rsKey.getInt(1));
        
        return student;
    }

    public List<Student> getAllStudents() throws SQLException {

        List<Student> students = new ArrayList<Student>();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM STUDENTS");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Student student = new Student();
            student.setId(rs.getInt("id"));
            student.setName(rs.getString("name"));

            students.add(student);
        }

        return students;
    }

    public List<StudentFile> getAllFiles() throws SQLException, ParseException {

        List<StudentFile> studentFiles = new ArrayList<StudentFile>();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM FILES");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            StudentFile studentFile = new StudentFile();
            studentFile.setId(rs.getInt("id"));
            studentFile.setStudentId(rs.getInt("idStudent"));
            studentFile.setSentIn(new Date(rs.getTimestamp("sentIn").getTime()));
            studentFile.setType(FileType.getFileType(rs.getInt("typeID")));
            studentFile.setFileName(rs.getString("fileName"));
            studentFile.setProjectName(rs.getString("projectName"));
            studentFile.setStage(TDDStage.getStageByString(rs.getString("tddStage")));
            
            studentFiles.add(studentFile);
        }

        return studentFiles;
    }

    public void exportDB() throws SQLException {

        String SQL_EXPORT = sqlProperties.getProperty("SQL_EXPORT");
        
        PreparedStatement ps = conn.prepareStatement(SQL_EXPORT);
        ps.setString(1, null);
        ps.setString(2, "STUDENTS");
        ps.setString(3, "students.dat");
        ps.setString(4, "%");
        ps.setString(5, null);
        ps.setString(6, null);
        ps.execute();
        
        ps = conn.prepareStatement(SQL_EXPORT);
        ps.setString(1, null);
        ps.setString(2, "FILES");
        ps.setString(3, "files.dat");
        ps.setString(4, "%");
        ps.setString(5, null);
        ps.setString(6, null);
        ps.execute();

    }

    public void close() throws SQLException {
        conn.close();
    }

}
