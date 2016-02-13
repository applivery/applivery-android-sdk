package com.applivery.applvsdklib.matchers;

import java.util.Date;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsDateEqualTo extends BaseMatcher<Date> {

    private final Date expectedValue;

    public IsDateEqualTo(Date expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean matches(Object item) {
        boolean areEquals = Math.abs(expectedValue.getTime() - ((Date) item).getTime()) < 10000;
        return areEquals;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expectedValue.toString());
    }

    @Factory
    public static Matcher<Date>
    isDateEqualTo(Date t) {
        return new IsDateEqualTo(t);
    }
}
