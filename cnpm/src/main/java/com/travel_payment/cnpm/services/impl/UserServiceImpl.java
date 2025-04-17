package com.travel_payment.cnpm.services.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.travel_payment.cnpm.configurations.CustomUserDetailsService;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.configurations.JwtUtils;
import com.travel_payment.cnpm.contants.TravelConstants;
import com.travel_payment.cnpm.data.dao.AuthorizationDao;
import com.travel_payment.cnpm.data.dao.UserDao;
import com.travel_payment.cnpm.data.repository.UserRepository;
import com.travel_payment.cnpm.data.specification.UserSpecification;
import com.travel_payment.cnpm.dto.request.user.UserLoginReq;
import com.travel_payment.cnpm.dto.request.user.UserReq;
import com.travel_payment.cnpm.dto.request.user.UserUdReq;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.UserDTO;
import com.travel_payment.cnpm.entities.core.User;
import com.travel_payment.cnpm.enums.RoleEnum;
import com.travel_payment.cnpm.exceptions.common.AlreadyException;
import com.travel_payment.cnpm.exceptions.common.InvalidException;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.exceptions.user.EmailSenderException;
import com.travel_payment.cnpm.exceptions.user.PermissionException;
import com.travel_payment.cnpm.services.UserService;
import com.travel_payment.cnpm.utils.EmailUtilService;
import com.travel_payment.cnpm.utils.OtpUtils;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLDataException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.travel_payment.cnpm.contants.TravelConstants.User.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final OtpUtils otpUtils;

    private final EmailUtilService emailUtilService;

    private final AuthorizationDao authorizationDao;

    private final UserDao userDao;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtFilter jwtFilter;
    private final AmazonS3 amazonS3;
    private final CustomUserDetailsService userDetailsService;

    @Value("${cloud.aws.s3.bucketAvatar}")
    private String bucket;

    /**
     * Đăng ký người dùng với role mặc định là user
     * Thực hiện xác thực với otp email
     * @param userReq
     * @return
     * @throws SQLDataException
     */
    @Override
    public ResponseEntity<ApiResponse> signup(UserReq userReq) throws SQLDataException {
        Optional<User> user = userRepository.findByUsername(userReq.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(userReq.getEmail());
        if(user.isPresent()){
            if(user.get().getIsActive()){
                throw new AlreadyException(USER_NAME_EXISTS);
            }
            else {
                userDao.deleteUser(user.get().getId());
            }
        }
        if(userByEmail.isPresent()){
            if(userByEmail.get().getIsActive()){
                throw new AlreadyException(EMAIL_EXISTS);
            }
            else {
                userDao.deleteUser(userByEmail.get().getId());
            }
        }
        try{
            if (!userReq.getPassword().equals(userReq.getRetypePassword())){
                return TravelResponse.generateResponse
                        (null, "Password does not match", HttpStatus.BAD_REQUEST);
            }
            String OTP = otpUtils.generateOtp();
            emailUtilService.sentOtpEmail(userReq.getEmail(), OTP);
            // pw encoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userReq.setPassword(passwordEncoder.encode(userReq.getPassword()));
            // new user
            User newUser = modelMapper.map(userReq, User.class);
            newUser.setOTP(OTP);
            newUser.setOtpGenerateTime(LocalDateTime.now());
            newUser.setCreatedAt(LocalDate.now());
            newUser.setUpdatedAt(LocalDate.now());
            newUser.setIsActive(false);
            newUser.setIsBlocked(false);
            userRepository.save(newUser);
            // return
            return TravelResponse.generateResponse(null, "OTP sent to " + userReq.getEmail()
                    + ", please verify to complete registration", HttpStatus.OK);
        }
        catch (MessagingException ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            throw new EmailSenderException(TravelConstants.EMAIL_SENDER_ERROR);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verify account với otp
     * @param request
     * @return
     * @throws SQLDataException
     */
    @Override
    public String verifyAccount(Map<String, String> request) throws SQLDataException {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );

        if(request.get("otp").equals(user.getOTP()) && Duration.between(user.getOtpGenerateTime(),
                LocalDateTime.now()).getSeconds() < (2* 60)){
            user.setIsActive(true);
            userRepository.save(user);
            authorizationDao.insertIntoAuthorization(user.getId(), RoleEnum.USER.getId());
            return "Account verified successfully";
        }
        else if(!request.get("otp").equals(user.getOTP())){
            return "OTP is invalid";
        }
        return "OTP expired";
    }

    /**
     * Gửi lại mã otp nếu xác thực thất bại
     * @param request
     * @return
     */
    @Override
    public String regenerateOtp(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                ()-> new NotFoundException(USER_NOT_FOUND)
        );
        String otp = otpUtils.generateOtp();
        try {
            emailUtilService.sentOtpEmail(request.get("email"), otp);
        }
        catch (MessagingException ex){
            throw new EmailSenderException(TravelConstants.EMAIL_SENDER_ERROR);
        }
        user.setOTP(otp);
        user.setOtpGenerateTime(LocalDateTime.now());
        userRepository.save(user);
        return "OTP regenerate successfully";
    }

    /**
     * Đăng nhập tài khoản, compare với password đã mã hóa và generate token
     * Lưu token vào redis cache
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> login(UserLoginReq request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Boolean isPasswordCorrect = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isPasswordCorrect){
            throw new InvalidException("Invalid password");
        }
        if(!user.getIsActive()){
            throw new PermissionException("Account is not active, please create account one more time");
        }
        if (user.getIsBlocked()){
            throw new PermissionException("Account is blocked");
        }
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            if(authentication.isAuthenticated()){
                String username = customUserDetailsService.getUserDetails().getUsername();
                String token = jwtUtils.generateToken(username, createClaim());
                return new ResponseEntity<>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
            }
            return TravelResponse.generateResponse(null, "Authentication failed", HttpStatus.FORBIDDEN);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> refreshToken(String oldToken) {
        try{
            String username = jwtUtils.getUserNameFromTokenExpired(oldToken);
            if(username == null){
                return TravelResponse.generateResponse(null, "Invalid token", HttpStatus.UNAUTHORIZED);
            }
            String newToken = jwtUtils.generateToken(username, createClaim());
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("token", newToken);
            return TravelResponse.generateResponse(tokenResponse, "Refresh token successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, "Token refresh failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy thông tin cá nhân của người đang đăng nhập
     * cache profile
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getProfile() {
        try{
            UserDTO userDTO = userDao.getProfile(jwtFilter.getCurrentUsername()); // thuc hien truy van moi neu cache rong
            return TravelResponse.generateResponse
                    (userDTO, "Get profile successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload/Update ảnh đại diện của người dùng, lưu trữ trên cloud
     * Thực hiện xóa file cũ nếu là action update
     * @param file
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> uploadAvatarProfile(MultipartFile file) {
        User user = getUser();
        try {
            if(file == null ){
                return TravelResponse.generateResponse(null, "Not found file", HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() == 0){
                return TravelResponse.generateResponse(null, "File is empty", HttpStatus.BAD_REQUEST);
            }
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return TravelResponse.generateResponse(null, "File is not an image", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return TravelResponse.generateResponse(null, "File is too large", HttpStatus.PAYLOAD_TOO_LARGE);
            }
            String oldFileName = user.getAvatar();
            String fileName = uploadToS3(file);
            user.setAvatar(fileName);
            userRepository.save(user);
            if (oldFileName != null) {
                deleteToS3(oldFileName);
            }
            return TravelResponse.generateResponse(fileName, "Avatar upload successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy avatar của người dùng để hiển thị cho profile
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> viewAvatar() {
        User user = getUser();
        try{
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            GeneratePresignedUrlRequest preSignedUrlRequest = new GeneratePresignedUrlRequest
                    (bucket, user.getAvatar())
                    .withExpiration(expiration)
                    .withMethod(HttpMethod.GET);
            URL url = amazonS3.generatePresignedUrl(preSignedUrlRequest);
            return TravelResponse.generateResponse(url, "Avatar view successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy tất cả các người dùng trong hệ thống để quản lý trừ admin, blog, manager
     * cache all user
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getAllUser() {
        return getAllObject("User");
    }
    /**
     * Lấy tất cả các manager trong hệ thống để quản lý truwf admin
     * cache all manager
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getAllManager() {
        return getAllObject("Manager");
    }
    /**
     * Lấy tất cả các người dùng trong hệ thống để quản lý tru admin
     * cache all blogger
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getAllBlogger() {
        return getAllObject("Blogger");
    }

    @Override
    public ResponseEntity<ApiResponse> getAllUserBlocked() {
        try{
            if (jwtFilter.isAdmin()){
                List<UserDTO> userDTOS = userDao.getAllUserBlocked();
                return TravelResponse.generateResponse(userDTOS, "Get all user blocked successfully", HttpStatus.OK);

            }
            return TravelResponse.generateResponse(null, TravelConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<ApiResponse> searchUser(String search, String order, String by, String isBlocked) {
        try {
            Sort.Direction direction = order != null && order.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            String sortBy = by != null ? by : "createdAt";
            Sort sort = Sort.by(direction, sortBy);
            log.info("search {}, order {}, by {}", search, order, by);

            Boolean isBlockedParsed = Boolean.parseBoolean(isBlocked);
            Specification<User> spec = Specification.where(UserSpecification.hasSearchText(search))
                    .and(UserSpecification.hasNotAdmin())
                    .and(UserSpecification.hasBlocked(isBlockedParsed));

            List<User> users = userRepository.findAll(spec, sort);

            List<UserDTO> usersResult = users.stream().map(u -> new UserDTO(u)).toList();
            return TravelResponse.generateResponse(usersResult, "Search result", HttpStatus.OK);

        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Quên mật khẩu, thực hiện xác thực qua email
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> forgotPassword(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );
        String otp = otpUtils.generateOtp();
        try{
            emailUtilService.sentResetPasswordEmail(request.get("email"), otp);
            user.setOTP(otp);
            user.setOtpGenerateTime(LocalDateTime.now());
            userRepository.save(user);
            return TravelResponse.generateResponse
                    (null, "OTP sent to " + request.get("email") + ", please verify to reset password", HttpStatus.OK);
        } catch (MessagingException e) {
            throw new EmailSenderException(TravelConstants.EMAIL_SENDER_ERROR);
        }
    }

    /**
     *sau khi xac thuc otp cho lay lai mat khau, chuyen den nhap mat khau moi
     * @param request
     * @return
     */
    @Override
    public String verifyForgotPassword(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );

        if(request.get("otp").equals(user.getOTP()) && Duration.between(user.getOtpGenerateTime(),
                LocalDateTime.now()).getSeconds() < (2* 60)){
            return "OTP verified successfully";
        }
        else if(!request.get("otp").equals(user.getOTP())){
            return "OTP is invalid";
        }
        return "OTP expired";
    }

    /**
     * Đặt lại mật khẩu khi verify thành công
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> setPassword(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElseThrow(
                ()-> new NotFoundException(USER_NOT_FOUND)
        );
        if (!request.get("password").equals(request.get("retypePassword"))) {
            return TravelResponse.generateResponse(null, "Password does not match", HttpStatus.BAD_REQUEST);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.get("password")));
        userRepository.save(user);
        return TravelResponse.generateResponse(null, "Password reset successfully", HttpStatus.OK);

    }

    /**
     * Thay đổi mật khẩu người dùng
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> changePassword(Map<String, String> request) {
        User user = getUser();
        try{
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if(passwordEncoder.matches(request.get("oldPassword"), user.getPassword())){
                if (!request.get("newPassword").equals(request.get("retypeNewPassword"))) {
                    return TravelResponse.generateResponse(null, "Password does not match", HttpStatus.BAD_REQUEST);
                }
                if (request.get("oldPassword").equals(request.get("newPassword"))) {
                    return TravelResponse.generateResponse(null, "New password and old password must not be the same!", HttpStatus.BAD_REQUEST);
                }
                user.setPassword(passwordEncoder.encode(request.get("newPassword")));
                userRepository.save(user);
                return TravelResponse.generateResponse(null, "Password change successfully", HttpStatus.OK);
            }
            else{
                return TravelResponse.generateResponse(null, "Old password is incorrect", HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Chỉnh sửa profile
     * clear cache profile
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> updateProfile(UserUdReq request) {
        User user = getUser();
        try{
            String cacheKey = "viewProfile:user:" + jwtFilter.getCurrentUsername();
            userDao.updateUser(request, user.getId());
            return TravelResponse.generateResponse(null, "Profile updated successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Api check token từ client
     * @return
     */
    @Override
    public ResponseEntity<?> checkToken() {
        return TravelResponse.generateResponse(null, "Check success", HttpStatus.OK);
    }

    /**
     * Block user
     * @param request: username, isBlocked
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> blockUser(Map<String, String> request) {
        try {
            if (jwtFilter.isAdmin()){
                if (request.get("username").equals(jwtFilter.getCurrentUsername())) {
                    return TravelResponse.generateResponse(null, "Cannot block admin", HttpStatus.BAD_REQUEST);
                }
                userDao.blockUser(request.get("username"), Boolean.parseBoolean(request.get("isBlocked")));
                if (request.get("isBlocked").equalsIgnoreCase("true")) {
                    return TravelResponse.generateResponse(null, "User blocked", HttpStatus.OK);
                }
                else {
                    return TravelResponse.generateResponse(null, "User unblocked", HttpStatus.OK);
                }
            }
            return TravelResponse.generateResponse(null, TravelConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Tạo claim để generate token, được gọi ở hàm login
     * @return
     * @throws SQLDataException
     */
    private Map<String, Object> createClaim() throws SQLDataException {
        Map<String, Object> claim = new HashMap<>();
        List<String> roles = userDao.getRolesUser(customUserDetailsService.getUserDetails().getId());
        claim.put("name", customUserDetailsService.getUserDetails().getFullName());
        claim.put("role", roles);
        claim.put("dob", customUserDetailsService.getUserDetails().getDob().toString());
        claim.put("email", customUserDetailsService.getUserDetails().getEmail());
        claim.put("phoneNumber", customUserDetailsService.getUserDetails().getPhoneNumber());
        return claim;
    }

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        return fileName;
    }
    private void deleteToS3(String oldFileName) {
        amazonS3.deleteObject(bucket, oldFileName);
    }

    private User getUser(){
        return userDetailsService.getUserDetails();
    }

    /**
     * Hàm để lấy all đối tượng (manager, user, blogger) tùy vào tham số truyền vào
     * @param objectName
     * @return
     */
    private ResponseEntity<ApiResponse> getAllObject(String objectName){
        try{
            if(jwtFilter.isAdmin()){
                switch (objectName){
                    case "User":
                        List<UserDTO> users = userDao.getAllUser();
                        return TravelResponse.generateResponse(users, "Get all" + objectName + "successfully", HttpStatus.OK);

                    case "Manager":
                        List<UserDTO> manager = userDao.getAllManager();
                        return TravelResponse.generateResponse(manager, "Get all" + objectName + "successfully", HttpStatus.OK);

                    case "Blogger":
                        List<UserDTO> blogger = userDao.getAllBlogger();
                        return TravelResponse.generateResponse(blogger, "Get all" + objectName + "successfully", HttpStatus.OK);

                    default:
                        return TravelResponse.generateResponse(null, "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

                }

            }
            else{
                return TravelResponse.generateResponse(null, TravelConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
