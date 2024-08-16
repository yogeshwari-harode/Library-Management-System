package Mypackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class Edit_Book extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        String query = "UPDATE book SET title = ?, author = ?, price = ?, category = ?, bookcover = ?, pdf_file = ? WHERE isbn = ?";

        try (PrintWriter out = res.getWriter(); 
             Connection con = DriverManager.getConnection(url, uid, pwd); 
             PreparedStatement ps = con.prepareStatement(query)) {

            long isbn = Long.parseLong(req.getParameter("isbn"));
            String title = req.getParameter("title");
            String author = req.getParameter("author");
            double price = Double.parseDouble(req.getParameter("price"));
            String category = req.getParameter("category");

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setDouble(3, price);
            ps.setString(4, category);

            // Handle cover image
            Part coverPart = req.getPart("cover");
            InputStream coverInputStream = null;
            if (coverPart != null && coverPart.getSize() > 0) {
                coverInputStream = coverPart.getInputStream();
            }

            if (coverInputStream != null) {
                ps.setBlob(5, coverInputStream);
            } else {
                ps.setNull(5, java.sql.Types.BLOB);
            }

            // Handle PDF file
            Part pdfPart = req.getPart("pdf");
            InputStream pdfInputStream = null;
            if (pdfPart != null && pdfPart.getSize() > 0) {
                pdfInputStream = pdfPart.getInputStream();
            }

            if (pdfInputStream != null) {
                ps.setBlob(6, pdfInputStream);
            } else {
                ps.setNull(6, java.sql.Types.BLOB);
            }

            ps.setLong(7, isbn);

            int n = ps.executeUpdate();
            if (n > 0) {
                res.sendRedirect("EditBook_Success.html");
            } else {
                res.sendRedirect("BookNotFound.html");
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e);
        }
    }
}
