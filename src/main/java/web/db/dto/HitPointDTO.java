package web.db.dto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import web.domain.Point;

@Entity
@DiscriminatorValue("1")
public class HitPointDTO extends PointDTO {
    public HitPointDTO() {

    }

    public HitPointDTO(Point p, UserDTO user) {
        super(p.x(), p.y(), p.rs(), user);
    }

    @Override
    public Point toDomain() {
        return new Point(getX(), getY(), getRs(), true);
    }
}
