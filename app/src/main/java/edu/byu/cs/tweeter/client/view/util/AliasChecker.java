package edu.byu.cs.tweeter.client.view.util;

import android.widget.EditText;

public class AliasChecker {
    private EditText editText;
    private final int MAX_NUM_CHARS = 15;

    public AliasChecker(EditText editText) {
        this.editText = editText;
    }

    /**
     * Checks for the validity of the current alias
     * @return boolean if the alias is valid
     */
    public boolean isValid() {
        String alias = editText.getText().toString();
        boolean hasIllegalChars = hasIllegalCharacters(alias);
        boolean isTooLong = isTooLong(alias);
        boolean doesNotHaveAtSymbol = !hasAtSymbol(alias);
        if(hasIllegalChars || isTooLong || doesNotHaveAtSymbol) {
            return false;
        }
        return true;
    }

    private boolean hasIllegalCharacters(String alias) {
        if(
                alias.contains("!") || alias.contains("#") || alias.contains("$") ||
                alias.contains("%") || alias.contains("^") || alias.contains("&") ||
                alias.contains("*") || alias.contains("(") || alias.contains(")") ||
                alias.contains("+") || alias.contains("-") || alias.contains("=") ||
                alias.contains("\'") || alias.contains("\"") || alias.contains("[") ||
                alias.contains("]") || alias.contains("{") || alias.contains("}") ||
                alias.contains("|") || alias.contains("<") || alias.contains(">") ||
                alias.contains("?") || alias.contains("/") || alias.contains("\\") ||
                alias.contains(",") || alias.contains(".") || alias.contains("~") ||
                alias.contains("`")
        ) {return true;}
        return false;
    }

    private boolean isTooLong(String alias) {
        if(alias.length() > MAX_NUM_CHARS) {
            return true;
        } return false;
    }

    private boolean hasAtSymbol(String alias) {
        char atSymbol = '@';
        char firstChar = alias.charAt(0);

        if(atSymbol == firstChar) {
            return true;
        }
        return false;
    }
}
