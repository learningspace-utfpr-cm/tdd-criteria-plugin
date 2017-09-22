package net.bhpachulski.tddcriteria.service.impl;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import net.bhpachulski.tddcriteria.dao.TddCriteriaDAO;
import net.bhpachulski.tddcriteria.model.FileType;
import net.bhpachulski.tddcriteria.model.Student;
import net.bhpachulski.tddcriteria.model.StudentFile;
import net.bhpachulski.tddcriteria.model.TDDStage;

/**
 *
 * @author bhpachulski
 */
@Path("/tddCriteriaService")
public class TDDCriteriaService {
    
    private TddCriteriaDAO dao;

    public TDDCriteriaService() throws SQLException {
        dao = new TddCriteriaDAO();
        dao.init();
    }
    
    @POST
    @Path("/addStudent")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Student insertAluno(Student student) throws SQLException {
        return dao.insertOrGetExistingStudent(student);
    }
    
    @POST
    @Path("/addStudentFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public StudentFile addStudentFile(
            @FormDataParam("studentId") int studentId,
            @FormDataParam("fileType") int fileType,
            @FormDataParam("projectName") String projectName,
            @FormDataParam("fileName") String fileName,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("tddStage") String tddStage) throws SQLException {

        StudentFile sf = new StudentFile();
        sf.setStudentId(studentId);
        sf.setFileIs(uploadedInputStream);
        sf.setType(FileType.getFileType(fileType));
        sf.setFileName(fileName);
        sf.setProjectName(projectName);
        sf.setStage(TDDStage.getStageByString(tddStage));
        
        return dao.insertStudentFile(sf);
    }
    
    @GET
    @Path("/allFiles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StudentFile> getAllFiles () throws SQLException, ParseException {
        return dao.getAllFiles();
    }
}