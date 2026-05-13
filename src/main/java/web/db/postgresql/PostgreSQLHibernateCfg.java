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

        String psql_url = System.getenv("POSTGRES_URL");
        String psql_user = System.getenv("POSTGRES_USER");
        String psql_password = System.getenv("POSTGRES_PASSWORD");

        cfg.setProperty("hibernate.connection.url", psql_url);
        cfg.setProperty("hibernate.connection.username", psql_user);
        cfg.setProperty("hibernate.connection.password", psql_password);

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
