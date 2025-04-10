package com.example.LiveChat.Repository;

import com.example.LiveChat.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    List<User> findUsersByPropertyId(Long propertyId);
    Optional<User> findByUsernameAndPropertyId(String username, Long propertyId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.property.id = :propertyId")
    boolean existsByUsernameAndPropertyId(@Param("username") String username, @Param("propertyId") Long propertyId);
}
