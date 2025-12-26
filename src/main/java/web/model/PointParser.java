//package web.model;
//
//import web.domain.UncheckedPoint;
//import web.exceptions.PointParserException;
//
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.text.ParseException;
//import java.util.List;
//import java.util.stream.Stream;
//
//public class PointParser {
//    private static final List<BigDecimal> SUPPORTED_X =
//            Stream.iterate(-2.0f, seed -> seed + 0.5f)
//                    .limit(9)
//                    .map(BigDecimal::valueOf)
//                    .toList();
//    private static final BigDecimal Y_LOWER_BOUND = BigDecimal.valueOf(-3.0f);
//    private static final BigDecimal Y_UPPER_BOUND = BigDecimal.valueOf(5.0f);
//    private static final List<BigDecimal> SUPPORTED_R =
//            Stream.iterate(-2.0f, seed -> seed + 0.5f)
//                    .limit(9)
//                    .map(BigDecimal::valueOf)
//                    .toList();
//
//    private static BigDecimal parseFloat(String value) throws ParseException {
//        if (value.contains(".")) {
//            DecimalFormat decimalFormat = new DecimalFormat();
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setDecimalSeparator('.');
//            decimalFormat.setParseBigDecimal(true);
//            decimalFormat.setDecimalFormatSymbols(symbols);
//
//            return (BigDecimal) decimalFormat.parse(value);
//        }
//
//        DecimalFormat decimalFormat = new DecimalFormat();
//        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//        symbols.setDecimalSeparator(',');
//        decimalFormat.setParseBigDecimal(true);
//        decimalFormat.setDecimalFormatSymbols(symbols);
//
//        return (BigDecimal) decimalFormat.parse(value);
//    }
//
//    private static boolean isInBounds(BigDecimal x, BigDecimal y, BigDecimal r) {
//        return SUPPORTED_X.stream().anyMatch(e -> x.compareTo(e) == 0)
//                && y.compareTo(Y_LOWER_BOUND) >= 0 && y.compareTo(Y_UPPER_BOUND) <= 0
//                && SUPPORTED_R.stream().anyMatch(e -> r.compareTo(e) == 0);
//    }
//
//    public static UncheckedPoint parsePoint(String xStr, String yStr,
//                                            String rStr, boolean needsBoundCheck)
//            throws PointParserException {
//        if (xStr == null || yStr == null || rStr == null) {
//            throw new PointParserException("Отсутствующие параметры");
//        }
//
//        if (xStr.isBlank() || yStr.isBlank() || rStr.isBlank()) {
//            throw new PointParserException("Пустые параметры");
//        }
//
//        try {
//            BigDecimal x = parseFloat(xStr);
//            BigDecimal y = parseFloat(yStr);
//            BigDecimal r = parseFloat(rStr);
//
//            if (needsBoundCheck && !isInBounds(x, y, r)) {
//                throw new PointParserException("Значения чисел вне границ");
//            }
//
//            return new UncheckedPoint(x, y, r);
//        } catch (NumberFormatException | ParseException e) {
//            throw new PointParserException("Неверный формат числа", e);
//        }
//    }
//}