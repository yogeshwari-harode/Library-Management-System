package Mypackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class User_SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        String query = "INSERT INTO usersignup (email, username, password) VALUES (?, ?, ?)";

        try (PrintWriter out = res.getWriter(); Connection con = DriverManager.getConnection(url, uid, pwd); PreparedStatement ps = con.prepareStatement(query)) {
            String email = req.getParameter("email");
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, password);

            int n = ps.executeUpdate();
            if (n == 1) {
                res.sendRedirect("SuccessfulUserSignup.html");
            } else {
                res.sendRedirect("InvalidUserLogin.html");
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e);
        }
    }
}
