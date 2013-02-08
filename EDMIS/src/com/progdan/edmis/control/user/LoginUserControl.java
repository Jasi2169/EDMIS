package com.progdan.edmis.control.user;

import javax.servlet.http.*;
import java.util.*;
import com.progdan.logengine.*;
import com.progdan.edmis.model.user.User;
import com.progdan.edmis.error.user.*;
import com.progdan.edmis.error.license.LicenseException;

public class LoginUserControl {
    private static Logger logger = Logger.getLogger(LoginUserControl.class.
            getName());
    public void login(HttpSession session, String login, String passwd,
                      String addr) throws
            UnknowUserException, PasswdErrorException,
            InactiveUserAccountException, LicenseException {
        logger.debug(">>> Start of LoginUserControl.login()***");
        GregorianCalendar now = new GregorianCalendar();
            if (User.exists(login)) {
                UserReader read = new UserReader();
                UserWriter write = new UserWriter();
                User user = read.readUser(login);
                if (user.isAccountActive()) {
                    if (passwd.equals(user.getPassword())) {
                        write.login(user);
                        session.setAttribute("User", user);
                        logger.info("User " + login + " logged on the system.");
                    } else {
                        logger.warn("Password incorrect for the " + login +
                                    ", from the address: " +
                                    addr);
                        logger.debug("<<< End of LoginUserControl.login()***");
                        throw new PasswdErrorException();
                    }
                } else {
                    logger.warn("Username " + login +
                                " account is inactive on this server.");
                    logger.debug("<<< End of LoginUserControl.login()***");
                    throw new InactiveUserAccountException();
                }
            } else {
                logger.warn("Username " + login + " unknow for this server.");
                logger.debug("<<< End of LoginUserControl.login()***");
                throw new UnknowUserException();
            }
        logger.debug("<<< End of LoginUserControl.login()***");
    }
}
