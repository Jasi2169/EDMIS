package com.progdan.edmis.control.user;

import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.error.user.UnknowUserException;

public class ActivateUserControl {
    private static Logger logger = Logger.getLogger(RegisterUserControl.class.
            getName());
    public void activate(String login) throws UnknowUserException {
        logger.debug(">>> Start of ActivateUserControl.activate()***");
        if (User.exists(login)) {
            UserWriter.activate(login);
            logger.info("Username " + login + " sucessfuly activated on the server.");
        } else {
            logger.warn("Username " + login + " unknow for this server.");
            logger.debug("<<< End of UserControl.ActivateUserControl.activate()***");
            throw new UnknowUserException();
        }
        logger.debug("<<< End of UserControl.ActivateUserControl.activate()***");
    }
}
