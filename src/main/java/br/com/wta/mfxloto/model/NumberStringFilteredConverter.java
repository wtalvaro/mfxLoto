/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.model;

import java.text.ParsePosition;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author w_tel
 */
public class NumberStringFilteredConverter extends NumberStringConverter {
    // Note, if needed you can add in appropriate constructors 
    // here to set locale, pattern matching or an explicit
    // type of NumberFormat.
    // 
    // For more information on format control, see 
    //    the NumberStringConverter constructors
    //    DecimalFormat class 
    //    NumberFormat static methods for examples.
    // This solution can instead extend other NumberStringConverters if needed
    //    e.g. CurrencyStringConverter or PercentageStringConverter.

    public UnaryOperator<TextFormatter.Change> getFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = getNumberFormat().parse(newText, parsePosition);
            if (object == null || parsePosition.getIndex() < newText.length()) {
                return null;
            } else {
                return change;
            }
        };
    }
}
