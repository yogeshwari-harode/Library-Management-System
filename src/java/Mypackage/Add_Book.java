package Mypackage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = 16177215) // 16MB
public class Add_Book extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        // Retrieve the form data
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        long isbn = Long.parseLong(request.getParameter("isbn"));
        double price = Double.parseDouble(request.getParameter("price"));
        String category = request.getParameter("category");

        // Handle the cover image
        Part coverPart = request.getPart("cover");
        InputStream coverStream = coverPart.getInputStream();

        // Handle the PDF file
        Part pdfPart = request.getPart("pdf");
        InputStream pdfStream = pdfPart.getInputStream();

        // Database connection parameters
        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";

        String query = "INSERT INTO book (title, author, isbn, price, category, bookcover, pdf_file) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, uid, pwd);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setLong(3, isbn);
            ps.setDouble(4, price);
            ps.setString(5, category);
            ps.setBinaryStream(6, coverStream, (int) coverPart.getSize());
            ps.setBinaryStream(7, pdfStream, (int) pdfPart.getSize());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                response.sendRedirect("AddBook_Success.html");
            }

        } catch (Exception e) {
            response.sendRedirect("SomethingWentWrong.html");
        }
    }
}


