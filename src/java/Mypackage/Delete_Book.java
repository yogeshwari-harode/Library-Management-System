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

public class Delete_Book extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        String query = "DELETE FROM book WHERE isbn = ?";

        try {
            PrintWriter out = res.getWriter();
            Connection con = DriverManager.getConnection(url, uid, pwd);
            PreparedStatement ps = con.prepareStatement(query);

            long isbn = Long.parseLong(req.getParameter("isbn"));

            ps.setLong(1, isbn);

            int n = ps.executeUpdate();
            if (n > 0) {
                res.sendRedirect("DeleteBook_Success.html");
            } else {
                res.sendRedirect("BookNotFound.html");
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e);
        }
    }
    
}
