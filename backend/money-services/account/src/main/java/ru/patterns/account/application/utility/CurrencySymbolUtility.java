package ru.patterns.account.application.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.shared.constants.CurrencyConstants;

import java.util.Map;

@UtilityClass
public class CurrencySymbolUtility {

    private final Map<Integer, String> CURRENCY_SYMBOLS = Map.of(
            643, "₽",
            840, "$",
            156, "¥",
            398, "₸",
            0, "₿"
    );

    public String getCurrencySymbol(Integer id) {
        if (id == null) {
            return CURRENCY_SYMBOLS.get(CurrencyConstants.BASE_CURRENCY_ID);
        }

        return CURRENCY_SYMBOLS.get(id);
    }

    public boolean hasCurrency(Integer id) {
        return CURRENCY_SYMBOLS.containsKey(id);
    }
}
