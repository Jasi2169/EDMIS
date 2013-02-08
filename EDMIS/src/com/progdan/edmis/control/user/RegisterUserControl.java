package com.progdan.edmis.control.user;

import com.progdan.logengine.*;
import com.progdan.edmis.error.user.ExistingUserException;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.control.mail.SignupMailController;

public class RegisterUserControl {
    private static Logger logger = Logger.getLogger(RegisterUserControl.class.
            getName());
    private User user;
    public void signup(String login, String passwd, String email, String name) throws
            ExistingUserException {
        logger.debug(">>> Start of UserControl.signup()***");
        if (User.exists(login)) {
            logger.warn("Username " + login +
                        " already registered on this server.");
            throw new ExistingUserException();
        }
        user = new User(login, passwd, email, name);
        UserWriter write = new UserWriter();
        write.writeNew(user);
        logger.info("Username " + login + " sucessfully registered.");
        SignupMailController mail = SignupMailController.getInstance();
        mail.confirmSignup(email, login);
        logger.debug("<<< End of UserControl.signup()***");
    }
}
