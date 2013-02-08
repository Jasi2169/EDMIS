package com.progdan.edmis.control.user;

import javax.servlet.http.*;

import com.progdan.edmis.model.user.User;

public class UserSessionListener implements HttpSessionListener {
    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent sessionEvent) {
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.getSession();
        User user = (User)session.getAttribute("User");
        if(user != null){
            UserWriter writer = new UserWriter();
            writer.logout(user);
        }
    }
}
