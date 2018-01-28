import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by st.ivanov44 on 25/10/2017.
 */
public class StartServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException
    {
        String pollutionGrid = request.getParameter("pollutionGrid");
        ServletContext context = getServletContext();
        GraphNetwork.initialiseGraph(context, pollutionGrid);
        response.getWriter().write(GraphNetwork.getNodesWithPollution());
    }

}
