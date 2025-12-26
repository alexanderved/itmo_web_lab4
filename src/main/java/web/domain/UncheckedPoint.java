package web.domain;

import java.math.BigDecimal;

public record UncheckedPoint(BigDecimal x, BigDecimal y, BigDecimal[] rs) {

}
