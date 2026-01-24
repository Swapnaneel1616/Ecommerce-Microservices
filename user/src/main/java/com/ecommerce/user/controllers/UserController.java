package com.ecommerce.user.controllers;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;

//    private static Logger logger = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        return new ResponseEntity<>(userService.fetchAllUsers() ,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUsers(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>("User is created" , HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable String id){
        log.info("Request Received for users : {}" , id);
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id , @RequestBody UserRequest updateUserRequest){
        boolean updated = userService.updateUser(id , updateUserRequest);
        if(updated)
            return new ResponseEntity<>("USER UPDATED SUCCESFULLY " , HttpStatus.OK);
        return ResponseEntity.notFound().build();
    }

}
