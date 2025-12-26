package web.db.postgresql;

import jakarta.ejb.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import web.db.HibernateCfg;
import web.db.dto.HitPointDTO;
import web.db.dto.MissPointDTO;
import web.db.dto.PointDTO;
import web.db.dto.UserDTO;

@Singleton(name = "hibernateCfg")
public class PostgreSQLHibernateCfg implements HibernateCfg {
    private final SessionFactory sessionFactory;

    public PostgreSQLHibernateCfg() {
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect");

        cfg.addAnnotatedClass(UserDTO.class);
        cfg.addAnnotatedClass(PointDTO.class);
        cfg.addAnnotatedClass(HitPointDTO.class);
        cfg.addAnnotatedClass(MissPointDTO.class);

        cfg.configure();

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());

        sessionFactory = cfg.buildSessionFactory(builder.build());
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
