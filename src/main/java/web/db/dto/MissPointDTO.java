package web.db.dto;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import web.domain.Point;

@Entity
@DiscriminatorValue("0")
public class MissPointDTO extends PointDTO {
    public MissPointDTO() {

    }

    public MissPointDTO(Point p, UserDTO user) {
        super(p.x(), p.y(), p.rs(), user);
    }
}