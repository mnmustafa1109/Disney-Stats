package untitled;

import java.sql.* ;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    private static int getMonthValue(String month) {
        return switch (month.toLowerCase()) {
            case "january" -> 1;
            case "february" -> 2;
            case "march" -> 3;
            case "april" -> 4;
            case "may" -> 5;
            case "june" -> 6;
            case "july" -> 7;
            case "august" -> 8;
            case "september" -> 9;
            case "october" -> 10;
            case "november" -> 11;
            case "december" -> 12;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }
    public static String[] splitWords(String str) {
        // Split the string using commas
        String[] words = str.split(",");
        
        // Trim leading and trailing whitespace from each word
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
        }
        return words;
    }

    public static void main(String[] args) {
        CSVReader csvReader = null;


        int director_ent_id = 1;
        int actor_ent_id = 1;
        int genre_ent_id = 1;
        int country_ent_id = 1;

        Connection myconn = null;
        String user ="mnmustafa1109";
        String pass="Lover@1109";
        String insertEntertainment =  "INSERT INTO entertainment (show_id, rating , date_added, release_year) VALUES ( ? , ? , ?, ?)";
        String insertENT_DESC =  "INSERT INTO entertainment_info (show_id, title, description,duration) VALUES ( ? , ? , ?, ?)";
        String insertDirector=  "INSERT INTO director (director_name) VALUES ( ? )";
        String insertD_ent=  "INSERT INTO entertainment_director (show_id, director_id) VALUES ( ? , ? )";
        String insertActor=  "INSERT INTO actor ( actor_name) VALUES ( ? )";
        String insertA_ent =  "INSERT INTO entertainment_actor (show_id, actor_id) VALUES ( ? , ? )";
        String insertCountry =  "INSERT INTO country ( country_name) VALUES ( ? )";
        String insertProduction_ent =  "INSERT INTO entertainment_country (show_id, country_id) VALUES ( ? , ? )";
        String insertGenre = "INSERT INTO genre (genre_name) VALUES ( ? )";
        String insertG_ent=  "INSERT INTO entertainment_genre (show_id, genre_id) VALUES ( ? , ? )";

        try {
            myconn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/test", user, pass);}
        catch(Exception exc){
            exc.printStackTrace();
            System.out.println("Error connecting to database");
        }

        try {
            csvReader = new CSVReader(new FileReader("src/main/resources/disney_plus_titles.csv"));
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String show_id = line[0];
                String title = line [2];
                String [] director_names= splitWords(line[3]);
                String [] actor_name= splitWords(line[4]);
                String [] country_names = splitWords(line[5]);

                String inputDate = line[6];
                Pattern pattern = Pattern.compile("([A-Za-z]+)\\s+(\\d{1,2}),\\s+(\\d{4})");
                Matcher matcher = pattern.matcher(inputDate);
                String date_added = null; // month date , year
                if (matcher.matches()) {
                    String month = matcher.group(1);
                    int dayOfMonth = Integer.parseInt(matcher.group(2));
                    int year = Integer.parseInt(matcher.group(3));
                    LocalDate date = LocalDate.of(year, getMonthValue(month), dayOfMonth);
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    date_added = outputFormatter.format(date);
                }

                String release_year = line[7];
                release_year = release_year.trim();
                String rating = line[8];
                String duration = line[9];
                String [] genre_name= splitWords(line[10]);
                String description = line[11];

                System.out.println("show_id: " + show_id + " type: " + line[1] + " title: " + title + " director: " + line[3] + " cast: " + line[4] + " country: " + line[5] + " date_added: " + date_added + " release_year: " + release_year + " rating: " + rating + " duration: " + line[9] + " listed_in: " + line[10] + " description: " + description + "\n\n\n");
                System.out.println("show_id: " + show_id + "\n\n\n");

                try{
                    assert myconn != null;
                    PreparedStatement pstmt = myconn.prepareStatement(insertEntertainment);
                    pstmt.setString(1, show_id);
                    pstmt.setString(2, rating);
                    pstmt.setString(3, date_added);
                    pstmt.setString(4, release_year);
                    pstmt.executeUpdate();

                }
                catch(SQLException e) {
                    System.out.println("Error inserting into table");
                    e.printStackTrace();
                }

                try {
                    PreparedStatement pstmt = myconn.prepareStatement(insertENT_DESC);
                    pstmt.setString(1, show_id);
                    pstmt.setString(2, title);
                    pstmt.setString(3, description);
                    pstmt.setString(4, duration);
                    pstmt.executeUpdate();

                }
                catch   (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("show_id: " + show_id + " title: " + title + " description: " + description +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String directorName : director_names) {
                        PreparedStatement pstmt = myconn.prepareStatement(insertDirector);
                        pstmt.setString(1, directorName);
                        String Check = "SELECT * FROM director WHERE director_name = ?";
                        PreparedStatement pstmt2 = myconn.prepareStatement(Check);
                        pstmt2.setString(1, directorName);
                        ResultSet rs = pstmt2.executeQuery();
                        if (!rs.next()) {
                            pstmt.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("director_num: " + director_ent_id + " director_name: " + director_names[0] +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String directorName : director_names) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertD_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT director_id FROM director WHERE director_name = ?";
                        PreparedStatement prepareStatement = myconn.prepareStatement(Check);
                        prepareStatement.setString(1, directorName);
                        ResultSet rs = prepareStatement.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("director_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("director_num: " + director_ent_id + " show_id: " + show_id +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String actorName : actor_name) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertActor);
                        preparedStatement.setString(1, actorName);
                        String Check = "SELECT * FROM actor WHERE actor_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, actorName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("actor_num: " + actor_ent_id + " actor_name: " + actor_name[0] +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String actorName : actor_name) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertA_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT actor_id FROM actor WHERE actor_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, actorName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("actor_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("actor_num: " + actor_ent_id + " show_id: " + show_id +"\n\n\n");
                    e.printStackTrace();
                }


                try {
                    for (String countryName : country_names) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertCountry);
                        preparedStatement.setString(1, countryName);
                        String Check = "SELECT * FROM country WHERE country_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, countryName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("country_num: " + country_ent_id + " country_name: " + country_names[0] +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String countryName : country_names) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertProduction_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT country_id FROM country WHERE country_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, countryName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("country_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("country_num: " + country_ent_id + " show_id: " + show_id +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String genreName : genre_name) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertGenre);
                        preparedStatement.setString(1, genreName);
                        String Check = "SELECT * FROM genre WHERE genre_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, genreName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("genre_num: " + genre_ent_id + " genre_name: " + genre_name[0] +"\n\n\n");
                    e.printStackTrace();
                }

                try {
                    for (String genreName : genre_name) {
                        PreparedStatement preparedStatement = myconn.prepareStatement(insertG_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT genre_id FROM genre WHERE genre_name = ?";
                        PreparedStatement preparedStatement1 = myconn.prepareStatement(Check);
                        preparedStatement1.setString(1, genreName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("genre_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error inserting into table");
                    System.out.println("genre_num: " + genre_ent_id + " show_id: " + show_id +"\n\n\n");
                    e.printStackTrace();
                }
               
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}