package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;
import java.sql.Connection;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    private int retrieveUserId(Principal principal){
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }
        System.out.println("Principal name: " + principal.getName());

        // get the currently logged in username
        String userName = principal.getName();

        // find database user by userId
        User user = userDao.getByUserName(userName);
        return user.getId();
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public Profile getProfile(Principal principal){
        int user_id = retrieveUserId(principal);
        return profileDao.getProfile(user_id);
    }


}
