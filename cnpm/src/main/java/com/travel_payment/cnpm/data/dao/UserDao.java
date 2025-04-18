package com.travel_payment.cnpm.data.dao;

import com.travel_payment.cnpm.dto.request.user.UserUdReq;
import com.travel_payment.cnpm.dto.response.UserDTO;
import com.travel_payment.cnpm.entities.core.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public User getUser(Integer id){
        User user = entityManager.find(User.class, id);
        return user;
    }

    @Transactional
    public UserDTO getProfile(String username) throws SQLDataException {
        try {
            String sql = """
                    SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, 
                    GROUP_CONCAT(r.name) as roles_name from users as u 
                    JOIN authorization as a on a.user_id = u.id 
                    JOIN roles as r on a.role_id = r.id 
                    where u.user_name = :username 
                    group by u.id
                    """;
            Object[] results = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("username", username)
                    .getSingleResult();
            return new UserDTO((String) results[0], (String) results[1], (String) results[2],
                    (String) results[3], results[4].toString(),results[5].toString(), results[6].toString(),
                    Arrays.asList(results[7].toString().split(",")));
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public List<UserDTO> getAllUser() throws SQLDataException {
        try{
            String sql = """
                    SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, 
                    GROUP_CONCAT(r.name) as roles_name from users as u 
                    JOIN authorization as a on a.user_id = u.id 
                    JOIN roles as r on a.role_id = r.id 
                    LEFT JOIN
                        (SELECT DISTINCT user_id FROM authorization a2
                         JOIN roles r2 ON a2.role_id = r2.id
                         WHERE r2.name in ('ADMIN')) AS admin_users ON u.id = admin_users.user_id
                    where u.is_active = true and u.is_blocked = false and admin_users.user_id is null
                    group by u.id
                    """;
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .getResultList();
            return results.stream()
                    .map(result -> new UserDTO(result[0].toString(), result[1].toString(), result[2].toString(),
                            result[3].toString(), result[4].toString(), result[5].toString(), result[6].toString(),
                            Arrays.asList(result[7].toString().split(",")))).collect(Collectors.toList());
        }
        catch (Exception exception){
            throw new SQLDataException(exception.getMessage());
        }
    }

    @Transactional
    public List<UserDTO> getAllManager() throws SQLDataException {
        try{
            String sql = """
                    SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, 
                    GROUP_CONCAT(r.name) as roles_name from users as u 
                    JOIN authorization as a on a.user_id = u.id 
                    JOIN roles as r on a.role_id = r.id 
                    left join
                    	(select distinct user_id from authorization as a2
                        join roles as r2 on a2.role_id = r2.id
                        where r2.name = 'ADMIN') as admin_acc on u.id = admin_acc.user_id
                    where u.is_active = true and u.is_blocked = false and r.name = 'MANAGER' and admin_acc.user_id is null
                    group by u.id
                    """;
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .getResultList();
            return results.stream()
                    .map(result -> new UserDTO(result[0].toString(), result[1].toString(), result[2].toString(),
                            result[3].toString(), result[4].toString(), result[5].toString(), result[6].toString(),
                            Arrays.asList(result[7].toString().split(",")))).collect(Collectors.toList());
        }
        catch (Exception exception){
            throw new SQLDataException(exception.getMessage());
        }
    }

    @Transactional
    public List<UserDTO> getAllBlogger() throws SQLDataException {
        try{
            String sql = """
                    SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, 
                    GROUP_CONCAT(r.name) as roles_name from users as u 
                    JOIN authorization as a on a.user_id = u.id 
                    JOIN roles as r on a.role_id = r.id 
                    left join
                    	(select distinct user_id from authorization as a2
                        join roles as r2 on a2.role_id = r2.id
                        where r2.name = 'ADMIN') as admin_acc on u.id = admin_acc.user_id
                    where u.is_active = true and u.is_blocked = false and r.name = 'BLOGGER' and admin_acc.user_id is null
                    group by u.id
                    """;
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .getResultList();
            return results.stream()
                    .map(result -> new UserDTO(result[0].toString(), result[1].toString(), result[2].toString(),
                            result[3].toString(), result[4].toString(), result[5].toString(), result[6].toString(),
                            Arrays.asList(result[7].toString().split(",")))).collect(Collectors.toList());
        }
        catch (Exception exception){
            throw new SQLDataException(exception.getMessage());
        }
    }

    @Transactional
    public List<UserDTO> getAllUserBlocked() throws SQLDataException {
        try{
            String sql = """
                    SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, 
                    GROUP_CONCAT(r.name) as roles_name from users as u 
                    JOIN authorization as a on a.user_id = u.id 
                    JOIN roles as r on a.role_id = r.id 
                    left join
                    	(select distinct user_id from authorization as a2
                        join roles as r2 on a2.role_id = r2.id
                        where r2.name = 'ADMIN') as admin_acc on u.id = admin_acc.user_id
                    where u.is_active = true and u.is_blocked = true and admin_acc.user_id is null
                    group by u.id
                    """;
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .getResultList();
            return results.stream()
                    .map(result -> new UserDTO(result[0].toString(), result[1].toString(), result[2].toString(),
                            result[3].toString(), result[4].toString(), result[5].toString(), result[6].toString(),
                            Arrays.asList(result[7].toString().split(",")))).collect(Collectors.toList());
        }
        catch (Exception exception){
            throw new SQLDataException(exception.getMessage());
        }
    }

    @Transactional
    public void updateUser(UserUdReq request, Integer userId) throws SQLDataException {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String sql = """
                    UPDATE users as u set u.full_name = :fullName, u.phone_number = :phoneNumber, u.dob = :dob 
                    where u.id = :userId
                    """;
            entityManager.createNativeQuery(sql)
                    .setParameter("fullName", request.getFullName())
                    .setParameter("phoneNumber", request.getPhoneNumber())
                    .setParameter("dob", LocalDate.parse(request.getDob(), formatter))
                    .setParameter("userId", userId)
                    .executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteUser(Integer id) throws SQLDataException {
        try{
            String sql = "DELETE FROM users WHERE id = :userId";
            entityManager.createNativeQuery(sql)
                    .setParameter("userId", id)
                    .executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void blockUser(String username, Boolean isBlocked) throws SQLDataException {
        try {
            String sql = """
                    update users set is_blocked = :isBlocked where user_name = :userName
                    """;
            entityManager.createNativeQuery(sql).setParameter("isBlocked", isBlocked)
                    .setParameter("userName", username)
                    .executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public List<String> getRolesUser(Integer id) throws SQLDataException {
        try{
            String sql = """
                    SELECT r.name 
                    FROM roles AS r 
                    JOIN authorization AS a ON r.id = a.role_id 
                    JOIN users AS u ON u.id = a.user_id 
                    WHERE u.id = :userId
                    """;
            return entityManager.createNativeQuery(sql)
                    .setParameter("userId", id)
                    .getResultList();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }
}
