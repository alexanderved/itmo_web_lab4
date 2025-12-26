package web.model;

import web.domain.Point;
import web.domain.UncheckedPoint;

import java.math.BigDecimal;
import java.util.Arrays;

public class PointChecker {
    private static boolean isInRectangle(UncheckedPoint p) {
        return Arrays.stream(p.rs())
                .map((r) -> {
                    BigDecimal halfR = r.multiply(BigDecimal.valueOf(0.5));

                    if (r.compareTo(BigDecimal.ZERO) >= 0) {
                        return (p.x().compareTo(BigDecimal.ZERO) <= 0
                                && p.x().compareTo(r.negate()) >= 0
                                && p.y().compareTo(BigDecimal.ZERO) >= 0
                                && p.y().compareTo(halfR) <= 0);
                    } else {
                        return (p.x().compareTo(BigDecimal.ZERO) >= 0
                                && p.x().compareTo(r.negate()) <= 0
                                && p.y().compareTo(BigDecimal.ZERO) <= 0
                                && p.y().compareTo(halfR) >= 0);
                    }
                })
                .reduce(false, (res, v) -> res || v);
    }

    private static boolean isInCircle(UncheckedPoint p) {
        return Arrays.stream(p.rs())
                .map((r) -> {
                    BigDecimal sqrX = p.x().pow(2);
                    BigDecimal sqrY = p.y().pow(2);
                    BigDecimal sqrR = r.pow(2);

                    if (r.compareTo(BigDecimal.ZERO) >= 0) {
                        return (p.x().compareTo(BigDecimal.ZERO) >= 0
                                && p.y().compareTo(BigDecimal.ZERO) >= 0
                                && sqrX.add(sqrY).compareTo(sqrR) <= 0);
                    } else {
                        return (p.x().compareTo(BigDecimal.ZERO) <= 0
                                && p.y().compareTo(BigDecimal.ZERO) <= 0
                                && sqrX.add(sqrY).compareTo(sqrR) <= 0);
                    }
                })
                .reduce(false, (res, v) -> res || v);
    }

    private static boolean isInTriangle(UncheckedPoint p) {
        return Arrays.stream(p.rs())
                .map((r) -> {
                    BigDecimal halfR = r.multiply(BigDecimal.valueOf(0.5));

                    if (r.compareTo(BigDecimal.ZERO) >= 0) {
                        return (p.x().compareTo(BigDecimal.ZERO) <= 0
                                && p.y().compareTo(BigDecimal.ZERO) <= 0
                                && p.x().negate().subtract(r).compareTo(p.y()) <= 0);
                    } else {
                        return (p.x().compareTo(BigDecimal.ZERO) >= 0
                                && p.y().compareTo(BigDecimal.ZERO) >= 0
                                && p.x().subtract(r).compareTo(p.y()) >= 0);
                    }
                })
                .reduce(false, (res, v) -> res || v);
    }

    private static boolean isInside(UncheckedPoint p) {
        return isInRectangle(p) || isInCircle(p) || isInTriangle(p);
    }

    public static Point checkPoint(UncheckedPoint p) {
        return new Point(p.x(), p.y(), p.rs(), isInside(p));
    }
}
