package com.progdan.edmis.control.user;

import com.progdan.logengine.*;
import com.progdan.edmis.error.user.ExistingUserException;
import com.progdan.edmis.model.user.User;

public class UpdateUserController {
    private static Logger logger = Logger.getLogger(RegisterUserControl.class.
            getName());
    public void update(User user, String passwd) throws
            ExistingUserException {
        logger.debug(">>> Start of UserControl.signup()***");
        user.setPassword(passwd);
        UserWriter write = new UserWriter();
        write.updatePassword(user);
        logger.info("Username " + user.getLogin() + " sucessfully updated.");
        logger.debug("<<< End of UserControl.signup()***");
    }
}
