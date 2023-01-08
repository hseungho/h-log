import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

public class HLog {

    private static List<FixedPart> fixedParts;
    private static LinkedList<String> message;

    public static void hInfo(String inputMessage, Object... variables) {
        fixedParts = parse(inputMessage);

        message = new LinkedList<>(Arrays.asList(inputMessage.split("")));

        matchVariables(variables);

        format(message, fixedParts);

        String log = printLog(message);

        System.out.println(log);
    }

    private static List<Variable> integers;
    private static List<Variable> decimals;
    private static List<Variable> strings;
    private static List<Variable> booleans;

    private static void initVariableLists() {
        integers = new ArrayList<>();
        decimals = new ArrayList<>();
        strings = new ArrayList<>();
        booleans = new ArrayList<>();
    }

    private static void matchVariables(Object... variables) {
        initVariableLists();

        for(Object var : variables) {

            if(var instanceof Byte) {
                integers.add(new Variable(Byte.class, var));

            } else if (var instanceof Short) {
                integers.add(new Variable(Short.class, var));

            } else if (var instanceof Integer) {
                integers.add(new Variable(Integer.class, var));

            } else if (var instanceof Long) {
                integers.add(new Variable(Long.class, var));

            } else if (var instanceof Float) {
                decimals.add(new Variable(Float.class, var));

            } else if (var instanceof Double) {
                decimals.add(new Variable(Double.class, var));

            } else if (var instanceof Character) {
                strings.add(new Variable(Character.class, var));

            } else if (var instanceof String) {
                strings.add(new Variable(String.class, var));

            } else if (var instanceof Boolean) {
                booleans.add(new Variable(Boolean.class, var));

            } else {
                throw new RuntimeException();
            }

        }
    }

    private static void format(LinkedList<String> message, List<FixedPart> fixedParts) {
        int iCount = 0, dCount = 0, sCount = 0, bCount = 0;

        for(FixedPart fixedPart : fixedParts) {
            if(fixedPart.type == FixedPart.FixedType.INTEGER) {
                int start = fixedPart.start;
                message.set(start, integers.get(iCount++).var.toString());
            } else if(fixedPart.type == FixedPart.FixedType.DECIMAL) {
                int start = fixedPart.start;
                message.set(start, decimals.get(dCount++).var.toString());
            } else if(fixedPart.type == FixedPart.FixedType.STRING) {
                int start = fixedPart.start;
                message.set(start, strings.get(sCount++).var.toString());
            } else if(fixedPart.type == FixedPart.FixedType.BOOLEAN) {
                int start = fixedPart.start;
                message.set(start, booleans.get(bCount++).var.toString());
            }
        }

        for(int i = message.size(); i >= 0; i--) {
            for(FixedPart fixedPart : fixedParts) {
                if(i == fixedPart.end) {
                    message.remove(i);
                }
            }
        }
    }

    private static DateTimeFormatter hLogTimeFormatter;
    static {
        hLogTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(YEAR, 4).appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2).appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 2).appendLiteral(' ')
                .appendValue(HOUR_OF_DAY, 2).appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart().appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .appendFraction(NANO_OF_SECOND, 0, 3, true)
                .toFormatter();
    }

    private static String printLog(LinkedList<String> message) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder
                .append("[")
                .append(LocalDateTime.now().format(hLogTimeFormatter))
                .append("  ")
                .append("H-LOG")
                .append(" ")
                .append("INFO")
                .append("]")
                .append("  ---  ");

        message.forEach(logBuilder::append);

        return logBuilder.toString();
    }

    private static List<FixedPart> parse(String m) {
        List<FixedPart> parts = new ArrayList<>();

        int i = 0;
        int max = m.length();

        while(i < max) {
            int n = m.indexOf("-", i);
            if (n < 0) {
                break;
            } else {
                parts.add(new FixedPart(m, n));
                i = n+1;
            }
            if(i >= max) {
                System.err.println("error");
            }
        }

        return parts;
    }

    //////////////////////////
    // Private static class //
    //////////////////////////
    private static class Variable {
        Class<?> varType;
        Object var;
        Variable(Class<?> varType, Object var) {
            this.varType = varType;
            this.var = this.varType.cast(var);
        }
    }

    private static class FixedPart {
        private enum FixedType { INTEGER, DECIMAL, STRING, BOOLEAN }
        private final FixedType type;
        private final int start;
        private final int end;
        FixedPart(String o, int start) {
            this.start = start;
            this.end = start + 1;

            char c = o.charAt(end);
            this.type = switch (c) {
                case ParsingType.INTEGER -> FixedType.INTEGER;
                case ParsingType.DECIMAL -> FixedType.DECIMAL;
                case ParsingType.STRING -> FixedType.STRING;
                case ParsingType.BOOLEAN -> FixedType.BOOLEAN;
                default -> throw new RuntimeException(); //TODO 커스텀 예외 후 호출 메소드에서 캐치하여 error로 프린트하도록
            };
        }

        private static class ParsingType {
            static final char INTEGER = 'i';
            static final char DECIMAL = 'd';
            static final char STRING = 's';
            static final char BOOLEAN = 'b';
        }
    }

}
