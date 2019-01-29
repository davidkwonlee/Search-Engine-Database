/*This file sends accepts search parameters, sends calls to the database and retrieves database results
    based on the search parameters
*/
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.StringTokenizer;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

public class searchServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Search Results</title></head>");
        out.println("<body>");
        out.println("<center><h1>Search Results</h1></center>");
        
        out.println("<table align = 'center'>");
        
        String retrieved_query = request.getParameter("search_query");
        int wordCount = 0;
        
        boolean word = false;
        
        int endOfLine = retrieved_query.length() - 1;
        
        for (int i = 0; i < retrieved_query.length(); i++) {
            if (Character.isLetter(retrieved_query.charAt(i)) && i != endOfLine) {
                word = true;
            } else if (!Character.isLetter(retrieved_query.charAt(i)) && word) {
                wordCount++;
                word = false;
            } else if (Character.isLetter(retrieved_query.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        
        ArrayList<String> final_list_to_print = new ArrayList<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon= DriverManager.getConnection("jdbc:mysql://localhost:3306/cs121_database?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "123");
            String query = "";
            if (wordCount == 0) {
                out.println("<tr><th align = 'center'>Search Query Can't be empty.</th></tr>");
                out.println("</table>");
                out.println("</body>");
                out.println("</html>");
            }
            else if (wordCount == 1) {
                query = "select * from results where results.query_key = ?";
                PreparedStatement preparedStatement = dbcon.prepareStatement(query);
                preparedStatement.setString(1, retrieved_query.toLowerCase().trim());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    String url_1 = resultSet.getString("url_1");
                    String url_2 = resultSet.getString("url_2");
                    String url_3 = resultSet.getString("url_3");
                    String url_4 = resultSet.getString("url_4");
                    String url_5 = resultSet.getString("url_5");
                    String url_6 = resultSet.getString("url_6");
                    String url_7 = resultSet.getString("url_7");
                    String url_8 = resultSet.getString("url_8");
                    String url_9 = resultSet.getString("url_9");
                    String url_10 = resultSet.getString("url_10");
                    
                    out.println("<tr><th align = 'left'>Search#1:    </th><td align = 'left'>" + url_1 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#2:    </th><td align = 'left'>" + url_2 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#3:    </th><td align = 'left'>" + url_3 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#4:    </th><td align = 'left'>" + url_4 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#5:    </th><td align = 'left'>" + url_5 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#6:    </th><td align = 'left'>" + url_6 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#7:    </th><td align = 'left'>" + url_7 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#8:    </th><td align = 'left'>" + url_8 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#9:    </th><td align = 'left'>" + url_9 + "</td></tr>");
                    out.println("<tr><th align = 'left'>Search#10:    </th><td align = 'left'>" + url_10 + "</td></tr>");
                    out.println("</table>");
                    out.println("</body>");
                    out.println("</html>");
                    preparedStatement.close();
                }
                else if(!resultSet.next()){
                    out.println("<tr><td align = 'center'>Search Retrieved No Results</td></tr>");
                }
                
            }else if (wordCount > 1) {
                out.println("<tr><th align = 'center'>The Search Query word count is    " + wordCount + "</th></tr>");
                
                Map<Double, List<String>> tfidf_map = new HashMap<Double, List<String>>();
                
                //STEP 1: execute query with first key word (i.e. "Computer")
                StringTokenizer st = new StringTokenizer(retrieved_query);
                while (st.hasMoreTokens()) {
                    String current_token = st.nextToken();
                    //out.println("<tr><th>Words to search: " + current_token + "</th></tr>");
                    query = "select * from results where results.query_key = ?";
                    PreparedStatement preparedStatement = dbcon.prepareStatement(query);
                    preparedStatement.setString(1, current_token.toLowerCase().trim());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    
                    if(resultSet.next()) {
                        //STEP 2: retrieve all URLs, for URL_1, URL_2, URL_3, URL_4, URL_5, URL_6, URL_7, URL_8, URL_9, URL_10
                        String url_1 = resultSet.getString("url_1");
                        String url_2 = resultSet.getString("url_2");
                        String url_3 = resultSet.getString("url_3");
                        String url_4 = resultSet.getString("url_4");
                        String url_5 = resultSet.getString("url_5");
                        String url_6 = resultSet.getString("url_6");
                        String url_7 = resultSet.getString("url_7");
                        String url_8 = resultSet.getString("url_8");
                        String url_9 = resultSet.getString("url_9");
                        String url_10 = resultSet.getString("url_10");
                        
                        List<String> urlSet = new ArrayList<String>();
                        urlSet.add(url_1);
                        urlSet.add(url_2);
                        urlSet.add(url_3);
                        urlSet.add(url_4);
                        urlSet.add(url_5);
                        urlSet.add(url_6);
                        urlSet.add(url_7);
                        urlSet.add(url_8);
                        urlSet.add(url_9);
                        urlSet.add(url_10);
                        
                        //For loop for each url
                        for(String retrieved_url : urlSet) {
                            String query_2 = "select url_query_idf.idf from url_query_idf where url_query_idf.query_key = ? and url_query_idf.url = ?";
                            PreparedStatement preparedStatement_2 = dbcon.prepareStatement(query_2);
                            preparedStatement_2.setString(1, current_token.toLowerCase().trim());
                            preparedStatement_2.setString(2, retrieved_url);
                            ResultSet resultSet_2 = preparedStatement_2.executeQuery();
                            
                            if(resultSet_2.next()) {
                                //STEP 3: make a dictionary/List and attach -> (TF-IDF, QUERY_KEY, URL)
                                
                                List<String> url_query_set = new ArrayList<String>();
                                
                                String idf_retrieved = resultSet_2.getString("idf");
                                
                                Double idf_values = Double.parseDouble(idf_retrieved);
                                
                                //out.println("<tr><th>QUERY: " + current_token.toLowerCase().trim() + " URL: "  + retrieved_url + " IDF: " + idf_values +"</th></tr>");
                                url_query_set.add(current_token.toLowerCase().trim());
                                url_query_set.add(retrieved_url);
                                //Maybe add a try except statement here?
                                tfidf_map.put(idf_values, url_query_set);
                                
                            }else if(!resultSet_2.next()) {
                                out.println("NO");
                            }
                            
                            preparedStatement_2.close();
                        }
                        preparedStatement.close();
                        
                        //STEP 4: Sort by increasing to decreasing TF_IDF and print out the URLS
                        
                        //Do code here to print out results
                        Map<Double, List<String>> reverseSortedMap = new TreeMap<Double, List<String>>(Collections.reverseOrder());
                        reverseSortedMap.putAll(tfidf_map);
                        for (Map.Entry<Double, List<String>> entry : reverseSortedMap.entrySet()) {
                            //Double key = entry.getKey();
                            List<String> url_values = entry.getValue();
                            
                            //out.println("<tr><th> " + key + "->" + url_values.get(1) + "</th></tr>");
                            final_list_to_print.add(url_values.get(1));
                        }
                    }else if(!resultSet.next()){
                        out.println("<tr><th>Search Retrieved No Results</th></tr>");
                    }
                }
                //print out the final list of items
                for (int i =  1; i <= 10; i++) {
                    out.println("<tr><th align = 'left'>Search#" + i + ":    </th><td align = 'left'>"+ final_list_to_print.get(i-1) +  "</td></tr>");
                }
                out.println("</table>");
                out.println("</body>");
                out.println("</html>");
            }
            dbcon.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        out.println("</center>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}


