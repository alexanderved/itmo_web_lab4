package web.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import web.domain.UncheckedPoint;

class PointCheckerTest {
    private static UncheckedPoint createPoint(double x, double y, double r) {
        return new UncheckedPoint(BigDecimal.valueOf(x), BigDecimal.valueOf(y), new BigDecimal[]{ BigDecimal.valueOf(r) });
    }

    @Test
    void testHitInRectangle() {
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(-0.5, 0.25, 1.0)).isHit());
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(-1.0, 0.5, 2.0)).isHit());

        Assertions.assertFalse(PointChecker.checkPoint(createPoint(-1.1, 0.25, 1.0)).isHit());
        Assertions.assertFalse(PointChecker.checkPoint(createPoint(-1.0, 0.6, 1.0)).isHit());
    }

    @Test
    void testHitInCircle() {
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(0.25, 0.25, 0.5)).isHit());
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(1.0, 1.5, 2.0)).isHit());

        Assertions.assertFalse(PointChecker.checkPoint(createPoint(2.1, 0.25, 1.0)).isHit());
        Assertions.assertFalse(PointChecker.checkPoint(createPoint(1.0, 0.6, 0.5)).isHit());
    }

    @Test
    void testHitInTriangle() {
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(-0.5, -0.25, 2.0)).isHit());
        Assertions.assertTrue(PointChecker.checkPoint(createPoint(-1.0, -0.5, 1.5)).isHit());

        Assertions.assertFalse(PointChecker.checkPoint(createPoint(-1.1, -0.25, 1.0)).isHit());
        Assertions.assertFalse(PointChecker.checkPoint(createPoint(-1.9, -0.2, 2.0)).isHit());
    }
}
