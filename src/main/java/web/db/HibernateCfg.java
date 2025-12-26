package web.db;

import jakarta.ejb.Local;
import org.hibernate.SessionFactory;

@Local
public interface HibernateCfg {
    SessionFactory getSessionFactory();
}
