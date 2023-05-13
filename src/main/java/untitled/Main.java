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
        CSVReader csvReader ;

        Connection connection = null;
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
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/test", user, pass);}
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

                System.out.println("show_id: " + show_id + "\n\n\n");

                try{
                    assert connection != null;
                    PreparedStatement preparedStatement = connection.prepareStatement(insertEntertainment);
                    preparedStatement.setString(1, show_id);
                    preparedStatement.setString(2, rating);
                    preparedStatement.setString(3, date_added);
                    preparedStatement.setString(4, release_year);
                    preparedStatement.executeUpdate();

                }
                catch(SQLException e) {
                    System.out.println("Error inserting into table");
                    e.printStackTrace();
                }

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(insertENT_DESC);
                    preparedStatement.setString(1, show_id);
                    preparedStatement.setString(2, title);
                    preparedStatement.setString(3, description);
                    preparedStatement.setString(4, duration);
                    preparedStatement.executeUpdate();

                }
                catch   (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String directorName : director_names) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertDirector);
                        preparedStatement.setString(1, directorName);
                        String Check = "SELECT * FROM director WHERE director_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, directorName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String directorName : director_names) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertD_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT director_id FROM director WHERE director_name = ?";
                        PreparedStatement prepareStatement = connection.prepareStatement(Check);
                        prepareStatement.setString(1, directorName);
                        ResultSet rs = prepareStatement.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("director_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String actorName : actor_name) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertActor);
                        preparedStatement.setString(1, actorName);
                        String Check = "SELECT * FROM actor WHERE actor_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, actorName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String actorName : actor_name) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertA_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT actor_id FROM actor WHERE actor_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, actorName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("actor_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }


                try {
                    for (String countryName : country_names) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertCountry);
                        preparedStatement.setString(1, countryName);
                        String Check = "SELECT * FROM country WHERE country_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, countryName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String countryName : country_names) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertProduction_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT country_id FROM country WHERE country_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, countryName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("country_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String genreName : genre_name) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertGenre);
                        preparedStatement.setString(1, genreName);
                        String Check = "SELECT * FROM genre WHERE genre_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, genreName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (!rs.next()) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    for (String genreName : genre_name) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertG_ent);
                        preparedStatement.setString(1, show_id);
                        String Check = "SELECT genre_id FROM genre WHERE genre_name = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(Check);
                        preparedStatement1.setString(1, genreName);
                        ResultSet rs = preparedStatement1.executeQuery();
                        if (rs.next()) {
                            preparedStatement.setInt(2, rs.getInt("genre_id"));
                        }
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
               
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}