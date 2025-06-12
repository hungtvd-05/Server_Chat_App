package com.app.service;

import com.app.connection.DatabaseConnection;
import com.app.dao.UserDAO;
import com.app.model.Model_Login;
import com.app.model.Model_Message;
import com.app.model.Model_Register;
import com.app.model.TestUserAccount;
import com.app.model.User;
import com.app.model.UserAccount;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ServiceUser {

    private UserDAO userDAO = new UserDAO();

    public Model_Message register(Model_Register data) {
        Model_Message message = new Model_Message();
//        System.out.println("a2");
        try {
            User user = userDAO.findByUsernameOrEmail(data.getUserName(), data.getMail()).join(); 
            if (user != null) {
                message.setAction(false);
                message.setMessage("Người dùng đã tồn tại");
            } else {
                message.setAction(true);
                data.setPassword(BCrypt.hashpw(data.getPassword(), BCrypt.gensalt()));
                
                User newUser = new User(data.getUserName(), data.getPassword(), data.getMail());
                
                UserAccount userAccount = new UserAccount(newUser, data.getFullName(), data.getUserName(), data.getMail(), data.getPhone(), "", "", false);
                
                newUser.setUserAccount(userAccount);
                
                User saveUser = userDAO.save(newUser).join();
                userAccount.setUserId(saveUser.getUserId());
                message.setMessage("Đăng ký thành công");
//                System.out.println(new TestUserAccount(saveUser.getUserId(), saveUser.getUsername(), saveUser.getUserAccount().getFullName(), saveUser.getMail(), saveUser.getUserAccount().getPhone(), saveUser.getUserAccount().getGender(), true, saveUser.getUserAccount().getImage()));
                message.setData(new TestUserAccount(saveUser.getUserId(), saveUser.getUsername(), saveUser.getUserAccount().getFullName(), saveUser.getMail(), saveUser.getUserAccount().getPhone(), saveUser.getUserAccount().getGender(), false, saveUser.getUserAccount().getImage()));
            }
        } catch (Exception e) {
            message.setAction(false);
            message.setMessage("Lỗi máy chủ: " + e.getMessage());
        }
//        System.out.println(message.getMessage());
        return message;
    }
    
    public TestUserAccount login(Model_Login login) {
        User user = userDAO.findByUsernameOrEmail(login.getUsername(), login.getUsername()).join(); 
        if (user != null) {
//            System.out.println("check");
            if (BCrypt.checkpw(login.getPassword(), user.getPassword())) {
                return new TestUserAccount(user.getUserId(), user.getUsername(), user.getUserAccount().getFullName(), user.getMail(), user.getUserAccount().getPhone(), user.getUserAccount().getGender(), true, user.getUserAccount().getImage());
            }
        } else {
//            System.out.println(login.getUsername());
//            System.out.println("loi");
        }
        return null;
    }
    
    public List<TestUserAccount> getUser(long exitUser) {
        List<TestUserAccount> list = new ArrayList<>();
        try {
            list = userDAO.getActiveUserAccountsExcludingId(exitUser).join();
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }        
        return list;
    }
    
    public void updateStatus(boolean status, long userId) {
        userDAO.updateStatus(userId, status);
    }
    

    public ServiceUser() {
        DatabaseConnection.getInstance().getConnection();
    }
    
    public void updateAllStatus() {
        userDAO.updateAllStatus();
    }
}
