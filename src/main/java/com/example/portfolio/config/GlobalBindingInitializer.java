package com.example.portfolio.config;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Automatically trims all incoming String request parameters and converts empty or whitespace-only
 * strings into null values across all controllers. This ensures that unpopulated form inputs
 * are stored as null rather than empty strings (""), preventing ghost buttons or empty badges.
 */
@ControllerAdvice
public class GlobalBindingInitializer {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
