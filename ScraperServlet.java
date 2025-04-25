import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ScrapeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("url");
        String[] options = request.getParameterValues("options");


        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) {
            visitCount = 1;
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Web Scraping Results</title></head><body>");
        out.println("<h1>Web Scraping Results</h1>");
        out.println("<p>You have visited this page " + visitCount + " times.</p>");

        if (url != null && !url.isEmpty()) {
            try {
                out.println("<h2>Results for " + url + "</h2>");

                if (options != null) {
                    for (String option : options) {
                        switch (option) {
                            case "title":
                                String title = WebScraper.getTitle(url);
                                out.println("<p><strong>Title:</strong> " + title + "</p>");
                                break;
                            case "links":
                                List<String> links = WebScraper.getLinks(url);
                                out.println("<ul>");
                                for (String link : links) {
                                    out.println("<li><a href='" + link + "'>" + link + "</a></li>");
                                }
                                out.println("</ul>");
                                break;
                            case "news":
                                List<WebScraper.NewsItem> news = WebScraper.scrapeNews(url);
                                Gson gson = new Gson();
                                String json = gson.toJson(news);
                                out.println("<pre>" + json + "</pre>");
                                break;
                        }
                    }
                } else {
                    out.println("<p>No options selected.</p>");
                }
            } catch (IOException e) {
                out.println("<p>Error: " + e.getMessage() + "</p>");
            }
        } else {
            out.println("<p>Please enter a URL.</p>");
        }

        out.println("</body></html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.html");
    }
}