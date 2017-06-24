package betrag;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmountFactoryQueryBuilder;
import javax.money.MonetaryRounding;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

/**
 * 
 * @author Thomas Nill
 * 
 * Geldbeträge
 *
 */

public class Geld {
    private static final CurrencyUnit euro;
    private static final MonetaryAmountFactory<?> factory;
    private static final MonetaryRounding round;
    private static final MonetaryAmountFormat germanFormat;

    private Geld() {
        super();
    }

    static {
        euro = Monetary.getCurrency("EUR");
        round = Monetary.getRounding(euro);
        factory = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder
                .of().setFixedScale(true).setPrecision(10).setMaxScale(2)
                .build());
        germanFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
    }

    public static String format(MonetaryAmount amount) {
        return germanFormat.format(amount);
    }

    public static MonetaryAmount round(MonetaryAmount amount) {
        return round.apply(amount);
    }

    public static MonetaryAmount createAmount(long l) {
        return factory.setCurrency(euro.getCurrencyCode()).setNumber(l)
                .create();
    }

    public static MonetaryAmount createAmount(Number l) {
        return factory.setCurrency(euro.getCurrencyCode()).setNumber(l)
                .create();
    }

    public static MonetaryAmount createAmount(double l) {
        return round.apply(factory.setCurrency(euro.getCurrencyCode())
                .setNumber(l).create());
    }

    public static MonetaryAmount percentAmount(long percent, double l) {
        return round.apply(factory.setCurrency(euro.getCurrencyCode())
                .setNumber(((percent) / 100.0) * l).create());
    }
    
    public static MonetaryAmount percentAmount(MonetaryAmount g,double percent) {
        return round.apply(g.multiply(percent));
    }
    
    public static boolean absolutGrößer(MonetaryAmount a,MonetaryAmount b) {
        return a.abs().isGreaterThan(b.abs());
    }
    
    public static MonetaryAmount getNull() {
        return createAmount(0);
    }
}
