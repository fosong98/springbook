package springbook.user.test.learningtest.jdk.jaxb;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;


import springbook.user.sqlservice.jaxb.*;

public class JaxbTest {
    @Test
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(
                ClassLoader.getSystemResourceAsStream("sqlmap.xml")
        );

        List<SqlType> sqlList = sqlmap.getSql();

        assertEquals(3, sqlList.size());
        assertEquals("add", sqlList.get(0).getKey());
        assertEquals("insert", sqlList.get(0).getValue());
        assertEquals("get", sqlList.get(1).getKey());
        assertEquals("select", sqlList.get(1).getValue());
        assertEquals("delete", sqlList.get(2).getKey());
        assertEquals("delete", sqlList.get(2).getValue());

    }
}
