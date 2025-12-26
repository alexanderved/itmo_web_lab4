package web.db.dto;

import jakarta.persistence.*;
import web.domain.Point;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "points")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_hit", discriminatorType = DiscriminatorType.INTEGER)
public class PointDTO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "x", nullable = false, columnDefinition = "NUMERIC")
    private BigDecimal x;

    @Column(name = "y", nullable = false, columnDefinition = "NUMERIC")
    private BigDecimal y;

    @Column(name = "rs", nullable = false)
    private BigDecimal[] rs;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;

    public PointDTO() {

    }

    public PointDTO(BigDecimal x, BigDecimal y,
                    BigDecimal[] rs, UserDTO user) {
        this.x = x;
        this.y = y;
        this.rs = rs;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal[] getRs() {
        return rs;
    }

    public UserDTO getUser() {
        return user;
    }

    public Point toDomain() {
        return new Point(x, y, rs, false);
    }
}
