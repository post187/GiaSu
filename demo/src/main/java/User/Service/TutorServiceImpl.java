package User.Service;

import User.DTO.Request.TutorUpdateRequest;
import User.DTO.Request.VerificationSubmitRequest;
import User.DTO.Response.TutorProfileResponse;
import User.DTO.Response.UserResponse;
import User.Entity.TutorProfile;
import User.Entity.User;
import User.Mapper.TutorProfileMapper;
import User.Mapper.UserMapper;
import User.Repository.TutorProfileRepository;
import User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TutorServiceImpl implements TutorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TutorProfileMapper tutorProfileMapper;

    @Override
    public TutorProfileResponse getMyProfile() {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUser(user1);
        return tutorProfileMapper.toResponse(tutorProfile);
    }

    @Override
    public TutorProfileResponse updateProfile(TutorUpdateRequest request) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUser(user1);

        tutorProfile.setBio(request.getBio());
        tutorProfile.setEducation(request.getEducation());
        tutorProfile.setCity(request.getCity());
        tutorProfile.setCertificates(request.getCertificates());
        tutorProfile.setHourlyRateMax(request.getHourlyRateMax());
        tutorProfile.setHourlyRateMin(request.getHourlyRateMin());
        tutorProfile.setTeachingModes(request.getTeachingModes());
        tutorProfile.setDistrict(request.getDistrict());
        tutorProfile.setYearsOfExperience(request.getYearsOfExperience());

        return tutorProfileMapper.toResponse(tutorProfile);
    }

    @Override
    public TutorProfileResponse submitVerification(VerificationSubmitRequest request) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUser(user1);

        tutorProfile.setNationalIdNumber(request.getNationalIdNumber());
        tutorProfile.setVerificationSubmittedAt(LocalDateTime.now());
        tutorProfile.setNationalIdFrontImageUrl(request.getNationalIdFrontImageUrl());
        tutorProfile.setNationalIdBackImageUrl(request.getNationalIdBackImageUrl());
        tutorProfile.setProofDocuments(request.getProofDocuments().toString());
        tutorProfile.setCertificatesDetail(request.getCertificatesDetail().toString());

        return tutorProfileMapper.toResponse(tutorProfile);
    }
}
