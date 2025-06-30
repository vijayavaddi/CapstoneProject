package com.hcltech.doctorpatient.dao.service;

//import com.hcltech.doctorpatient.dto.UserDTO;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
//import com.hcltech.doctorpatient.model.Role;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.UserRepository;
//import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
//import java.util.stream.Collectors;

@Service
public class UserDao {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public UserDao(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(User updatedUser, UUID uid) {

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uid));
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setMobile(updatedUser.getMobile());
        //user.setRole(Role.valueOf(userDto.getRole().toUpperCase());
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id " + id));

    }
}