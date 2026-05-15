package mn.edu.num.students;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class StudentEndpoint {
    private static final String NAMESPACE_URI = "http://num.edu.mn/students";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStudentRequest")
    @ResponsePayload
    public GetStudentResponse getStudent(@RequestPayload GetStudentRequest request) {
        GetStudentResponse response = new GetStudentResponse();
        Student student = new Student();
        
        int id = request.getId();
        student.setId(id);

        if (id == 101) {
            student.setFirstName("Bat-Erdene");
            student.setLastName("Student");
            student.setGpa(3.8);
        } else if (id == 102) {
            student.setFirstName("Sarnai");
            student.setLastName("Student");
            student.setGpa(3.9);
        } else {
            student.setFirstName("Unknown");
            student.setLastName("Unknown");
            student.setGpa(0.0);
        }

        response.setStudent(student);
        return response;
    }
}