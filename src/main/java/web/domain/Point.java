package web.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public record Point(BigDecimal x, BigDecimal y,
                    BigDecimal[] rs, boolean isHit)
        implements Serializable {

}
