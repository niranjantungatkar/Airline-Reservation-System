package edu.sjsu.utils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class ReservationIdGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        String prefix = "RAT";
        Connection connection = (Connection) session.connection();

        try {
            Statement statement=(Statement) connection.createStatement();

            ResultSet rs=statement.executeQuery("select count(orderNumber) as total from Reservation");
            
            if(rs.next())
            {
                int id=rs.getInt(1)+100;
                String generatedId = prefix + new Integer(id).toString();
                System.out.println("-------------------------------------------");
                System.out.println("Generated Id: " + generatedId);
                return generatedId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
	}
}
