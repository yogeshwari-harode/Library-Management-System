package Mypackage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddToCart extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        
        long isbn = Long.parseLong(request.getParameter("isbn"));
        String checkQuery = "SELECT * FROM book WHERE isbn = ? AND status = 'addToCart'";
        String updateQuery = "UPDATE book SET status = 'addToCart' WHERE isbn = ?";

        try (Connection con = DriverManager.getConnection(url, uid, pwd)) {
            
            // Check if the book is already in the cart
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setLong(1, isbn);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        response.sendRedirect("Book_Already_In_Cart.html");
                        return;
                    }
                }
            }

            // If the book is not in the cart, update its status
            try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                updateStmt.setLong(1, isbn);
                int n = updateStmt.executeUpdate();
                if (n == 1) {
                    response.sendRedirect("AddToCart_Success.html");
                } else {
                    response.sendRedirect("SomethingWentWrong.html");
                }
            }

        } catch (SQLException e) {
            System.out.println("Exception: " + e);
            response.sendRedirect("SomethingWentWrong.html");
        }
    }
}
