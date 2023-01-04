package com.example.JdbcExample;

import java.sql.*;

public class JDBCExample {
    static final String DB_URL = "jdbc:postgresql://localhost/people";
    static final String USER = "postgres";
    static final String PASS = "5403";
    static final String QUERY = "SELECT * FROM Person";
    public static void main(String[] args) throws SQLException {
        //updatePerson(new Person(2,"Gosho","Ivanov"));
        //createPerson();
        //deletePerson(1);
       getPeople();
    }

    public static void createPerson() throws SQLException {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            connection.setAutoCommit(false);
            try(ResultSet rs = statement.executeQuery(QUERY)){
                rs.moveToInsertRow();
                rs.updateString("name", "Misho");
                rs.updateString("last_name", "Ivanov");
                rs.insertRow();
                connection.commit();
            }catch (SQLException e){
                connection.rollback();
                throw new SQLException(e);
            }
        }
    }
    public static void updatePerson(Person person) throws SQLException {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            connection.setAutoCommit(false);
            try (ResultSet rs = statement.executeQuery(QUERY + " where id = " + person.getId())) {
                if (rs.next()) {
                    rs.updateString("name", person.getName());
                    rs.updateString("last_name", person.getLastName());
                    rs.updateRow();
                }
            }catch (SQLException e){
                connection.rollback();
                throw new SQLException(e);
            }
            connection.commit();
        }

    }
    public static void deletePerson(int id) throws SQLException {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            try(ResultSet rs = statement.executeQuery(QUERY + " where id = " + id)){
                if (rs.next()) {
                    rs.deleteRow();
                }
            }
        }
    }
    public static void getPeople() throws SQLException {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs = statement.executeQuery(QUERY)) {
                while (rs.next()) {
                    System.out.print("ID: " + rs.getInt("id"));
                    System.out.print(", First: " + rs.getString("name"));
                    System.out.println(", Last: " + rs.getString("last_name"));
                }
            }
        }
    }
    public static Connection createConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static class Person{
        private int id;
        private String name;
        private String lastName;

        public Person() {
        }

        public Person(int id, String name, String lastName) {
            this.id = id;
            this.name = name;
            this.lastName = lastName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
