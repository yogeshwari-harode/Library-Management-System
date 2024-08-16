package Mypackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Download_Book_PDF extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String isbn = request.getParameter("isbn"); // ISBN is now retrieved from the hidden field

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        String query = "SELECT pdf_file FROM book WHERE isbn = ?"; // Assuming there's a column for the PDF

        try (Connection con = DriverManager.getConnection(url, uid, pwd);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                InputStream pdfStream = rs.getBinaryStream("pdf_file");
                if (pdfStream != null) {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment;filename=book_" + isbn + ".pdf");

                    OutputStream out = response.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;

                    while ((bytesRead = pdfStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    out.flush();
                    out.close();
                } else {
                    response.setContentType("text/html");
                    response.getWriter().println("<h3>No PDF available for this book.</h3>");
                }
            }

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }

        // Redirect back to the cart page
        response.sendRedirect("UserCart.html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet for downloading book PDFs";
    }
}
