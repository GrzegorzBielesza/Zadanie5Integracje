package org.example;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class Placeholder {
    private String placeholderText;
    private JTextField textField;
    private boolean showingPlaceholder;

    public Placeholder(JTextField textField, String placeholderText) {
        this.textField = textField;
        this.placeholderText = placeholderText;
        this.showingPlaceholder = true;

        // Set the initial placeholder text
        showPlaceholder();

        // Add a focus listener to hide the placeholder when the text field is focused
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                hidePlaceholder();
            }

            @Override
            public void focusLost(FocusEvent e) {
                showPlaceholder();
            }
        });
    }

    private void showPlaceholder() {
        if (textField.getText().isEmpty() && !showingPlaceholder) {
            textField.setText(placeholderText);
            textField.setCaretPosition(0);
            showingPlaceholder = true;
        }
    }

    private void hidePlaceholder() {
        if (showingPlaceholder) {
            textField.setText("");
            showingPlaceholder = false;
        }
    }

    public void changeAlpha(float alpha) {
        if (showingPlaceholder) {
            Color color = textField.getForeground();
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int alphaValue = Math.round(alpha * 255);
            Color newColor = new Color(red, green, blue, alphaValue);
            textField.setForeground(newColor);
        }
    }
}
