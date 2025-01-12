/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAO.UserDao;

import java.io.IOException;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import tools.MD5;

/**
 *
 * @author User
 */
public class UserServlet extends HttpServlet {

    UserDao userDao = new UserDao();

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        String url = "index.jsp";
        String name = "";
        User user = new User();
        HttpSession session = request.getSession();
        switch (command) {
            case "insert":
//                    user.setHoten(request.getParameter("hoten"));
//                    user.setNgaysinh(Date.valueOf(request.getParameter("ngaysinh")));
//                    user.setSdt(request.getParameter("sdt"));
                String cmnd = request.getParameter("cmnd");
                Pattern regex = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                Matcher matcher = regex.matcher(cmnd);
                String uname = request.getParameter("uname");
                String password = request.getParameter("pass");
                String cfpassword = request.getParameter("copass");
                User getUser = new User();
                if (cmnd.equals("") || cmnd == null) {
                    request.setAttribute("error", "cmnd không được để null");
                    request.setAttribute("uname", uname);

                    RequestDispatcher rq = request.getRequestDispatcher(url);
                    rq.forward(request, response);
                }
                

                if (uname.equals("") || uname == null) {
                    request.setAttribute("error", "UserName không được để null");
                    request.setAttribute("cmnd", cmnd);
                    RequestDispatcher rq = request.getRequestDispatcher(url);
                    rq.forward(request, response);
                }
                Pattern regexUname = Pattern.compile("[$&+,:;=?@#|'<>.-^*()%!]");
                Matcher matcherUname = regexUname.matcher(uname);
                if (password.equals("") || password == null) {
                    request.setAttribute("error", "Pass không được để null");
                    request.setAttribute("cmnd", cmnd);
                    request.setAttribute("uname", uname);
                    RequestDispatcher rq = request.getRequestDispatcher(url);
                    rq.forward(request, response);
                }
//                Pattern regexPass = Pattern.compile("[$&+,:;=?@#|'<>.-^*()%!]");
//                Matcher matcherPass = regexPass.matcher(password);
//                if (matcherPass.find()) {
//                    request.setAttribute("error", "Password không được chứa ký tự đặc biệt ");
//                    request.setAttribute("uname", uname);
//
//                    RequestDispatcher rq = request.getRequestDispatcher(url);
//                    rq.forward(request, response);
//                }
                if (!cfpassword.equals(password)) {
                    request.setAttribute("error", "Confirm password is wrong");
                    request.setAttribute("cmnd", cmnd);
                    request.setAttribute("uname", uname);
                    RequestDispatcher rq = request.getRequestDispatcher(url);
                    rq.forward(request, response);
                }

                if (!cmnd.equals("") && cmnd != null && !password.equals("") && password != null && !uname.equals("") && uname != null && cfpassword.equals(password)) {
                    String pass = MD5.encryption(request.getParameter("pass"));
                    boolean checkcmnd = userDao.checkcmnd(cmnd);
                    if (checkcmnd == true) {
                        request.setAttribute("error", "cmnd đã tồn tại");
                        request.setAttribute("cmnd", cmnd);
                        request.setAttribute("uname", uname);
                        RequestDispatcher rq = request.getRequestDispatcher(url);
                        rq.forward(request, response);
                    } else {
                        user.setcmnd(cmnd);
                        user.setUsername(uname);
                        user.setPassword(pass);
                        user.setAdmin(2);
                        userDao.inseartUser(user);
                        session.setAttribute("user", user);
                        RequestDispatcher rq = request.getRequestDispatcher(url);
                        rq.forward(request, response);
                        break;
                    }
                }
            case "login":
                user = userDao.login(request.getParameter("login-cmnd"), MD5.encryption(request.getParameter("login-pass")));


                
         
                if (user != null) {
                    
                    session.setAttribute("user", user);

                    response.sendRedirect(url);
                } else {
                    request.setAttribute("error", "Wrong cmnd or password");
                    response.sendRedirect(url);
                }
                break;
            case "logout":
                session.setAttribute("user", null);
                response.sendRedirect("index.jsp");
//                case "update":
//                    user.setUserID(userDao.sizeUser());
//                    user.setHoten(request.getParameter("hoten"));
//                    user.setNgaysinh(Date.valueOf(request.getParameter("ngaysinh")));
//                    user.setcmnd(request.getParameter("cmnd"));
//                    user.setSdt(request.getParameter("sdt"));
//                    userDao.updateUser(user); 
//                    session.setAttribute("user",user);
//                    url = "/index.jsp";
                break;
            case "updatePass": {
                String url1 = "changePass.jsp";
                User user2 = new User();
                user2 = (User) session.getAttribute("user");
                String password1 = user2.getPassword();
                long id1 = user2.getUserID();
                System.out.println(user2.getUserID());
                String oldPass = request.getParameter("oldPass");
                String newPass = request.getParameter("newPass");
                String cfnewPass = request.getParameter("cfnewPass");
                String pass1 = MD5.encryption(oldPass);
                if (oldPass.equals("") || oldPass == null) {
                    request.setAttribute("error", "OldPass không được để null");
                   
                    RequestDispatcher rq = request.getRequestDispatcher(url1);
                    rq.forward(request, response);
                }
                if (!password1.equals(pass1)) {
                    request.setAttribute("error", "Sai mật khẩu!");
                    RequestDispatcher rq = request.getRequestDispatcher(url1);
                    rq.forward(request, response);
                }
                if (newPass.equals("") || newPass == null) {
                    request.setAttribute("error", "NewPass không được để null");
                   
                    RequestDispatcher rq = request.getRequestDispatcher(url1);
                    rq.forward(request, response);
                }
                if (!cfnewPass.equals(newPass)) {
                    request.setAttribute("error", "Xác nhận mật khẩu sai!");
                    RequestDispatcher rq = request.getRequestDispatcher(url1);
                    rq.forward(request, response);
                }

                if (password1.equals(pass1) && cfnewPass.equals(newPass)) {
                    String passnew = MD5.encryption(newPass);
                    userDao.updatePass(id1, passnew);
                    RequestDispatcher rq = request.getRequestDispatcher(url);
                    rq.forward(request, response);
                    break;
                }

            }
            case "update": {
                String newcmnd = request.getParameter("cmnd");
                String newfullname = request.getParameter("name");
                String newngaysinh = request.getParameter("dob");
                String newdantoc = request.getParameter("dantoc");
                String newtenngdamho = request.getParameter("ngdamho");
                String newgioitinh = request.getParameter("gioitinh");
                String newquequan = request.getParameter("quequan");
                String newbhxh = request.getParameter("bhxh");
                String newhogiadinhid = request.getParameter("hogiadinh");
//                String id = request.getParameter("userID");
//                long num = Long.parseLong(id);
                User user1 = new User();
                user1 = (User) session.getAttribute("user");
                System.out.println(user1.getUserID());
//                user1.setUserID(num);
                user1.setBhxh_id(newbhxh);
                user1.setDantoc(newdantoc); 
                user1.setDob(newngaysinh);
                user1.setcmnd(newcmnd);
                user1.setGioitinh(newgioitinh);
                user1.setHogiadinh_id(newhogiadinhid);
                user1.setHotennguoidamho(newtenngdamho);
                user1.setQuequan(newquequan);
                user1.setUsername(newfullname);
                                        
                long id = user1.getUserID();

//                userDao.updateUser(user1);
                UserDao userDao1 = new UserDao();
                try {
                    if (userDao1.updateUser(user1)) {
                        response.sendRedirect("index.jsp");
                    } else {
                        response.sendRedirect("error.jsp");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            
        }
    }

}
