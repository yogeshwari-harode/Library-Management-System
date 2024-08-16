package Mypackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";

        String checkQuery = "SELECT email FROM signup WHERE email = ?";
        String insertQuery = "INSERT INTO signup (email, username, password) VALUES (?, ?, ?)";

        try (PrintWriter out = res.getWriter(); 
             Connection con = DriverManager.getConnection(url, uid, pwd)) {

            String email = req.getParameter("email");
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            // Check if the user already exists
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        res.sendRedirect("AdminAlreadyExist.html");
                        return;
                    }
                }
            }

            // If the user does not exist, add them to the database
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setString(1, email);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);

                int n = insertStmt.executeUpdate();
                if (n == 1) {
                    res.sendRedirect("SuccessfulSignup.html");
                } else {
                    res.sendRedirect("InvalidLogin.html");
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e);
        }
    }
}
