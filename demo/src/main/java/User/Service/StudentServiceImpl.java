package User.Service;

import User.DTO.Request.StudentUpdateRequest;
import User.DTO.Response.StudentProfileResponse;
import User.DTO.Response.UserResponse;
import User.Entity.StudentProfile;
import User.Repository.StudentProfileRepository;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Override
    public StudentProfileResponse getMyProfile() {
        UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StudentProfile studentProfile = studentProfileRepository.findByUserId(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return mapToResponse(studentProfile);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateProfile(StudentUpdateRequest dto) {
        UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StudentProfile student = studentProfileRepository.findByUserId(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        // 2. Cập nhật các trường nếu không null (Tương ứng logic payload.field !== undefined trong TS)
        if (dto.getGradeLevel() != null) student.setGradeLevel(dto.getGradeLevel());
        if (dto.getGoals() != null) student.setGoals(dto.getGoals());
        if (dto.getPreferredSubjects() != null) student.setPreferredSubject(dto.getPreferredSubjects());
        if (dto.getNotes() != null) student.setNotes(dto.getNotes());

        // 3. Lưu và trả về kết quả
        return mapToResponse(studentProfileRepository.save(student));
    }

    private StudentProfileResponse mapToResponse(StudentProfile entity) {
        return StudentProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .gradeLevel(entity.getGradeLevel())
                .goals(entity.getGoals())
                .preferredSubjects(entity.getPreferredSubject())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
